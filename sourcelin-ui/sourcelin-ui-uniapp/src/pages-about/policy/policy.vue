<template>
  <view class="legal s-container" :class="themeStore.themeClass">
    <s-empty
      v-if="!article"
      title="内容不存在"
      text="协议内容暂时不可访问，请稍后重试。"
    />

    <template v-else>
      <view class="legal__hero s-card">
        <view class="legal__chip">{{ article.chip }}</view>
        <view class="legal__title">{{ article.title }}</view>
        <view class="legal__subtitle">{{ article.subtitle }}</view>
      </view>

      <view class="legal__content s-card">
        <view
          v-for="section in article.sections"
          :key="section.title"
          class="legal__section"
        >
          <view class="legal__section-title">{{ section.title }}</view>
          <view class="legal__section-body">
            <view
              v-for="paragraph in section.paragraphs"
              :key="paragraph"
              class="legal__paragraph"
            >
              {{ paragraph }}
            </view>
          </view>
        </view>

        <view class="legal__footer">
          <view class="legal__footer-text">本政策最后更新于：{{ article.updatedAt }}</view>
          <view class="legal__footer-hint">{{ article.footerHint }}</view>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { onLoad, onShow } from '@dcloudio/uni-app';
import { getLegalArticle, type LegalArticle } from '../modules/site/constants/legal';
import { applyH5Seo, buildSeoTitle, extractSeoSummary } from '@/shared/utils/seo';
import { useThemeStore } from '@/stores/theme';

const themeStore = useThemeStore();
const article = ref<LegalArticle | null>(null);

watch(article, (currentArticle) => {
  applyH5Seo({
    title: buildSeoTitle(currentArticle?.title || '协议详情'),
    description: extractSeoSummary(currentArticle?.subtitle),
    keywords: [currentArticle?.title || '协议与政策', '隐私政策', '用户协议']
  });
}, { immediate: true });

onShow(() => {
  themeStore.syncNativeArea();
});

onLoad((options) => {
  article.value = getLegalArticle(typeof options?.type === 'string' ? options.type : '');
});
</script>

<style lang="scss" scoped>
.legal {
  min-height: 100vh;

  &__hero {
    margin-bottom: $space-md;
  }

  &__chip {
    width: fit-content;
    min-height: 46rpx;
    padding: 0 18rpx;
    display: inline-flex;
    align-items: center;
    border-radius: 999rpx;
    background: rgba(59, 89, 255, 0.12);
    color: var(--sl-color-primary);
    font-size: 22rpx;
    font-weight: 700;
  }

  &__title {
    margin-top: 18rpx;
    color: var(--sl-text-main);
    font-size: 40rpx;
    font-weight: 800;
  }

  &__subtitle {
    margin-top: 12rpx;
    color: var(--sl-text-sub);
    font-size: 25rpx;
    line-height: 1.7;
  }

  &__section + &__section {
    margin-top: 28rpx;
    padding-top: 28rpx;
    border-top: 1rpx solid var(--sl-control-border);
  }

  &__section-title {
    color: var(--sl-text-main);
    font-size: 30rpx;
    font-weight: 800;
  }

  &__section-body {
    margin-top: 14rpx;
    display: flex;
    flex-direction: column;
    gap: 12rpx;
  }

  &__paragraph {
    color: var(--sl-text-sub);
    font-size: 25rpx;
    line-height: 1.82;
  }

  &__footer {
    margin-top: 34rpx;
    padding-top: 24rpx;
    border-top: 1rpx solid var(--sl-control-border);
    text-align: center;
  }

  &__footer-text {
    color: var(--sl-text-sub);
    font-size: 22rpx;
    line-height: 1.6;
  }

  &__footer-hint {
    margin-top: 8rpx;
    color: var(--sl-text-muted);
    font-size: 21rpx;
    line-height: 1.6;
  }
}
</style>
