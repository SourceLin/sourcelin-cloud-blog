<template>
  <view v-if="visible" class="s-comment-sheet__mask" :class="themeClass" @tap="emit('close')">
    <view
      class="s-comment-sheet"
      :class="{ 's-comment-sheet--keyboard': keyboardVisible }"
      :style="sheetStyle"
      @tap.stop
    >
      <view class="s-comment-sheet__head">
        <view>
          <view class="s-comment-sheet__title">评论互动</view>
          <view class="s-comment-sheet__subtitle">{{ subtitle }}</view>
        </view>
        <view class="s-comment-sheet__close" @tap="emit('close')">关闭</view>
      </view>

      <s-loading
        v-if="loading && comments.length === 0"
        :visible="true"
        :fullpage="false"
        compact
        text="正在加载评论..."
      />
      <view v-else-if="comments.length === 0" class="s-comment-sheet__empty">
        暂无评论，来写第一条
      </view>
      <scroll-view v-else class="s-comment-sheet__list" scroll-y>
        <view
          v-for="item in commentTree"
          :key="item.id"
          class="s-comment-sheet__comment"
          @tap="handleCommentTap(item)"
        >
          <view class="s-comment-sheet__comment-head">
            <text class="s-comment-sheet__nickname">{{ item.userNickname || item.nickname || '读者' }}</text>
            <view class="s-comment-sheet__meta">
              <text class="s-comment-sheet__time">{{ formatDate(item.createTime) }}</text>
              <text v-if="canComment" class="s-comment-sheet__reply-btn" @tap.stop="handleCommentTap(item)">回复</text>
              <text
                v-if="isLoggedIn && currentUserId && currentUserId === item.userId"
                class="s-comment-sheet__delete-btn"
                @tap.stop="emit('delete', item.id)"
              >
                删除
              </text>
            </view>
          </view>
          <view class="s-comment-sheet__comment-content">{{ item.content }}</view>

          <view v-if="item.replies.length > 0" class="s-comment-sheet__reply-box" @tap.stop>
            <view
              v-for="reply in isExpanded(item.id) ? item.replies : item.replies.slice(0, 2)"
              :key="reply.id"
              class="s-comment-sheet__reply-item"
              @tap="handleCommentTap(reply)"
            >
              <view class="s-comment-sheet__reply-head">
                <view class="s-comment-sheet__reply-users">
                  <text class="s-comment-sheet__reply-author">{{ reply.userNickname || reply.nickname || '读者' }}</text>
                  <text class="s-comment-sheet__reply-arrow">▶</text>
                  <text class="s-comment-sheet__reply-target">{{ reply.parentNickname || '读者' }}</text>
                </view>
                <view class="s-comment-sheet__reply-meta">
                  <text class="s-comment-sheet__reply-time">{{ formatDate(reply.createTime) }}</text>
                  <text v-if="canComment" class="s-comment-sheet__reply-action" @tap.stop="handleCommentTap(reply)">回复</text>
                  <text
                    v-if="isLoggedIn && currentUserId && currentUserId === reply.userId"
                    class="s-comment-sheet__reply-delete"
                    @tap.stop="emit('delete', reply.id)"
                  >
                    删除
                  </text>
                </view>
              </view>
              <view class="s-comment-sheet__reply-content">{{ reply.content }}</view>
            </view>

            <view
              v-if="!isExpanded(item.id) && item.replies.length > 2"
              class="s-comment-sheet__reply-toggle"
              @tap.stop="toggleExpanded(item.id)"
            >
              <text class="s-comment-sheet__reply-toggle-text">—— 展开 {{ item.replies.length }} 条回复 ∨</text>
            </view>
            <view
              v-else-if="isExpanded(item.id) && item.replies.length > 2"
              class="s-comment-sheet__reply-toggle"
              @tap.stop="toggleExpanded(item.id)"
            >
              <text class="s-comment-sheet__reply-toggle-text">—— 收起回复 ∧</text>
            </view>
          </view>
        </view>
        <s-loading
          v-if="loading"
          :visible="true"
          :fullpage="false"
          compact
          text="正在加载评论..."
        />
      </scroll-view>

      <view v-if="replyTarget" class="s-comment-sheet__reply-indicator">
        <text class="s-comment-sheet__reply-indicator-text">
          正在回复 @{{ replyTarget.userNickname || replyTarget.nickname || '读者' }}
        </text>
        <view class="s-comment-sheet__reply-indicator-close" @tap="emit('cancelReply')">✕</view>
      </view>

      <template v-if="canComment">
        <textarea
          :value="modelValue"
          class="s-comment-sheet__textarea"
          :placeholder="placeholder"
          placeholder-class="s-comment-sheet__textarea-placeholder"
          maxlength="300"
          auto-height
          fixed
          :adjust-position="false"
          :cursor-spacing="cursorSpacing"
          @input="onInput"
        />
        <view class="s-comment-sheet__actions">
          <button class="s-comment-sheet__button s-comment-sheet__button--ghost sl-button sl-button--secondary" @tap="emit('close')">
            取消
          </button>
          <button
            class="s-comment-sheet__button s-comment-sheet__button--primary sl-button sl-button--primary"
            :disabled="submitting"
            @tap="emit('submit')"
          >
            <s-inline-loading v-if="submitting" text="发布中" light />
            <text v-else>发布</text>
          </button>
        </view>
      </template>
      <view v-else class="s-comment-sheet__actions" style="margin-top: 20rpx;">
        <button
          class="s-comment-sheet__button s-comment-sheet__button--ghost sl-button sl-button--secondary"
          style="width: 100%"
          @tap="emit('close')"
        >
          返回
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, shallowRef, triggerRef, watch } from 'vue';
import type { CommentItem } from '@/modules/comment/types/comment';

interface CommentNode extends CommentItem {
  replies: CommentItem[];
}

const props = defineProps<{
  visible: boolean;
  subtitle: string;
  loading: boolean;
  submitting: boolean;
  comments: CommentItem[];
  replyTarget: CommentItem | null;
  modelValue: string;
  canComment: boolean;
  isLoggedIn: boolean;
  currentUserId?: number;
  keyboardVisible: boolean;
  cursorSpacing: number;
  sheetStyle: Record<string, string>;
  themeClass?: string;
}>();

const emit = defineEmits<{
  close: [];
  submit: [];
  delete: [id: number];
  reply: [item: CommentItem];
  cancelReply: [];
  'update:modelValue': [value: string];
}>();

const expandedIds = shallowRef<Set<number>>(new Set());

watch(
  () => props.visible,
  (visible) => {
    if (!visible && expandedIds.value.size > 0) {
      expandedIds.value.clear();
      triggerRef(expandedIds);
    }
  }
);

const placeholder = computed(() => {
  if (props.replyTarget) {
    const targetNickname = props.replyTarget.userNickname || props.replyTarget.nickname || '读者';
    return `回复 @${targetNickname}:`;
  }
  return '写下你的看法...';
});

const commentTree = computed<CommentNode[]>(() => {
  const rootComments: CommentNode[] = [];
  const childComments: CommentItem[] = [];

  props.comments.forEach((item) => {
    if (!item.parentCommentId) {
      rootComments.push({ ...item, replies: [] });
    } else {
      childComments.push(item);
    }
  });

  childComments.forEach((child) => {
    const root = rootComments.find((r) => r.id === child.floorCommentId);
    if (root) {
      root.replies.push(child);
    } else {
      rootComments.push({ ...child, replies: [] });
    }
  });

  rootComments.forEach((root) => {
    root.replies.sort((a, b) => {
      const aTime = a.createTime ? new Date(a.createTime).getTime() : 0;
      const bTime = b.createTime ? new Date(b.createTime).getTime() : 0;
      return aTime - bTime;
    });
  });

  return rootComments;
});

function formatDate(value?: string): string {
  return value ? value.slice(0, 10) : '刚刚';
}

function isExpanded(id: number): boolean {
  return expandedIds.value.has(id);
}

function toggleExpanded(id: number): void {
  if (expandedIds.value.has(id)) {
    expandedIds.value.delete(id);
  } else {
    expandedIds.value.add(id);
  }
  triggerRef(expandedIds);
}

function handleCommentTap(item: CommentItem): void {
  if (!props.canComment) return;
  emit('reply', item);
}

function onInput(event: unknown): void {
  const detail = (event as { detail?: { value?: string } } | undefined)?.detail;
  emit('update:modelValue', detail?.value ?? '');
}
</script>

<style lang="scss" scoped>
.s-comment-sheet__mask {
  --sheet-primary: var(--sl-color-primary, #3b59ff);
  --sheet-primary-soft: var(--sl-color-primary-soft, #8f70ff);
  --sheet-text-main: var(--sl-text-main, #111827);
  --sheet-text-sub: var(--sl-text-sub, rgba(75, 85, 99, 0.68));

  position: fixed;
  inset: 0;
  z-index: 120;
  display: flex;
  align-items: flex-end;
  background: rgba(17, 24, 39, 0.54);

  &.sl-theme--dark {
    .s-comment-sheet {
      background: linear-gradient(180deg, var(--sl-surface-bg), var(--sl-page-bg));
      border-top: 1rpx solid var(--sl-border-light);
      box-shadow:
        inset 0 2rpx 0 rgba(255, 255, 255, 0.08),
        0 -18rpx 48rpx rgba(0, 0, 0, 0.34);
    }

    .s-comment-sheet__textarea {
      color: var(--sl-text-main);
      background: var(--sl-input-bg);
      border-color: var(--sl-control-border);
    }

    .s-comment-sheet__button--ghost {
      color: var(--sl-text-sub);
      background: var(--sl-control-bg);
      border-color: var(--sl-border-light);
    }

    .s-comment-sheet__reply-box {
      background: rgba(255, 255, 255, 0.025);
      border-color: rgba(255, 255, 255, 0.04);
    }

    .s-comment-sheet__reply-item {
      border-bottom-color: rgba(255, 255, 255, 0.04);
    }

    .s-comment-sheet__reply-indicator {
      background: rgba(105, 129, 255, 0.12);
      border-color: rgba(105, 129, 255, 0.2);
    }
  }
}

.s-comment-sheet {
  position: relative;
  width: 100%;
  max-height: 78vh;
  padding: $space-lg $space-md $space-lg;
  border-radius: 36rpx 36rpx 0 0;
  background: linear-gradient(180deg, var(--sl-control-bg-strong), var(--sl-page-bg));
  display: flex;
  flex-direction: column;

  &--keyboard {
    padding-bottom: $space-lg;
  }

  &__head,
  &__comment-head,
  &__actions {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $space-sm;
  }

  &__title {
    color: var(--sheet-text-main);
    font-size: 32rpx;
    font-weight: 700;
  }

  &__subtitle,
  &__close,
  &__comment-head,
  &__empty {
    color: var(--sheet-text-sub);
    font-size: 22rpx;
  }

  &__empty {
    padding: $space-lg 0;
    text-align: center;
  }

  &__list {
    height: 42vh;
    margin-top: $space-md;
    padding-right: 8rpx;
  }

  &__comment {
    padding: $space-sm 0;
    border-bottom: 1rpx solid var(--sl-border-light, rgba(17, 24, 39, 0.06));

    &:last-child {
      border-bottom: none;
    }
  }

  &__comment-content {
    margin-top: 8rpx;
    color: var(--sheet-text-main);
    font-size: 26rpx;
    line-height: 1.7;
  }

  &__nickname {
    color: var(--sheet-text-main);
    font-weight: 700;
    font-size: 26rpx;
  }

  &__meta {
    display: flex;
    align-items: center;
    gap: $space-sm;
  }

  &__time {
    color: var(--sheet-text-sub);
    font-size: 22rpx;
  }

  &__reply-btn {
    color: var(--sheet-primary);
    font-size: 22rpx;
    font-weight: 600;
  }

  &__delete-btn {
    color: $color-danger;
    font-size: 22rpx;
    font-weight: 600;
  }

  &__reply-box {
    margin-top: $space-sm;
    margin-left: 20rpx;
    padding: 16rpx 20rpx;
    border-radius: $radius-md;
    background: rgba(0, 0, 0, 0.024);
    border: 1rpx solid rgba(17, 24, 39, 0.03);
  }

  &__reply-item {
    padding: 10rpx 0;
    border-bottom: 1rpx dashed rgba(17, 24, 39, 0.04);

    &:last-of-type {
      border-bottom: none;
    }
  }

  &__reply-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $space-xs;
  }

  &__reply-users {
    display: flex;
    align-items: center;
    gap: 8rpx;
    font-size: 24rpx;
  }

  &__reply-author {
    color: var(--sheet-text-main);
    font-weight: 700;
  }

  &__reply-arrow {
    font-size: 18rpx;
    color: var(--sheet-text-sub);
    margin: 0 4rpx;
  }

  &__reply-target {
    color: var(--sheet-primary);
    font-weight: 600;
  }

  &__reply-meta {
    display: flex;
    align-items: center;
    gap: 12rpx;
  }

  &__reply-time {
    color: var(--sheet-text-sub);
    font-size: 20rpx;
  }

  &__reply-action {
    color: var(--sheet-primary);
    font-size: 20rpx;
    font-weight: 600;
  }

  &__reply-delete {
    color: $color-danger;
    font-size: 20rpx;
    font-weight: 600;
  }

  &__reply-content {
    margin-top: 6rpx;
    color: var(--sheet-text-main);
    font-size: 24rpx;
    line-height: 1.6;
  }

  &__reply-toggle {
    display: flex;
    align-items: center;
    padding-top: 10rpx;
  }

  &__reply-toggle-text {
    font-size: 22rpx;
    color: var(--sheet-primary);
    font-weight: 700;
  }

  &__reply-indicator {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 16rpx;
    padding: 12rpx 20rpx;
    border-radius: $radius-sm;
    background: rgba(59, 89, 255, 0.08);
    border: 1rpx solid rgba(59, 89, 255, 0.15);
  }

  &__reply-indicator-text {
    font-size: 24rpx;
    color: var(--sheet-primary);
    font-weight: 600;
  }

  &__reply-indicator-close {
    font-size: 26rpx;
    color: var(--sheet-primary);
    padding: 0 8rpx;
  }

  &__textarea {
    width: 100%;
    min-height: 148rpx;
    margin-top: $space-md;
    padding: $space-md;
    box-sizing: border-box;
    border-radius: $radius-lg;
    background: rgba(31, 41, 55, 0.04);
    border: 1rpx solid rgba(17, 24, 39, 0.08);
    font-size: 28rpx;
    color: var(--sheet-text-main);
  }

  &__textarea-placeholder {
    color: var(--sl-text-muted);
  }

  &__actions {
    margin-top: $space-md;
  }

  &__button {
    flex: 1;
    min-height: 84rpx;
    line-height: 84rpx;
    border-radius: 999rpx;
    font-size: 28rpx;

    &::after {
      border: none;
    }

    &:active {
      transform: scale(0.98);
    }

    &--ghost {
      color: var(--sheet-text-sub);
      background: var(--sl-control-bg);
      border: 1rpx solid rgba(229, 231, 235, 0.9);
    }

    &--primary {
      background: linear-gradient(135deg, var(--sheet-primary), var(--sheet-primary-soft));
      color: #fff;
      box-shadow: 0 14rpx 32rpx rgba(59, 89, 255, 0.2);
    }
  }
}
</style>
