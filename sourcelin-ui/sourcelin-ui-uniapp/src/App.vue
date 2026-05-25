<script setup lang="ts">
/**
 * App.vue：全局生命周期入口
 * 业务初始化（启动校验、token 还原、未读拉取）放在这里
 */
import { onLaunch, onShow, onError } from '@dcloudio/uni-app';
import { useUserStore } from '@/stores/user';
import { useAppStore } from '@/stores/app';

const userStore = useUserStore();
const appStore = useAppStore();

onLaunch((options) => {
  // 系统信息收集（自定义导航栏需要）
  appStore.collectSystemInfo();
  // Token 还原
  userStore.restoreFromStorage();
  appStore.markLaunchReady();
  if (process.env.NODE_ENV !== 'production') {
    console.info('[App][onLaunch]', options);
  }
});

onShow(() => {
  // 冷启动后再次进入，可在此刷新 token 时效与未读
  if (userStore.isLoggedIn) {
    // appStore.refreshUnread();
  }
});

onError((err) => {
  console.error('[App][onError]', err);
});
</script>

<style lang="scss">
/* 全局样式：仅放真正全局的内容 */
page {
  --sl-color-primary: #3b59ff;
  --sl-color-primary-soft: #8f70ff;
  --sl-glow-a: rgba(59, 89, 255, 0.045);
  --sl-glow-b: rgba(29, 209, 161, 0.035);
  --sl-bg-glass-pure: rgba(255, 255, 255, 0.52);
  --sl-bg-glass-tint: rgba(255, 255, 255, 0.32);
  --sl-text-main: #111827;
  --sl-text-sub: #4b5563;

  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.36), rgba(248, 250, 252, 0.76) 42%),
    radial-gradient(circle at -14% 0%, var(--sl-glow-a), rgba(255, 255, 255, 0) 34%),
    radial-gradient(circle at 112% 22%, var(--sl-glow-b), rgba(255, 255, 255, 0) 32%),
    $color-bg;
  color: $color-text;
  font-size: 28rpx;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.5;
}

/* 通用容器 */
.s-container {
  padding: $space-md $space-md calc(172rpx + env(safe-area-inset-bottom));
}

.s-card {
  position: relative;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.56), rgba(255, 255, 255, 0.28)),
    var(--sl-bg-glass-pure);
  border: 1rpx solid rgba(255, 255, 255, 0.7);
  border-radius: 28rpx;
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.78),
    inset 0 -1rpx 0 rgba(59, 89, 255, 0.05),
    0 10rpx 30rpx -10rpx rgba(31, 38, 135, 0.1);
  padding: $space-md;
  overflow: hidden;

  /* #ifdef H5 || APP-PLUS */
  backdrop-filter: blur(16rpx) saturate(1.28);
  -webkit-backdrop-filter: blur(16rpx) saturate(1.28);
  /* #endif */
}

.s-card::after {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: inherit;
  pointer-events: none;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, 0.48), rgba(255, 255, 255, 0) 34%),
    linear-gradient(315deg, rgba(59, 89, 255, 0.07), rgba(255, 255, 255, 0) 38%);
}

button[class]::after {
  border: none;
}

button[class] {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  border: none;
  background: transparent;
  overflow: hidden;
  transition:
    transform 0.2s cubic-bezier(0.25, 1, 0.5, 1),
    box-shadow 0.2s ease,
    background-color 0.2s ease,
    border-color 0.2s ease,
    opacity 0.2s ease;
}

button[class][disabled] {
  opacity: 0.68;
  transform: none !important;
}

.sl-button {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  min-height: 84rpx;
  padding: 0 30rpx;
  border-radius: 999rpx;
  font-size: 28rpx;
  font-weight: 700;
  line-height: 1;
  letter-spacing: 0.01em;
  overflow: hidden;
  transition:
    transform 0.2s cubic-bezier(0.25, 1, 0.5, 1),
    box-shadow 0.2s ease,
    background-color 0.2s ease,
    border-color 0.2s ease,
    opacity 0.2s ease;
}

.sl-button:active {
  transform: scale(0.98);
}

.sl-button--sm {
  min-height: 68rpx;
  padding: 0 22rpx;
  font-size: 24rpx;
}

.sl-button--primary {
  background: linear-gradient(135deg, var(--sl-color-primary), var(--sl-color-primary-soft));
  color: #fff;
  border: 1rpx solid rgba(255, 255, 255, 0.18);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.22),
    0 14rpx 32rpx rgba(59, 89, 255, 0.2);
}

.sl-button--primary:active {
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.18),
    0 8rpx 20rpx rgba(59, 89, 255, 0.14);
}

.sl-button--success {
  background: linear-gradient(135deg, #0f9f7f, #18c79c);
  color: #fff;
  border: 1rpx solid rgba(255, 255, 255, 0.16);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.22),
    0 14rpx 32rpx rgba(15, 159, 127, 0.18);
}

.sl-button--success:active {
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.18),
    0 8rpx 20rpx rgba(15, 159, 127, 0.12);
}

.sl-button--secondary {
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.82), rgba(255, 255, 255, 0.48)),
    rgba(255, 255, 255, 0.42);
  color: var(--sl-text-sub);
  border: 1rpx solid rgba(255, 255, 255, 0.84);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
    0 10rpx 24rpx rgba(17, 24, 39, 0.04);
}

.sl-button--secondary:active {
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.92), rgba(255, 255, 255, 0.58)),
    rgba(255, 255, 255, 0.52);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.94),
    0 6rpx 16rpx rgba(17, 24, 39, 0.03);
}

.sl-button--ghost {
  background: transparent;
  color: var(--sl-color-primary);
  border: 1rpx solid transparent;
  box-shadow: none;
}

.sl-button--ghost:active {
  background: rgba(59, 89, 255, 0.08);
}

.s-card--interactive {
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.s-card--interactive:active {
  transform: scale(0.985);
  box-shadow: 0 18rpx 46rpx -10rpx rgba(59, 89, 255, 0.16);
}

.s-liquid-tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 90;
  display: flex;
  justify-content: center;
  padding: 0 28rpx calc(18rpx + env(safe-area-inset-bottom));
  pointer-events: none;
}

.s-liquid-tabbar__shell {
  position: relative;
  width: 100%;
  max-width: 690rpx;
  height: 112rpx;
  display: flex;
  align-items: center;
  padding: 10rpx;
  border-radius: 46rpx;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.66), rgba(255, 255, 255, 0.34)),
    rgba(255, 255, 255, 0.28);
  border: 1rpx solid rgba(255, 255, 255, 0.72);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.86),
    inset 0 -1rpx 0 rgba(59, 89, 255, 0.08),
    0 22rpx 54rpx rgba(17, 24, 39, 0.12);
  overflow: hidden;
  pointer-events: auto;

  /* #ifdef H5 || APP-PLUS */
  backdrop-filter: blur(22rpx) saturate(1.4);
  -webkit-backdrop-filter: blur(22rpx) saturate(1.4);
  /* #endif */
}

.s-liquid-tabbar__shell::before {
  content: '';
  position: absolute;
  left: 18rpx;
  right: 18rpx;
  top: 8rpx;
  height: 36rpx;
  border-radius: 999rpx;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.62), rgba(255, 255, 255, 0));
  pointer-events: none;
}

.s-liquid-tabbar__item {
  position: relative;
  z-index: 1;
  flex: 1;
  min-width: 0;
  height: 92rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
  border-radius: 36rpx;
  color: rgba(75, 85, 99, 0.8);
  font-size: 21rpx;
}

.s-liquid-tabbar__item:active {
  transform: scale(0.94);
}

.s-liquid-tabbar__item--active {
  color: #1f4fff;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.74), rgba(255, 255, 255, 0.28)),
    rgba(59, 89, 255, 0.08);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
    0 10rpx 24rpx rgba(59, 89, 255, 0.12);
}

.s-liquid-tabbar__icon-wrap {
  width: 42rpx;
  height: 42rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.s-liquid-tabbar__icon {
  width: 42rpx;
  height: 42rpx;
  display: block;
}

.s-liquid-tabbar__text {
  line-height: 1.2;
  font-size: 21rpx;
  font-weight: 600;
}

.s-safe-bottom {
  padding-bottom: env(safe-area-inset-bottom);
}

.s-ellipsis {
  @include ellipsis;
}

.s-ellipsis-2 {
  @include ellipsis-multi(2);
}
</style>
