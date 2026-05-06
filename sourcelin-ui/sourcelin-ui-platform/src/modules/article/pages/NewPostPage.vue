<script setup lang="ts">
import type { Component } from 'vue'
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { createArticle, getArticleDetail, updateArticle, type ArticleCreateUpdateDTO, type ArticleDetail as ArticleDetailPayload, type ArticleDetailTag as ArticleDetailTagPayload } from '@/modules/article/api/article.api'
import { getCategories, type CategoryOption } from '@/modules/article/api/category.api'
import { getTags, type TagOption } from '@/modules/article/api/tag.api'
import PostEditorForm from '@/modules/article/components/PostEditorForm.vue'
import HeroStatCard from '@/shared/components/business/HeroStatCard.vue'
import PageShell from '@/shared/components/layout/PageShell.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SSkeleton from '@/shared/components/ui/SSkeleton.vue'
import { useSMessage } from '@/shared/composables/useSMessage'
import { appIcons } from '@/shared/components/ui/icons'
import { getToken } from '@/shared/utils/auth'

interface SelectOption {
  label: string
  value: number
}

interface ArticleTag {
  id?: number
}

interface HeroStatTile {
  label: string
  value: string
  icon: Component
  hue: number
  ariaLabel: string
}

function toNumber(value: unknown, fallback = 0): number {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

function getPlainContentLength(content: string): number {
  return content
    .replace(/<style[\s\S]*?>[\s\S]*?<\/style>/gi, ' ')
    .replace(/<script[\s\S]*?>[\s\S]*?<\/script>/gi, ' ')
    .replace(/<[^>]+>/g, ' ')
    .replace(/&nbsp;/gi, ' ')
    .replace(/\s+/g, '')
    .length
}

function normalizeRichContent(content: string): string {
  const source = typeof content === 'string' ? content.trim() : ''
  if (!source) {
    return ''
  }

  const preNormalized = source
    .replace(/\\"/g, '"')
    .replace(/src\s*=\s*""([^"]+)""/gi, 'src="$1"')
    .replace(/href\s*=\s*""([^"]+)""/gi, 'href="$1"')
    .replace(/src\s*=\s*"([^"]*?)"\s*"([^"]*?)"/gi, 'src="$1$2"')
    .replace(/href\s*=\s*"([^"]*?)"\s*"([^"]*?)"/gi, 'href="$1$2"')

  const parser = new DOMParser()
  const documentNode = parser.parseFromString(`<div>${preNormalized}</div>`, 'text/html')
  const container = documentNode.body.firstElementChild as HTMLDivElement | null
  if (!container) {
    return preNormalized
  }

  documentNode.querySelectorAll('*').forEach((element) => {
    Array.from(element.attributes).forEach((attribute) => {
      const normalizedValue = attribute.value.replace(/\\"/g, '"').trim()
      element.setAttribute(attribute.name, normalizedValue)
    })
  })

  documentNode.querySelectorAll('img').forEach((image) => {
    const normalizedSrc = image.getAttribute('src')?.replace(/\\"/g, '').replace(/^"+|"+$/g, '').trim() || ''
    if (normalizedSrc) {
      image.setAttribute('src', normalizedSrc)
    } else {
      image.remove()
    }
  })

  documentNode.querySelectorAll('a').forEach((link) => {
    const normalizedHref = link.getAttribute('href')?.replace(/\\"/g, '').replace(/^"+|"+$/g, '').trim() || ''
    if (normalizedHref) {
      link.setAttribute('href', normalizedHref)
    } else {
      link.removeAttribute('href')
    }
  })

  const walker = documentNode.createTreeWalker(container, NodeFilter.SHOW_COMMENT)
  const commentNodes: Comment[] = []
  while (walker.nextNode()) {
    commentNodes.push(walker.currentNode as Comment)
  }
  commentNodes.forEach((comment) => comment.remove())

  return container.innerHTML
}

const route = useRoute()
const router = useRouter()
const message = useSMessage()

const isLoggedIn = ref(Boolean(getToken()))
const editingId = computed(() => {
  const raw = route.query.id
  const value = typeof raw === 'string' ? Number(raw) : Number(Array.isArray(raw) ? raw[0] : 0)
  return Number.isFinite(value) && value > 0 ? value : null
})
const isEditing = computed(() => editingId.value !== null)

const form = ref({
  title: '',
  summary: '',
  avatar: '',
  avatarFileId: null as number | null,
  externalAvatarUrl: '',
  content: '',
  categoryId: null as number | null,
  tagIds: [] as number[],
  isOriginal: 1
})
const categories = ref<SelectOption[]>([])
const tagOptions = ref<SelectOption[]>([])
const loading = ref(false)
const bootstrapLoading = ref(false)

const pageTitle = computed(() => (isEditing.value ? '编辑文章' : '发布文章'))
const headerSubtitle = computed(() =>
  isEditing.value
    ? '保存后将再次送审，通过后前台展示才会更新。'
    : '提交后将先审核，审核通过后文章才会公开可见。'
)
const submitText = computed(() => (isEditing.value ? '保存修改' : '提交发布'))

const guidePanelTitle = computed(() =>
  isEditing.value ? '继续完善这篇稿子' : '开始一篇新文章'
)

const guidePanelLede = computed(() =>
  isEditing.value
    ? '改动保存后仍会送审，通过后才会替换前台展示。'
    : '标题与正文为必填；封面、摘要与标签可按需补充。'
)

const heroStatTiles = computed((): HeroStatTile[] => {
  const cat = categories.value.length
  const tags = tagOptions.value.length
  const draftLabel = isEditing.value && editingId.value ? `#${editingId.value}` : '新建'
  const draftAria =
    isEditing.value && editingId.value
      ? `正在编辑文章编号 ${editingId.value}`
      : '正在撰写一篇新文章'
  return [
    {
      label: '可选分类',
      value: String(cat),
      icon: appIcons.category,
      hue: 245,
      ariaLabel: `分类库共 ${cat} 项，可在表单中选择`
    },
    {
      label: '可选标签',
      value: String(tags),
      icon: appIcons.tag,
      hue: 205,
      ariaLabel: `标签库共 ${tags} 个，可在表单中多选`
    },
    {
      label: '当前稿件',
      value: draftLabel,
      icon: appIcons.document,
      hue: 175,
      ariaLabel: draftAria
    }
  ]
})

const loadCategories = async () => {
  try {
    const rows = await getCategories()
    categories.value = rows.map((item: CategoryOption) => {
      return {
        label: String(item.name ?? ''),
        value: item.id
      }
    }).filter((item) => item.value > 0)
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadTags = async () => {
  try {
    const rows = await getTags()
    tagOptions.value = rows.map((item: TagOption) => {
      return {
        label: String(item.name ?? ''),
        value: item.id
      }
    }).filter((item) => item.value > 0)
  } catch (error) {
    console.error('加载标签失败:', error)
  }
}

const loadArticleDetail = async () => {
  if (!editingId.value) {
    return
  }
  bootstrapLoading.value = true
  try {
    const article: ArticleDetailPayload = await getArticleDetail(editingId.value)
    const tags = Array.isArray(article.tags) ? article.tags : []
    form.value = {
      title: String(article.title ?? ''),
      summary: String(article.summary ?? ''),
      avatar: String(article.avatar ?? ''),
      avatarFileId: (article as { avatarFileId?: number }).avatarFileId ?? null,
      externalAvatarUrl: '',
      content: String(article.content ?? ''),
      categoryId: article.categoryId ?? null,
      tagIds: tags
        .map((tag: ArticleDetailTagPayload) => tag.id ?? 0)
        .filter((id) => id > 0),
      isOriginal: article.isOriginal ?? 1
    }
  } catch (error) {
    console.error('加载文章详情失败:', error)
    message.error('没能读到这篇文章，请稍后再试')
  } finally {
    bootstrapLoading.value = false
  }
}

const submitArticle = async () => {
  if (!isLoggedIn.value) {
    void router.push('/login')
    return
  }

  if (!form.value.title.trim()) {
    message.warning('请先写一个标题')
    return
  }

  if (getPlainContentLength(form.value.content) === 0) {
    message.warning('正文还没有内容')
    return
  }

  loading.value = true
  try {
    const avatarValue = form.value.externalAvatarUrl?.trim() || form.value.avatar
    const payload: ArticleCreateUpdateDTO = {
      title: form.value.title.trim(),
      summary: form.value.summary.trim(),
      avatar: avatarValue || undefined,
      content: normalizeRichContent(form.value.content),
      categoryId: form.value.categoryId ?? undefined,
      tagIds: form.value.tagIds,
      isOriginal: form.value.isOriginal
    }

    if (editingId.value) {
      await updateArticle(editingId.value, payload)
      message.success('文章已更新')
    } else {
      await createArticle(payload)
      message.success('文章已提交，审核通过后会展示出来')
    }
    void router.push('/user')
  } catch (error) {
    console.error('提交文章失败:', error)
    message.error('这次提交没有成功，请稍后再试')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await Promise.all([loadCategories(), loadTags()])
  await loadArticleDetail()
})
</script>

<template>
  <div class="glass-page newpost-page">
    <PageShell>
      <SCard
        class="newpost-hero"
        variant="lite"
      >
        <div class="newpost-hero__main">
          <div class="newpost-hero__copy">
            <span
              class="article-card-prelude-label"
            >创作</span>
            <h1 class="newpost-hero__title">{{ pageTitle }}</h1>
            <p class="newpost-hero__subtitle">{{ headerSubtitle }}</p>

            <section class="newpost-hero__summary" aria-label="编辑器概览">
              <HeroStatCard
                v-for="tile in heroStatTiles"
                :key="tile.label"
                class="newpost-hero__stat"
                mode="metric"
                tone="tinted"
                size="lg"
                :icon="tile.icon"
                :value="tile.value"
                :label="tile.label"
                :hue="tile.hue"
                :aria-label="tile.ariaLabel"
              />
            </section>
          </div>

          <aside class="newpost-hero__aside" aria-label="发布说明">
            <div class="newpost-guide-panel">
              <div class="newpost-guide-panel__glow" aria-hidden="true" />
              <div class="newpost-guide-panel__body">
                <div class="newpost-guide-panel__copy">
                  <p class="newpost-guide-panel__eyebrow">审核与展示</p>
                  <h2 class="newpost-guide-panel__title">{{ guidePanelTitle }}</h2>
                  <p class="newpost-guide-panel__lede">{{ guidePanelLede }}</p>
                </div>
                <div class="newpost-guide-panel__meta">
                  <span class="newpost-guide-chip">先审后发</span>
                  <span class="newpost-guide-chip">{{
                    isLoggedIn ? '已登录' : '未登录'
                  }}</span>
                </div>
              </div>
            </div>
          </aside>
        </div>
      </SCard>

      <div
        v-if="bootstrapLoading"
        class="newpost-bootstrap-skel"
        aria-busy="true"
        aria-label="编辑器数据加载中"
      >
        <SSkeleton :sharp="false" class="newpost-bootstrap-skel__title" />
        <div class="newpost-bootstrap-skel__grid">
          <SSkeleton v-for="n in 8" :key="`nps-${n}`" :sharp="false" class="newpost-bootstrap-skel__field" />
        </div>
        <div class="newpost-bootstrap-skel__actions">
          <SSkeleton :sharp="false" class="newpost-bootstrap-skel__btn" />
          <SSkeleton :sharp="false" class="newpost-bootstrap-skel__btn" />
        </div>
      </div>
      <PostEditorForm
        v-else
        v-model="form"
        :categories="categories"
        :tag-options="tagOptions"
        :loading="loading"
        :submit-text="submitText"
        cancel-text="返回"
        @cancel="router.back()"
        @submit="submitArticle"
      />
    </PageShell>
  </div>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.newpost-page {
  --content-max-width: 1080px;
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: var(--surface-page-content);
}

.newpost-page::before {
  background: var(--surface-page-ambient);
  opacity: 0.34;
}

.newpost-bootstrap-skel {
  display: grid;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
  border-radius: calc(var(--glass-radius) + 6px);
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft);
}

.newpost-bootstrap-skel__title {
  width: min(40%, 12rem) !important;
  height: 1.5rem !important;
}

.newpost-bootstrap-skel__grid {
  display: grid;
  gap: var(--spacing-md);
}

.newpost-bootstrap-skel__field {
  width: 100% !important;
  height: 2.75rem !important;
  border-radius: var(--border-radius-md) !important;
}

.newpost-bootstrap-skel__actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
  margin-top: var(--spacing-md);
}

.newpost-bootstrap-skel__btn {
  width: 6rem !important;
  height: 2.5rem !important;
  border-radius: var(--border-radius-md) !important;
}

/* 与 HotPage / AboutPage 同源：英雄区 + chip 眉题 + 概览块 + 侧栏说明卡 */
.newpost-hero {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
  padding: clamp(26px, 4vw, 34px);
  margin-bottom: var(--spacing-xl);
}

.newpost-hero__main {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(240px, 0.88fr);
  gap: var(--spacing-xxl);
  align-items: start;
}

.newpost-hero__copy {
  display: grid;
  gap: var(--page-block-gap);
  min-width: 0;
}

.article-card-prelude-label {
  width: fit-content;
  padding: 0.34rem 0.72rem;
  margin-top: 0.15rem;
  justify-self: start;
}

.newpost-hero__title {
  margin: 0;
  font-size: clamp(2.4rem, 4.6vw, 3.6rem);
  line-height: 0.98;
  letter-spacing: 0.02em;
  color: var(--title-color);
}

.newpost-hero__subtitle {
  max-width: 42rem;
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.98rem;
  line-height: 1.85;
}

.newpost-hero__summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: var(--spacing-md);
  width: 100%;
}








.newpost-guide-panel {
  position: relative;
  display: grid;
  width: 100%;
  min-height: 100%;
  overflow: hidden;
}

.newpost-guide-panel__glow {
  position: absolute;
  inset: auto -20% -32% auto;
  width: min(11rem, 68%);
  aspect-ratio: 1;
  border-radius: 50%;
  background: radial-gradient(
    circle,
    color-mix(in srgb, var(--primary-color) 14%, transparent) 0%,
    transparent 68%
  );
  pointer-events: none;
}

.newpost-guide-panel__body {
  position: relative;
  z-index: 1;
  display: grid;
  gap: var(--spacing-lg);
  padding: var(--spacing-xl);
}

.newpost-guide-panel__copy {
  display: grid;
  gap: var(--spacing-sm);
}

.newpost-guide-panel__eyebrow {
  margin: 0;
  font-size: var(--font-size-xs, 10px);
  font-weight: 700;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--primary-color) 72%, var(--text-secondary));
}

.newpost-guide-panel__title {
  margin: 0;
  font-size: clamp(1.25rem, 2.2vw, 1.65rem);
  line-height: 1.15;
  color: var(--title-color);
}

.newpost-guide-panel__lede {
  margin: 0;
  font-size: var(--font-size-md, 14px);
  line-height: 1.75;
  color: var(--text-reading-soft);
}

.newpost-guide-panel__meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.newpost-guide-chip {
  padding: 0.28rem 0.65rem;
  font-size: var(--font-size-sm, 12px);
  font-weight: 600;
  color: var(--text-secondary);
}

@include sourcelin-down(lg) {
  .newpost-hero__summary {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .newpost-hero__stat:last-child {
    grid-column: 1 / -1;
  }
}

@include sourcelin-down(md) {
  .newpost-hero {
    padding: 24px 20px;
    border-radius: 28px;
  }

  .newpost-hero__main {
    grid-template-columns: 1fr;
    gap: var(--spacing-xl);
  }

  .newpost-guide-panel__body {
    padding: var(--spacing-lg);
  }
}

@include sourcelin-down(sm) {
  .newpost-hero__summary {
    grid-template-columns: 1fr;
  }

  .newpost-hero__stat:last-child {
    grid-column: auto;
  }



}
</style>
