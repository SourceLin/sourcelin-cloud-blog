<template>
  <view class="s-empty" :class="`s-empty--${resolvedScene}`">
    <view class="s-empty__icon" :class="`s-empty__icon--${resolvedScene}`">
      <view class="s-empty__glow" />
      <view class="s-empty__card s-empty__card--back" />
      <view class="s-empty__card s-empty__card--front">
        <view class="s-empty__line s-empty__line--strong" />
        <view class="s-empty__line" />
        <view class="s-empty__line s-empty__line--short" />
        <view class="s-empty__dot" />
      </view>
      <view class="s-empty__spark s-empty__spark--left" />
      <view class="s-empty__spark s-empty__spark--right" />
    </view>
    <view class="s-empty__title">{{ resolvedTitle }}</view>
    <view class="s-empty__text">{{ resolvedText }}</view>
    <view v-if="hasAction" class="s-empty__action">
      <slot />
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, useSlots } from 'vue';

type EmptyScene = 'default' | 'search' | 'construction' | 'login';

const props = withDefaults(
  defineProps<{
    title?: string;
    text?: string;
    scene?: EmptyScene;
  }>(),
  { text: '暂无内容', title: '', scene: 'default' }
);

const slots = useSlots();

const sceneTitleMap: Record<EmptyScene, string> = {
  default: '暂无内容',
  search: '没有找到内容',
  construction: '页面建设中',
  login: '登录后可查看'
};

const sceneTextMap: Record<EmptyScene, string> = {
  default: '当前还没有可展示的内容，稍后再来看看。',
  search: '换个关键词或筛选条件再试试。',
  construction: '当前页面能力正在完善中，后续会逐步开放。',
  login: '完成登录后即可查看当前内容与相关记录。'
};

const resolvedScene = computed(() => props.scene);
const resolvedTitle = computed(() => props.title || sceneTitleMap[props.scene]);
const resolvedText = computed(() => props.text || sceneTextMap[props.scene]);
const hasAction = computed(() => !!slots.default);
</script>

<style lang="scss" scoped>
.s-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 72rpx $space-md;

  &--construction {
    padding-top: 92rpx;
  }

  &__icon {
    position: relative;
    width: 240rpx;
    height: 190rpx;
    margin-bottom: 26rpx;
  }

  &__icon--construction {
    transform: translateY(-6rpx);
  }

  &__icon--search .s-empty__spark {
    background: #8f70ff;
    box-shadow: 0 0 18rpx rgba(143, 112, 255, 0.24);
  }

  &__icon--login .s-empty__card--back {
    background: linear-gradient(135deg, rgba(59, 89, 255, 0.12), rgba(255, 255, 255, 0.72));
  }

  &__icon--login .s-empty__line--strong {
    background: linear-gradient(90deg, rgba(59, 89, 255, 0.64), rgba(143, 112, 255, 0.24));
  }

  &__icon--login .s-empty__dot {
    background: rgba(59, 89, 255, 0.24);
    box-shadow: 0 0 20rpx rgba(59, 89, 255, 0.16);
  }

  &__icon--construction .s-empty__card--back {
    background: linear-gradient(135deg, rgba(255, 191, 0, 0.14), rgba(255, 255, 255, 0.72));
  }

  &__icon--construction .s-empty__line--strong {
    background: linear-gradient(90deg, rgba(255, 153, 0, 0.58), rgba(255, 196, 0, 0.26));
  }

  &__glow {
    position: absolute;
    left: 24rpx;
    right: 24rpx;
    bottom: 10rpx;
    height: 86rpx;
    border-radius: 999rpx;
    background: radial-gradient(circle, rgba(59, 89, 255, 0.18), rgba(143, 112, 255, 0.04) 62%, rgba(255, 255, 255, 0));
  }

  &__card {
    position: absolute;
    border-radius: 30rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.82);
    box-shadow: 0 18rpx 44rpx rgba(31, 38, 135, 0.1);
  }

  &__card--back {
    left: 52rpx;
    top: 34rpx;
    width: 136rpx;
    height: 104rpx;
    transform: rotate(-8deg);
    background: linear-gradient(135deg, rgba(143, 112, 255, 0.14), rgba(255, 255, 255, 0.68));
  }

  &__card--front {
    left: 36rpx;
    top: 54rpx;
    width: 164rpx;
    height: 112rpx;
    transform: rotate(5deg);
    background: linear-gradient(145deg, rgba(255, 255, 255, 0.94), rgba(248, 250, 255, 0.74));
    overflow: hidden;
  }

  &__line {
    width: 92rpx;
    height: 9rpx;
    margin-left: 30rpx;
    margin-top: 14rpx;
    border-radius: 999rpx;
    background: rgba(75, 85, 99, 0.16);
  }

  &__line--strong {
    width: 72rpx;
    margin-top: 28rpx;
    background: linear-gradient(90deg, rgba(59, 89, 255, 0.58), rgba(143, 112, 255, 0.28));
  }

  &__line--short {
    width: 54rpx;
  }

  &__dot {
    position: absolute;
    right: 26rpx;
    bottom: 22rpx;
    width: 20rpx;
    height: 20rpx;
    border-radius: 50%;
    background: rgba(59, 89, 255, 0.18);
  }

  &__spark {
    position: absolute;
    width: 14rpx;
    height: 14rpx;
    border-radius: 50%;
    background: #3b59ff;
    box-shadow: 0 0 18rpx rgba(59, 89, 255, 0.22);
  }

  &__spark--left {
    left: 34rpx;
    top: 42rpx;
    opacity: 0.5;
  }

  &__spark--right {
    right: 28rpx;
    top: 76rpx;
    width: 10rpx;
    height: 10rpx;
    opacity: 0.36;
  }

  &__title {
    margin-bottom: 10rpx;
    color: $color-text;
    font-size: 31rpx;
    font-weight: 700;
    line-height: 1.35;
    text-align: center;
  }

  &__text {
    max-width: 460rpx;
    color: $color-text-secondary;
    font-size: 27rpx;
    line-height: 1.65;
    text-align: center;
  }

  &__action {
    margin-top: 24rpx;
  }
}
</style>
