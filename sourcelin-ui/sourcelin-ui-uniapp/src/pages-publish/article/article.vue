<template>
  <view v-if="ready" class="publish-article s-container" :class="themeStore.themeClass">
    <s-loading :visible="bootLoading" text="正在准备编辑器..." />

    <view class="publish-article__hero s-card">
      <view class="publish-article__title">{{ pageTitle }}</view>
      <view class="publish-article__desc">{{ pageDesc }}</view>
      <view v-if="isEditing" class="publish-article__status-row">
        <view class="publish-article__status-tag" :class="`publish-article__status-tag--${statusKey}`">
          {{ statusText }}
        </view>
        <view class="publish-article__status-tip">{{ statusTip }}</view>
      </view>
    </view>

    <view class="publish-article__form s-card">
      <view class="publish-article__field">
        <view class="publish-article__label">标题</view>
        <input
          v-model.trim="form.title"
          class="publish-article__input publish-article__input--title"
          maxlength="80"
          placeholder="给这篇文章起一个清晰标题"
          placeholder-class="publish-article__placeholder"
        >
      </view>

      <view class="publish-article__field">
        <view class="publish-article__label">摘要</view>
        <textarea
          v-model.trim="form.summary"
          class="publish-article__textarea publish-article__textarea--summary"
          maxlength="180"
          auto-height
          placeholder="用 1-2 句话概括重点，方便分享与回访"
          placeholder-class="publish-article__placeholder"
        />
      </view>

      <view class="publish-article__field">
        <view class="publish-article__field-head">
          <view class="publish-article__label">封面</view>
          <view class="publish-article__field-desc">建议横图，清晰且不过度花哨</view>
        </view>
        <view v-if="coverUrl" class="publish-article__cover-wrap">
          <image class="publish-article__cover" :src="coverUrl" mode="aspectFill" />
          <view class="publish-article__cover-actions">
            <view class="publish-article__cover-action" @tap="pickCover">更换</view>
            <view class="publish-article__cover-action publish-article__cover-action--danger" @tap="clearCover">移除</view>
          </view>
        </view>
        <view v-else class="publish-article__cover-picker" @tap="pickCover">
          <view class="publish-article__cover-icon">+</view>
          <view class="publish-article__cover-text">上传封面</view>
        </view>
      </view>

      <view class="publish-article__field">
        <view class="publish-article__field-head">
          <view class="publish-article__label">分类</view>
          <view class="publish-article__field-desc">至少选择一个主分类</view>
        </view>
        <view class="publish-article__chips">
          <view
            v-for="item in visibleCategories"
            :key="item.id"
            class="publish-article__chip"
            :class="{ 'publish-article__chip--active': form.categoryId === item.id }"
            @tap="form.categoryId = item.id"
          >
            <text class="publish-article__chip-text">{{ item.name }}</text>
          </view>
        </view>
        <view
          v-if="categories.length > collapsedCategoryCount"
          class="publish-article__chips-toggle"
          @tap="showAllCategories = !showAllCategories"
        >
          {{ showAllCategories ? '收起分类' : `展开全部 ${categories.length} 个分类` }}
        </view>
      </view>

      <view class="publish-article__field">
        <view class="publish-article__field-head">
          <view class="publish-article__label">标签</view>
          <view class="publish-article__field-desc">最多选择 5 个，帮助发现与推荐</view>
        </view>
        <view class="publish-article__chips">
          <view
            v-for="item in visibleTags"
            :key="item.id"
            class="publish-article__chip publish-article__chip--soft"
            :class="{ 'publish-article__chip--active': form.tagIds.includes(item.id) }"
            @tap="toggleTag(item.id)"
          >
            <text class="publish-article__chip-text"># {{ item.name }}</text>
          </view>
        </view>
        <view
          v-if="tags.length > collapsedTagCount"
          class="publish-article__chips-toggle"
          @tap="showAllTags = !showAllTags"
        >
          {{ showAllTags ? '收起标签' : `展开全部 ${tags.length} 个标签` }}
        </view>
      </view>

      <view class="publish-article__field">
        <view class="publish-article__field-head">
          <view class="publish-article__label">正文</view>
          <view class="publish-article__field-desc">移动端采用轻量编辑，适合初稿与随手更新</view>
        </view>
        <textarea
          v-model="form.content"
          class="publish-article__textarea publish-article__textarea--content"
          maxlength="-1"
          auto-height
          placeholder="输入正文内容。段落之间空一行，提交后会自动整理为阅读友好的段落。"
          placeholder-class="publish-article__placeholder"
        />
      </view>

      <view class="publish-article__field">
        <view class="publish-article__field-head">
          <view class="publish-article__label">发布选项</view>
        </view>
        <view class="publish-article__switches">
          <view class="publish-article__switch-row" @tap="toggleOriginal">
            <view>
              <view class="publish-article__switch-title">原创内容</view>
              <view class="publish-article__switch-desc">关闭后需填写原文链接</view>
            </view>
            <view class="publish-article__switch" :class="{ 'publish-article__switch--active': form.isOriginal === 1 }" />
          </view>

          <view v-if="form.isOriginal !== 1" class="publish-article__inline-field">
            <input
              v-model.trim="form.originalUrl"
              class="publish-article__input"
              maxlength="255"
              placeholder="请输入原文地址"
              placeholder-class="publish-article__placeholder"
            >
          </view>

          <view class="publish-article__switch-row" @tap="toggleComment">
            <view>
              <view class="publish-article__switch-title">允许评论</view>
              <view class="publish-article__switch-desc">关闭后读者只能阅读与分享</view>
            </view>
            <view class="publish-article__switch" :class="{ 'publish-article__switch--active': form.isComment === 1 }" />
          </view>
        </view>
      </view>
    </view>

    <view class="publish-article__toolbar">
      <button
        v-if="canDelete"
        class="publish-article__toolbar-button sl-button sl-button--danger"
        :disabled="submitting"
        @tap="removeArticleAction"
      >
        删除稿件
      </button>
      <button
        class="publish-article__toolbar-button sl-button sl-button--secondary"
        :disabled="submitting"
        @tap="saveDraft"
      >
        <s-inline-loading v-if="submitting && submitMode === 'draft'" text="保存中" />
        <text v-else>保存草稿</text>
      </button>
      <button
        class="publish-article__toolbar-button sl-button sl-button--primary"
        :disabled="submitting"
        @tap="submitReview"
      >
        <s-inline-loading v-if="submitting && submitMode === 'review'" text="提交中" light />
        <text v-else>{{ isEditing ? '更新并送审' : '提交审核' }}</text>
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import {
  createArticle,
  deleteArticle,
  fetchArticleDetail,
  fetchCategoryList,
  fetchTagList,
  updateArticle
} from '@/modules/article/api/article.api';
import type { ArticleDetail, CategoryItem, TagItem } from '@/modules/article/types/article';
import { markArticleRefresh } from '../modules/article/utils/publish';
import { uploadPublicFile } from '../modules/shared/api/file.api';
import { reportAnalyticsEvent } from '@/shared/utils/analytics';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';
import { useThemeStore } from '@/stores/theme';
import { useUserStore } from '@/stores/user';
import { showInfoToast, showSuccessToast } from '@/utils/feedback';
import { pickSingleImagePath } from '../modules/utils/media';
import { normalizeAssetUrl } from '@/utils/url';

type SubmitMode = 'draft' | 'review' | '';
const themeStore = useThemeStore();
const userStore = useUserStore();
const { guard } = useMiniAccess();

onShow(() => {
  themeStore.syncNativeArea();
});

interface ArticleForm {
  title: string;
  summary: string;
  content: string;
  categoryId?: number;
  tagIds: number[];
  avatar: string;
  avatarFileId?: number;
  isOriginal: 0 | 1;
  originalUrl: string;
  isComment: 0 | 1;
}

const STATUS_REVIEW = 1;
const STATUS_PUBLISHED = 2;
const STATUS_DRAFT = 3;
const MAX_TAG_COUNT = 5;
const collapsedCategoryCount = 8;
const collapsedTagCount = 10;

const ready = ref(false);
const bootLoading = ref(false);
const submitting = ref(false);
const submitMode = ref<SubmitMode>('');
const articleId = ref<number | null>(null);
const currentStatus = ref<number>(STATUS_DRAFT);
const categories = ref<CategoryItem[]>([]);
const tags = ref<TagItem[]>([]);
const showAllCategories = ref(false);
const showAllTags = ref(false);

const form = reactive<ArticleForm>({
  title: '',
  summary: '',
  content: '',
  categoryId: undefined,
  tagIds: [],
  avatar: '',
  avatarFileId: undefined,
  isOriginal: 1,
  originalUrl: '',
  isComment: 1
});

const isEditing = computed(() => articleId.value !== null);
const coverUrl = computed(() => normalizeAssetUrl(form.avatar));
const canDelete = computed(() => isEditing.value && currentStatus.value !== STATUS_PUBLISHED);
const visibleCategories = computed(() =>
  showAllCategories.value ? categories.value : categories.value.slice(0, collapsedCategoryCount)
);
const visibleTags = computed(() =>
  showAllTags.value ? tags.value : tags.value.slice(0, collapsedTagCount)
);
const pageTitle = computed(() => (isEditing.value ? '编辑文章' : '写文章'));
const pageDesc = computed(() =>
  isEditing.value
    ? '移动端适合快速补充与修订。保存草稿后可继续编辑，提交后会重新进入审核。'
    : '在手机上完成一篇轻量初稿，适合灵感记录、内容补写和随时发布。'
);
const statusKey = computed(() => {
  if (currentStatus.value === STATUS_PUBLISHED) return 'published';
  if (currentStatus.value === STATUS_REVIEW) return 'review';
  return 'draft';
});
const statusText = computed(() => {
  if (currentStatus.value === STATUS_PUBLISHED) return '已发布';
  if (currentStatus.value === STATUS_REVIEW) return '审核中';
  return '草稿';
});
const statusTip = computed(() => {
  if (currentStatus.value === STATUS_PUBLISHED) return '修改后会重新进入审核队列。';
  if (currentStatus.value === STATUS_REVIEW) return '可继续修订，更新后会覆盖当前审核版本。';
  return '草稿不会公开展示，可随时继续完善。';
});

onLoad(async (options) => {
  if (!guard('articlePublishEnabled')) {
    return;
  }
  if (!userStore.isBlogger) {
    uni.switchTab({ url: '/pages/home/home' });
    return;
  }
  bootLoading.value = true;
  ready.value = true;
  try {
    await Promise.all([loadCategories(), loadTags()]);
    const id = Number(options?.id);
    if (Number.isFinite(id) && id > 0) {
      articleId.value = id;
      await loadArticle(id);
    }
  } finally {
    bootLoading.value = false;
  }
});

async function loadCategories(): Promise<void> {
  categories.value = await fetchCategoryList();
}

async function loadTags(): Promise<void> {
  tags.value = await fetchTagList();
}

async function loadArticle(id: number): Promise<void> {
  const detail = await fetchArticleDetail(id);
  fillForm(detail);
}

function fillForm(detail: ArticleDetail): void {
  currentStatus.value = Number(detail.status) || STATUS_DRAFT;
  form.title = detail.title || '';
  form.summary = detail.summary || '';
  form.content = toEditorText(detail.content || '');
  form.categoryId = detail.categoryId;
  form.avatar = detail.avatar || '';
  form.avatarFileId = detail.avatarFileId;
  form.isOriginal = detail.isOriginal === 0 ? 0 : 1;
  form.originalUrl = detail.originalUrl || '';
  form.isComment = detail.isComment === 0 ? 0 : 1;
  form.tagIds = Array.isArray(detail.tags) && detail.tags.length > 0
    ? detail.tags.map((item) => Number(item.id)).filter((item) => Number.isFinite(item) && item > 0)
    : (detail.tagIds || '')
        .split(',')
        .map((item) => Number(item.trim()))
        .filter((item) => Number.isFinite(item) && item > 0);
}

function toEditorText(value: string): string {
  return value
    .replace(/<style[\s\S]*?>[\s\S]*?<\/style>/gi, '')
    .replace(/<script[\s\S]*?>[\s\S]*?<\/script>/gi, '')
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<\/(p|div|h1|h2|h3|h4|li)>/gi, '\n')
    .replace(/<[^>]+>/g, '')
    .replace(/&nbsp;/gi, ' ')
    .replace(/&amp;/gi, '&')
    .replace(/&lt;/gi, '<')
    .replace(/&gt;/gi, '>')
    .replace(/\n{3,}/g, '\n\n')
    .trim();
}

function toRichText(value: string): string {
  const safe = value
    .split('\n')
    .map((line) => line.trim())
    .filter((line) => line.length > 0)
    .map((line) =>
      `<p>${line
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
      }</p>`
    );
  return safe.join('') || '<p></p>';
}

function toggleTag(id: number): void {
  const exists = form.tagIds.includes(id);
  if (exists) {
    form.tagIds = form.tagIds.filter((item) => item !== id);
    return;
  }
  if (form.tagIds.length >= MAX_TAG_COUNT) {
    showInfoToast(`最多选择 ${MAX_TAG_COUNT} 个标签`);
    return;
  }
  form.tagIds = [...form.tagIds, id];
}

function toggleOriginal(): void {
  form.isOriginal = form.isOriginal === 1 ? 0 : 1;
  if (form.isOriginal === 1) {
    form.originalUrl = '';
  }
}

function toggleComment(): void {
  form.isComment = form.isComment === 1 ? 0 : 1;
}

function validateForm(): boolean {
  if (!form.title.trim()) {
    showInfoToast('请输入文章标题');
    return false;
  }
  if (!form.categoryId) {
    showInfoToast('请选择分类');
    return false;
  }
  if (!form.content.trim()) {
    showInfoToast('请输入正文内容');
    return false;
  }
  if (form.isOriginal === 0 && !form.originalUrl.trim()) {
    showInfoToast('转载文章请填写原文地址');
    return false;
  }
  return true;
}

function buildPayload(status: number) {
  return {
    title: form.title.trim(),
    summary: form.summary.trim() || undefined,
    content: toRichText(form.content),
    categoryId: form.categoryId,
    tagIds: form.tagIds,
    avatar: form.avatar || undefined,
    avatarFileId: form.avatarFileId,
    readAuth: 1,
    status,
    isComment: form.isComment,
    isOriginal: form.isOriginal,
    originalUrl: form.isOriginal === 0 ? form.originalUrl.trim() : undefined
  };
}

async function pickCover(): Promise<void> {
  const filePath = await pickSingleImagePath({
    sizeType: ['compressed'],
    sourceType: ['album', 'camera']
  });
  if (!filePath) return;
  const uploaded = await uploadPublicFile(filePath);
  form.avatar = uploaded.url || '';
  form.avatarFileId = uploaded.fileId ? Number(uploaded.fileId) : undefined;
}

function clearCover(): void {
  form.avatar = '';
  form.avatarFileId = undefined;
}

async function saveDraft(): Promise<void> {
  await submitWithStatus(STATUS_DRAFT, '草稿已保存');
}

async function submitReview(): Promise<void> {
  await submitWithStatus(STATUS_REVIEW, isEditing.value ? '文章已更新并提交审核' : '文章已提交审核');
}

async function submitWithStatus(status: number, successText: string): Promise<void> {
  if (submitting.value) return;
  if (!validateForm()) return;
  submitting.value = true;
  submitMode.value = status === STATUS_DRAFT ? 'draft' : 'review';
  try {
    const payload = buildPayload(status);
    if (articleId.value) {
      await updateArticle(articleId.value, payload);
    } else {
      const id = await createArticle(payload);
      articleId.value = Number(id);
    }
    currentStatus.value = status;
    void reportAnalyticsEvent({
      eventType: status === STATUS_DRAFT ? 'article_save_draft' : 'article_submit_review',
      pagePath: '/pages-publish/article/article',
      targetType: 'article',
      targetId: articleId.value || undefined,
      metadata: {
        categoryId: form.categoryId,
        tagCount: form.tagIds.length,
        editing: isEditing.value
      }
    });
    markArticleRefresh();
    showSuccessToast(successText);
    uni.navigateBack();
  } finally {
    submitting.value = false;
    submitMode.value = '';
  }
}

async function removeArticleAction(): Promise<void> {
  if (!articleId.value || submitting.value) return;
  const confirmed = await new Promise<boolean>((resolve) => {
    uni.showModal({
      title: '删除稿件',
      content: '仅草稿和审核中的文章允许删除。删除后不可恢复，是否继续？',
      success: (res) => resolve(!!res.confirm),
      fail: () => resolve(false)
    });
  });
  if (!confirmed) return;
  submitting.value = true;
  try {
    await deleteArticle(articleId.value);
    void reportAnalyticsEvent({
      eventType: 'article_delete_draft',
      pagePath: '/pages-publish/article/article',
      targetType: 'article',
      targetId: articleId.value
    });
    markArticleRefresh();
    showSuccessToast('稿件已删除');
    uni.navigateBack();
  } finally {
    submitting.value = false;
    submitMode.value = '';
  }
}
</script>

<style lang="scss" scoped>
.publish-article {
  min-height: 100vh;
  padding-bottom: calc(244rpx + env(safe-area-inset-bottom));

  &.sl-theme--dark {
    .publish-article__form {
      background: var(--sl-surface-bg);
    }

    .publish-article__placeholder {
      color: var(--sl-text-muted);
    }

    .publish-article__toolbar-button.sl-button--primary:not([disabled]) {
      color: #fff;
      background: linear-gradient(135deg, #4a90e2 0%, #60a5fa 100%);
    }
  }

  &__hero {
    margin-bottom: $space-md;
  }

  &__title {
    color: $color-text;
    font-size: 38rpx;
    font-weight: 800;
  }

  &__desc {
    margin-top: 12rpx;
    color: $color-text-secondary;
    font-size: 25rpx;
    line-height: 1.7;
  }

  &__status-row {
    display: flex;
    align-items: center;
    gap: 16rpx;
    margin-top: 22rpx;
    flex-wrap: wrap;
  }

  &__status-tag {
    min-height: 52rpx;
    padding: 0 22rpx;
    display: inline-flex;
    align-items: center;
    border-radius: 999rpx;
    font-size: 22rpx;
    font-weight: 700;
  }

  &__status-tag--draft {
    color: #7c4dff;
    background: rgba(124, 77, 255, 0.12);
  }

  &__status-tag--review {
    color: #ff7d00;
    background: rgba(255, 125, 0, 0.12);
  }

  &__status-tag--published {
    color: #00b42a;
    background: rgba(0, 180, 42, 0.12);
  }

  &__status-tip {
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__field + &__field {
    margin-top: $space-lg;
  }

  &__field-head {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
    align-items: flex-start;
    margin-bottom: 14rpx;
  }

  &__label {
    color: $color-text;
    font-size: 27rpx;
    font-weight: 700;
  }

  &__field-desc,
  &__switch-desc {
    color: $color-text-tertiary;
    font-size: 22rpx;
    line-height: 1.6;
    max-width: 100%;
  }

  &__input,
  &__textarea {
    @include sl-input;
  }

  &__input:focus,
  &__textarea:focus {
    border-color: var(--sl-border-focused);
    box-shadow:
      inset 0 0 0 1rpx var(--sl-border-focused),
      0 0 12rpx rgba(59, 89, 255, 0.1);
  }

  &__placeholder {
    color: var(--sl-text-muted);
  }

  &__input {
    min-height: $input-min-height;
    line-height: $input-line-height;
  }

  &__input--title {
    height: $input-min-height;
    padding-top: 0;
    padding-bottom: 0;
  }

  &__textarea--summary {
    min-height: 140rpx;
  }

  &__textarea--content {
    min-height: 520rpx;
    line-height: 1.8;
  }

  &__cover-wrap {
    overflow: hidden;
    border-radius: 28rpx;
    background: var(--sl-control-bg);
  }

  &__cover {
    width: 100%;
    height: 320rpx;
    display: block;
  }

  &__cover-actions {
    display: flex;
    gap: 18rpx;
    padding: 18rpx 22rpx 0;
  }

  &__cover-action {
    color: $color-primary;
    font-size: 24rpx;
    font-weight: 700;
  }

  &__cover-action--danger {
    color: #ff5b75;
  }

  &__cover-picker {
    min-height: 240rpx;
    border-radius: 28rpx;
    border: 2rpx dashed rgba(59, 89, 255, 0.22);
    background: var(--sl-bg-glass-tint);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 10rpx;
    color: $color-primary;
  }

  &__cover-icon {
    font-size: 48rpx;
    line-height: 1;
  }

  &__cover-text {
    font-size: 24rpx;
    font-weight: 700;
  }

  &__chips {
    display: flex;
    flex-wrap: wrap;
    gap: 14rpx;
  }

  &__chip {
    max-width: 100%;
    min-height: 68rpx;
    padding: 14rpx 22rpx;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 999rpx;
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
    color: $color-text-secondary;
    font-size: 24rpx;
    font-weight: 700;
    line-height: 1.35;
    box-sizing: border-box;
  }

  &__chip-text {
    display: block;
    max-width: 100%;
    word-break: break-all;
    text-align: center;
  }

  &__chips-toggle {
    margin-top: 18rpx;
    color: $color-primary;
    font-size: 23rpx;
    font-weight: 700;
  }

  &__chip--soft {
    color: #6d4bff;
  }

  &__chip--active {
    background: rgba(59, 89, 255, 0.12);
    border-color: rgba(59, 89, 255, 0.22);
    color: $color-primary;
  }

  &__switches {
    display: flex;
    flex-direction: column;
    gap: 18rpx;
  }

  &__switch-row {
    display: flex;
    justify-content: space-between;
    gap: 20rpx;
    align-items: center;
    padding: 20rpx 0;
  }

  &__switch-title {
    color: $color-text;
    font-size: 26rpx;
    font-weight: 700;
    margin-bottom: 6rpx;
  }

  &__switch {
    position: relative;
    width: 92rpx;
    height: 54rpx;
    border-radius: 999rpx;
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
    box-shadow:
      inset 0 2rpx 4rpx rgba(0, 0, 0, 0.05),
      0 1rpx 0 rgba(255, 255, 255, 0.9);
    transition: all 0.24s cubic-bezier(0.25, 0.8, 0.25, 1);

    &::after {
      content: '';
      position: absolute;
      top: 4rpx;
      left: 4rpx;
      width: 44rpx;
      height: 44rpx;
      border-radius: 50%;
      background: var(--sl-control-bg-strong);
      box-shadow:
        0 4rpx 10rpx rgba(17, 24, 39, 0.12),
        inset 0 1rpx 0 rgba(255, 255, 255, 0.9);
      transition: all 0.24s cubic-bezier(0.25, 0.8, 0.25, 1);
    }

    &--active {
      background: rgba(59, 89, 255, 0.16);
      border-color: rgba(59, 89, 255, 0.32);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.1),
        0 8rpx 20rpx rgba(59, 89, 255, 0.12);

      &::after {
        left: 42rpx;
        background: var(--sl-color-primary);
        box-shadow:
          0 6rpx 16rpx rgba(59, 89, 255, 0.35),
          inset 0 1rpx 0 rgba(255, 255, 255, 0.3);
      }
    }

    .sl-theme--dark & {
      background: rgba(8, 13, 24, 0.26);
      border-color: var(--sl-border-light);
      box-shadow: inset 0 2rpx 4rpx rgba(0, 0, 0, 0.24);

      &::after {
        background: #a8b2c8;
        box-shadow: 0 4rpx 10rpx rgba(0, 0, 0, 0.3);
      }

      &--active {
        background: rgba(105, 129, 255, 0.16);
        border-color: rgba(105, 129, 255, 0.32);
        box-shadow:
          inset 0 1rpx 0 rgba(255, 255, 255, 0.05),
          0 8rpx 20rpx rgba(105, 129, 255, 0.15);

        &::after {
          background: var(--sl-color-primary-soft);
          box-shadow:
            0 6rpx 16rpx rgba(105, 129, 255, 0.4),
            inset 0 1rpx 0 rgba(255, 255, 255, 0.3);
        }
      }
    }
  }

  &__inline-field {
    margin-top: -4rpx;
  }

  &__toolbar {
    position: fixed;
    left: 24rpx;
    right: 24rpx;
    bottom: calc(24rpx + env(safe-area-inset-bottom));
    z-index: 60;
    display: flex;
    gap: 12rpx;
    margin-top: $space-lg;
    padding: 14rpx;
    border-radius: 34rpx;
    background:
      linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
      var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
      0 18rpx 42rpx rgba(17, 24, 39, 0.08);

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(18rpx) saturate(1.25);
    -webkit-backdrop-filter: blur(18rpx) saturate(1.25);
    /* #endif */
  }

  &__toolbar-button {
    flex: 1;
    min-width: 0;
    min-height: 80rpx;
    padding: 0 18rpx;
    font-size: 25rpx;
    color: inherit;
    text-align: center;
  }

  &__toolbar-button text {
    color: inherit;
  }

  &__toolbar-button.sl-button--danger {
    color: #e54866;
    background: linear-gradient(145deg, rgba(255, 245, 247, 0.98), rgba(255, 235, 240, 0.92));
    border: 1rpx solid rgba(229, 72, 102, 0.16);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 10rpx 22rpx rgba(229, 72, 102, 0.08);
  }

  &__toolbar-button.sl-button--danger[disabled] {
    color: rgba(229, 72, 102, 0.78);
    background: linear-gradient(145deg, rgba(255, 245, 247, 0.96), rgba(255, 239, 242, 0.92));
    border-color: rgba(229, 72, 102, 0.12);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
      0 6rpx 14rpx rgba(229, 72, 102, 0.04);
  }

  &__toolbar-button.sl-button--secondary {
    color: #445064;
    background:
      linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
      var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.94),
      0 10rpx 22rpx rgba(17, 24, 39, 0.04);
  }

  &__toolbar-button.sl-button--secondary[disabled] {
    color: rgba(68, 80, 100, 0.74);
    background:
      linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
      var(--sl-control-bg);
    border-color: var(--sl-control-border);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 6rpx 14rpx rgba(17, 24, 39, 0.02);
  }

  &__toolbar-button.sl-button--primary {
    color: #fff;
    background: linear-gradient(135deg, #4f62ff, #7a67ff);
    border: 1rpx solid rgba(255, 255, 255, 0.14);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.2),
      0 14rpx 28rpx rgba(79, 98, 255, 0.22);
  }

  &__toolbar-button.sl-button--primary[disabled] {
    color: rgba(255, 255, 255, 0.98);
    background: linear-gradient(135deg, rgba(124, 139, 255, 0.98), rgba(166, 156, 255, 0.96));
    border-color: rgba(255, 255, 255, 0.12);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.18),
      0 8rpx 16rpx rgba(79, 98, 255, 0.08);
  }
}
</style>
