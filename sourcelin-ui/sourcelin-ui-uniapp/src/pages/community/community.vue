<template>
  <view class="community" :class="themeStore.themeClass">
    <s-loading :visible="loading && currentItems.length === 0" />

    <!-- 固定 Tab 切换器 -->
    <view class="community__tabbar">
      <view class="community__tabs">
        <view
          v-for="tab in tabs"
          :key="tab.key"
          class="community__tab"
          :class="{ 'community__tab--active': activeTab === tab.key }"
          @tap="switchTab(tab.key)"
        >
          {{ tab.text }}
        </view>
      </view>
      <view
        v-if="canPublishCommunity"
        class="community__fab"
        :aria-label="activeTab === 'says' ? '发说说' : '写树洞'"
        @tap="goPublish"
      >
        <uni-icons type="plusempty" size="22" color="currentColor" />
      </view>
    </view>

    <view class="community__body">
      <s-empty v-if="isEmpty" :text="emptyText" />

      <view v-else class="community__list">
        <view
          v-for="item in currentItems"
          :key="`${activeTab}-${item.id}`"
          class="community__card"
          :class="`community__card--${activeTab}`"
        >
          <view class="community__card-head">
            <view class="community__identity" @tap="openUserHome(item)">
              <view class="community__avatar">
                <image
                  v-if="getCardView(item).avatar"
                  class="community__avatar-img"
                  :src="getCardView(item).avatar"
                  mode="aspectFill"
                  @error="onImageError(getCardView(item).avatar)"
                />
                <text v-else>{{ getCardView(item).author.slice(0, 1) }}</text>
              </view>
              <view>
                <view class="community__author-row">
                  <text class="community__author">{{ getCardView(item).author }}</text>
                  <text v-if="activeTab === 'treeholes'" class="community__inline-badge">主帖</text>
                </view>
                <view class="community__time">{{ getCardView(item).timeText }}</view>
              </view>
            </view>
            <view v-if="showCommunityActions" class="community__more" @tap="openItemAction(item)">···</view>
          </view>
          <view class="community__content" :class="{ 'community__content--treehole': activeTab === 'treeholes' }">
            <text>{{ item.content }}</text>
          </view>

          <view
            v-if="activeTab === 'says' && getCardView(item).images.length > 0"
            class="community__media"
            :class="{
              'community__media--single': getCardView(item).images.length === 1,
              'community__media--multi': getCardView(item).images.length > 1
            }"
          >
            <view
              v-for="(image, index) in getCardView(item).galleryImages"
              :key="`${item.id}-image-${index}`"
              class="community__media-item"
              :class="{ 'community__media-item--featured': index === 0 && getCardView(item).images.length >= 3 }"
              @tap="previewImages(getCardView(item).images, index)"
            >
              <image class="community__media-img" :src="image" mode="aspectFill" lazy-load @error="onImageError(image)" />
              <view
                v-if="index === getCardView(item).galleryImages.length - 1 && getCardView(item).extraImageCount > 0"
                class="community__media-more"
              >
                +{{ getCardView(item).extraImageCount }}
              </view>
            </view>
          </view>

          <view v-if="showCommunityActions" class="community__actions">
            <view
              class="community__action"
              :class="{ 'community__action--active': isLiked(item) }"
              @tap="toggleLike(item)"
            >
              <uni-icons type="hand-up" size="15" color="currentColor" />
              <text>{{ item.likeCount || 0 }}</text>
            </view>
            <view
              class="community__action"
              :class="{ 'community__action--active': isCollected(item) }"
              @tap="toggleCollect(item)"
            >
              <uni-icons type="star" size="15" color="currentColor" />
              <text>{{ item.collectCount || 0 }}</text>
            </view>
            <view class="community__action" @tap="openCommentSheet(item)">
              <uni-icons type="chatbubble" size="15" color="currentColor" />
              <text>{{ activeTab === 'treeholes' ? '讨论' : '' }}{{ item.commentCount || 0 }}</text>
            </view>
          </view>
        </view>

        <s-loading
          v-if="loading && currentItems.length > 0"
          :visible="true"
          :fullpage="false"
          compact
          text="正在加载更多..."
        />
        <view v-else-if="finished && currentItems.length > 0" class="community__footer">已经到底了</view>
      </view>
    </view>

    <s-back-to-top :visible="backToTopVisible" />

    <view class="s-liquid-tabbar">
      <view class="s-liquid-tabbar__shell">
        <view
          v-for="item in visibleLiquidTabItems"
          :key="item.path"
          class="s-liquid-tabbar__item"
          :class="{ 's-liquid-tabbar__item--active': item.path === activeTabPath }"
          @tap="switchLiquidTab(item.path, activeTabPath)"
        >
          <view class="s-liquid-tabbar__icon-wrap">
            <image
              class="s-liquid-tabbar__icon"
              :src="item.path === activeTabPath ? item.activeIcon : item.icon"
              mode="aspectFit"
            />
          </view>
          <text class="s-liquid-tabbar__text">{{ item.text }}</text>
        </view>
      </view>
    </view>

    <s-comment-sheet
      v-model="commentContent"
      :visible="commentSheetVisible"
      :subtitle="activeCommentLabel"
      :loading="commentLoading"
      :submitting="commentSubmitting"
      :comments="commentList"
      :reply-target="replyTarget"
      :can-comment="canUseComments"
      :is-logged-in="userStore.isLoggedIn"
      :current-user-id="currentUserId"
      :keyboard-visible="keyboardVisible"
      :cursor-spacing="keyboardCursorSpacing"
      :sheet-style="commentSheetStyle"
      :theme-class="themeStore.themeClass"
      @close="closeCommentSheet"
      @submit="submitComment"
      @delete="handleDeleteComment"
      @reply="handleReplyComment"
      @cancel-reply="cancelReply"
    />
  </view>
</template>

<script setup lang="ts">
import { useCommunityPage } from '@/modules/community/composables/useCommunityPage';

const {
  userStore,
  themeStore,
  tabs,
  activeTab,
  activeTabPath,
  visibleLiquidTabItems,
  switchLiquidTab,
  loading,
  finished,
  isEmpty,
  emptyText,
  currentItems,
  canUseComments,
  canPublishCommunity,
  showCommunityActions,
  currentUserId,
  backToTopVisible,
  commentSheetVisible,
  commentLoading,
  commentSubmitting,
  commentContent,
  commentList,
  replyTarget,
  activeCommentLabel,
  keyboardCursorSpacing,
  keyboardVisible,
  commentSheetStyle,
  getCardView,
  onImageError,
  previewImages,
  goPublish,
  openUserHome,
  isLiked,
  isCollected,
  switchTab,
  openCommentSheet,
  closeCommentSheet,
  handleReplyComment,
  cancelReply,
  submitComment,
  handleDeleteComment,
  toggleLike,
  toggleCollect,
  openItemAction
} = useCommunityPage();
</script>

<style lang="scss" scoped>
.community {
  --community-primary: var(--sl-color-primary);
  --community-primary-soft: var(--sl-color-primary-soft);
  --community-text-main: var(--sl-text-main);
  --community-text-sub: var(--sl-text-sub);
  --community-glass-pure: var(--sl-bg-glass-pure);
  --community-glass-tint: var(--sl-bg-glass-tint);
  --community-glass-border: rgba(255, 255, 255, 0.72);
  --community-glass-highlight: rgba(255, 255, 255, 0.86);
  --community-shadow: rgba(17, 24, 39, 0.08);

  min-height: 100vh;
  padding-bottom: calc(172rpx + env(safe-area-inset-bottom));
  background: var(--sl-page-bg, #f5f7fb);
  transition: background-color 0.24s ease;

  &.sl-theme--dark {
    --community-glass-border: var(--sl-border-light);
    --community-glass-highlight: rgba(255, 255, 255, 0.08);
    --community-shadow: rgba(0, 0, 0, 0.18);
    background: var(--sl-page-bg, #09090b);

    .community__tabbar {
      background: var(--sl-page-bg, #080d18);
      border-bottom-color: var(--sl-border-light);
    }

    .community__tabs {
      background: linear-gradient(145deg, rgba(24, 27, 38, 0.92), rgba(18, 20, 28, 0.88));
      border-color: var(--sl-border-light);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.06),
        0 8rpx 20rpx rgba(0, 0, 0, 0.24);
    }

    .community__fab {
      background:
        linear-gradient(145deg, rgba(18, 27, 46, 0.66), rgba(18, 27, 46, 0.4)),
        rgba(105, 129, 255, 0.08);
      border-color: rgba(105, 129, 255, 0.2);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.08),
        0 6rpx 16rpx rgba(105, 129, 255, 0.1);
    }

    .community__fab:active {
      background:
        linear-gradient(145deg, rgba(18, 27, 46, 0.74), rgba(18, 27, 46, 0.52)),
        rgba(105, 129, 255, 0.12);
    }

    .community__card {
      background: var(--sl-card-glass-bg);
      border-color: var(--sl-border-light);
      box-shadow: var(--sl-shadow-soft);
    }

    .community__card:active {
      transform: scale(0.985);
      background: rgba(47, 61, 85, 0.7);
    }

    .community__time,
    .community__more {
      color: var(--sl-text-sub);
    }

    .community__author,
    .community__content {
      color: var(--sl-text-main);
      text-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.5);
    }
  }

  &__tabbar {
    position: sticky;
    top: 0;
    z-index: 80;
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 16rpx 30rpx 12rpx;
    background: var(--sl-page-bg, #f5f7fb);
    border-bottom: 1rpx solid var(--sl-border-glass);
    transition: background-color 0.24s ease, border-color 0.24s ease;
  }

  &__tabs {
    position: relative;
    display: flex;
    flex: 1;
    min-width: 0;
    padding: 8rpx;
    border-radius: 999rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.94), rgba(255, 255, 255, 0.82)),
      var(--community-glass-tint);
    border: 1rpx solid rgba(255, 255, 255, 0.88);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.96),
      0 8rpx 20rpx rgba(17, 24, 39, 0.06);

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(14rpx) saturate(1.2);
    -webkit-backdrop-filter: blur(14rpx) saturate(1.2);
    /* #endif */
  }

  &__tab {
    flex: 1;
    min-height: 74rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    color: var(--community-text-sub);
    font-size: 28rpx;
    font-weight: 700;
    position: relative;
    border-radius: 999rpx;
    transition: transform 0.25s cubic-bezier(0.25, 1, 0.5, 1), background-color 0.25s ease, color 0.25s ease, box-shadow 0.25s ease;

    &--active {
      color: var(--community-primary);
      background:
        linear-gradient(135deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
        rgba(59, 89, 255, 0.08);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
        0 10rpx 28rpx rgba(59, 89, 255, 0.14);
    }
  }

  &__tab:active {
    transform: scale(0.97);
  }

  &__body {
    position: relative;
    margin-top: 0;
    padding: 20rpx 30rpx;
  }

  &__fab {
    flex-shrink: 0;
    width: 74rpx;
    height: 74rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 999rpx;
    color: var(--community-primary);
    background:
      linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
      rgba(59, 89, 255, 0.08);
    border: 1rpx solid rgba(59, 89, 255, 0.18);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
      0 8rpx 20rpx rgba(59, 89, 255, 0.1);
    transition: transform 0.2s cubic-bezier(0.25, 1, 0.5, 1), background-color 0.2s ease, box-shadow 0.2s ease;

    /* #ifdef H5 || APP-PLUS */
    backdrop-filter: blur(14rpx) saturate(1.2);
    -webkit-backdrop-filter: blur(14rpx) saturate(1.2);
    /* #endif */

    &:active {
      transform: scale(0.92);
      background:
        linear-gradient(145deg, rgba(255, 255, 255, 0.92), rgba(255, 255, 255, 0.58)),
        rgba(59, 89, 255, 0.12);
      box-shadow:
        inset 0 1rpx 0 rgba(255, 255, 255, 0.94),
        0 6rpx 14rpx rgba(59, 89, 255, 0.08);
    }
  }

  &__list {
    display: flex;
    flex-direction: column;
    gap: 28rpx;
  }

  &__card {
    position: relative;
    overflow: hidden;
    padding: 34rpx 30rpx 30rpx;
    border-radius: 38rpx;
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      var(--community-glass-pure);
    border: 1rpx solid var(--sl-border-glass);
    box-shadow:
      inset 0 1rpx 0 var(--community-glass-highlight),
      0 18rpx 44rpx var(--community-shadow);
    transition: transform 0.2s ease, box-shadow 0.2s ease;
  }

  &__card--treeholes {
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      var(--sl-bg-glass-tint);
    border-color: var(--sl-border-glass);
  }

  &__card::after {
    content: '';
    position: absolute;
    left: 24rpx;
    right: 24rpx;
    top: 12rpx;
    height: 44rpx;
    border-radius: 999rpx;
    background: linear-gradient(180deg, var(--sl-panel-highlight), rgba(255, 255, 255, 0));
    pointer-events: none;
  }

  &__card-head,
  &__actions {
    display: flex;
    gap: $space-sm;
  }

  &__card-head {
    position: relative;
    z-index: 1;
    align-items: flex-start;
    justify-content: space-between;
  }

  &__identity {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  &__identity:active {
    transform: scale(0.985);
  }

  &__more {
    min-width: 54rpx;
    height: 54rpx;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    background: var(--sl-control-bg);
    color: rgba(75, 85, 99, 0.82);
    font-size: 28rpx;
    line-height: 1;
    flex-shrink: 0;
  }

  &__avatar {
    width: 64rpx;
    height: 64rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 22rpx;
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      rgba(59, 89, 255, 0.08);
    border: 1rpx solid var(--sl-border-glass);
    color: var(--community-primary);
    font-size: 28rpx;
    font-weight: 800;
    overflow: hidden;
    flex-shrink: 0;
  }

  &__avatar-img {
    width: 100%;
    height: 100%;
    border-radius: inherit;
    display: block;
  }

  &__author-row {
    display: flex;
    align-items: center;
    gap: 10rpx;
  }

  &__author {
    color: var(--community-text-main);
    font-size: 29rpx;
    font-weight: 700;
  }

  &__inline-badge {
    padding: 4rpx 12rpx;
    border-radius: 999rpx;
    color: var(--community-primary);
    background: rgba(59, 89, 255, 0.08);
    border: 1rpx solid rgba(59, 89, 255, 0.1);
    font-size: 19rpx;
    font-weight: 700;
  }

  &__time {
    margin-top: 4rpx;
    color: rgba(75, 85, 99, 0.62);
    font-size: 22rpx;
  }

  &__content {
    position: relative;
    z-index: 1;
    margin-top: 22rpx;
    padding: 24rpx;
    border-radius: 26rpx;
    background: var(--sl-bg-glass-tint);
    border: 1rpx solid var(--sl-border-glass);
    color: var(--community-text-main);
    font-size: 30rpx;
    line-height: 1.75;
  }

  &__content--treehole {
    margin-top: 24rpx;
    padding: 28rpx 32rpx;
    border-radius: 12rpx 32rpx 32rpx;
    background:
      linear-gradient(135deg, rgba(59, 89, 255, 0.06) 0%, rgba(143, 112, 255, 0.06) 100%),
      linear-gradient(180deg, var(--sl-panel-highlight), var(--sl-panel-lowlight));
    border: 1rpx solid var(--sl-border-glass);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
      0 10rpx 28rpx rgba(59, 89, 255, 0.02);
  }

  &__content--treehole::before {
    content: '';
    position: absolute;
    left: 24rpx;
    top: -12rpx;
    width: 22rpx;
    height: 22rpx;
    border-radius: 6rpx 0 0;
    background: var(--sl-control-bg-strong);
    border-left: 1rpx solid var(--sl-border-glass);
    border-top: 1rpx solid var(--sl-border-glass);
    transform: rotate(45deg);
  }

  &__media {
    position: relative;
    z-index: 1;
    margin-top: 18rpx;
    display: grid;
    gap: 10rpx;
  }

  &__media--single {
    grid-template-columns: 1fr;
  }

  &__media--multi {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  &__media-item {
    position: relative;
    overflow: hidden;
    min-height: 212rpx;
    border-radius: 24rpx;
    background: var(--sl-bg-glass-tint);
    border: 1rpx solid var(--sl-border-glass);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.72),
      0 12rpx 28rpx rgba(17, 24, 39, 0.06);
  }

  &__media-item:active {
    transform: scale(0.985);
  }

  &__media--single &__media-item {
    min-height: 360rpx;
  }

  &__media-item--featured {
    grid-column: span 2;
    min-height: 320rpx;
  }

  &__media-img {
    width: 100%;
    height: 100%;
    min-height: inherit;
    display: block;
  }

  &__media-more {
    position: absolute;
    inset: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 34rpx;
    font-weight: 800;
    letter-spacing: 0.08em;
    background: rgba(17, 24, 39, 0.48);
  }

  &__actions {
    position: relative;
    z-index: 1;
    justify-content: space-between;
    margin-top: 20rpx;
  }

  &__action {
    flex: 1;
    min-height: 70rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    border-radius: 999rpx;
    background: var(--sl-control-bg);
    border: 1rpx solid var(--sl-control-border);
    color: var(--community-text-sub);
    font-size: 24rpx;
    font-weight: 700;
    box-shadow: 0 4rpx 10rpx rgba(17, 24, 39, 0.01);
    transition: transform 0.2s ease, background-color 0.2s ease, color 0.2s ease;
  }

  &__action:active {
    transform: scale(0.96);
  }

  &__action--active {
    color: var(--community-primary);
    background:
      linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
      rgba(59, 89, 255, 0.12);
    border-color: rgba(59, 89, 255, 0.26);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 8rpx 20rpx rgba(59, 89, 255, 0.08);
  }

  &__footer {
    text-align: center;
    color: $color-text-tertiary;
    font-size: 24rpx;
    padding: $space-md 0;
  }
}
</style>
