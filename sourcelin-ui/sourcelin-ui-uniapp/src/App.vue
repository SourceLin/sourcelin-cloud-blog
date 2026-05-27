<script setup lang="ts">
/**
 * App.vue：全局生命周期入口
 * 业务初始化（启动校验、token 还原、未读拉取）放在这里
 */
import { onLaunch, onShow, onError } from '@dcloudio/uni-app';
import { useUserStore } from '@/stores/user';
import { useAppStore } from '@/stores/app';
import { useThemeStore } from '@/stores/theme';

const userStore = useUserStore();
const appStore = useAppStore();
const themeStore = useThemeStore();

onLaunch((options) => {
  // 系统信息收集（自定义导航栏需要）
  appStore.collectSystemInfo();
  // 全局明暗外观主题恢复与适配
  themeStore.restoreFromStorage();
  // Token 还原
  userStore.restoreFromStorage();
  appStore.markLaunchReady();
  if (process.env.NODE_ENV !== 'production') {
    console.info('[App][onLaunch]', options);
  }
});

onShow(() => {
  // 当设置为跟随系统时，每次返回前台重新解析，确保响应系统明暗外观变化
  if (themeStore.initialized) {
    themeStore.resolveTheme();
  }
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
/* 默认浅色变量与基础设置 */
page {
  --sl-page-bg: #f5f7fb;
  --sl-color-primary: #3b59ff;
  --sl-color-primary-soft: #8f70ff;
  --sl-glow-a: rgba(59, 89, 255, 0.045);
  --sl-glow-b: rgba(29, 209, 161, 0.035);
  --sl-bg-glass-pure: rgba(255, 255, 255, 0.56);
  --sl-bg-glass-tint: rgba(255, 255, 255, 0.32);
  --sl-border-glass: rgba(255, 255, 255, 0.76);
  --sl-glass-shadow: 0 22rpx 54rpx rgba(17, 24, 39, 0.04);
  --sl-text-main: #111827;
  --sl-text-sub: #4b5563;
  --sl-text-muted: #86909c;
  --sl-panel-highlight: rgba(255, 255, 255, 0.56);
  --sl-panel-lowlight: rgba(255, 255, 255, 0.28);
  --sl-control-bg: rgba(255, 255, 255, 0.54);
  --sl-control-bg-strong: rgba(255, 255, 255, 0.82);
  --sl-control-border: rgba(255, 255, 255, 0.84);

  background: var(--sl-page-bg);
  color: var(--sl-text-main);
  font-size: 28rpx;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.5;
}

/* 核心主题类设计：符合液态玻璃 UI 视觉规范 */
.sl-theme--light {
  --sl-page-bg: #f5f7fb;
  --sl-glow-a: rgba(59, 89, 255, 0.045);
  --sl-glow-b: rgba(29, 209, 161, 0.035);
  --sl-bg-glass-pure: rgba(255, 255, 255, 0.56);
  --sl-bg-glass-tint: rgba(255, 255, 255, 0.32);
  --sl-border-glass: rgba(255, 255, 255, 0.76);
  --sl-glass-shadow: 0 22rpx 54rpx rgba(17, 24, 39, 0.04);
  --sl-text-main: #111827;
  --sl-text-sub: #4b5563;
  --sl-text-muted: #86909c;
  --sl-color-primary: #3b59ff;
  --sl-color-primary-soft: #8f70ff;
  --sl-panel-highlight: rgba(255, 255, 255, 0.56);
  --sl-panel-lowlight: rgba(255, 255, 255, 0.28);
  --sl-control-bg: rgba(255, 255, 255, 0.54);
  --sl-control-bg-strong: rgba(255, 255, 255, 0.82);
  --sl-control-border: rgba(255, 255, 255, 0.84);

  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.36), rgba(248, 250, 252, 0.76) 42%),
    radial-gradient(circle at -14% 0%, var(--sl-glow-a), rgba(255, 255, 255, 0) 34%),
    radial-gradient(circle at 112% 22%, var(--sl-glow-b), rgba(255, 255, 255, 0) 32%),
    #f5f7fb;
  color: var(--sl-text-main);
  transition: background-color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1), color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.sl-theme--dark {
  --sl-page-bg: #080d18;
  --sl-glow-a: rgba(89, 115, 255, 0.08);
  --sl-glow-b: rgba(143, 112, 255, 0.06);
  --sl-bg-glass-pure: rgba(18, 27, 46, 0.62);
  --sl-bg-glass-tint: rgba(18, 27, 46, 0.40);
  --sl-border-glass: rgba(154, 176, 255, 0.12);
  --sl-glass-shadow: 0 22rpx 54rpx rgba(0, 0, 0, 0.18);
  --sl-text-main: #f3f6ff;
  --sl-text-sub: #a8b2c8;
  --sl-text-muted: #687693;
  --sl-color-primary: #6981ff;
  --sl-color-primary-soft: #8f82ff;
  --sl-panel-highlight: rgba(129, 151, 221, 0.10);
  --sl-panel-lowlight: rgba(8, 13, 24, 0.12);
  --sl-control-bg: rgba(17, 26, 44, 0.72);
  --sl-control-bg-strong: rgba(26, 38, 62, 0.82);
  --sl-control-border: rgba(154, 176, 255, 0.16);

  background:
    linear-gradient(180deg, rgba(8, 13, 24, 0.36), rgba(8, 13, 24, 0.94) 42%),
    radial-gradient(circle at -14% 0%, var(--sl-glow-a), rgba(8, 13, 24, 0) 34%),
    radial-gradient(circle at 112% 22%, var(--sl-glow-b), rgba(8, 13, 24, 0) 32%),
    #080d18;
  color: var(--sl-text-main);
  transition: background-color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1), color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1);
}

/* 通用容器 */
.s-container {
  padding: $space-md $space-md calc(172rpx + env(safe-area-inset-bottom));
}

.s-card {
  position: relative;
  background:
    linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
    var(--sl-bg-glass-pure);
  border: 1rpx solid var(--sl-border-glass);
  border-radius: 28rpx;
  box-shadow:
    inset 0 1rpx 0 var(--sl-border-glass),
    inset 0 -1rpx 0 rgba(59, 89, 255, 0.05),
    var(--sl-glass-shadow);
  padding: $space-md;
  overflow: hidden;
  transition: background-color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1), border-color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1), box-shadow 0.24s cubic-bezier(0.25, 0.8, 0.25, 1);

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
  opacity: 1;
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

.sl-button,
.sl-button text,
.sl-button view,
.sl-button .uni-icons {
  color: inherit;
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

.sl-button--primary[disabled] {
  background: linear-gradient(135deg, rgba(127, 146, 255, 0.94), rgba(176, 160, 255, 0.92));
  color: rgba(255, 255, 255, 0.98);
  border-color: rgba(255, 255, 255, 0.12);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.18),
    0 8rpx 20rpx rgba(59, 89, 255, 0.08);
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

.sl-button--success[disabled] {
  background: linear-gradient(135deg, rgba(102, 191, 168, 0.94), rgba(138, 215, 190, 0.92));
  color: rgba(255, 255, 255, 0.98);
  border-color: rgba(255, 255, 255, 0.12);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.18),
    0 8rpx 20rpx rgba(15, 159, 127, 0.06);
}

.sl-button--secondary {
  background:
    linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg)),
    var(--sl-control-bg);
  color: var(--sl-text-sub);
  border: 1rpx solid var(--sl-control-border);
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

.sl-theme--dark .sl-button--secondary:active {
  background: rgba(31, 43, 67, 0.92);
  border-color: rgba(154, 176, 255, 0.22);
}

.sl-button--secondary[disabled] {
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.76), rgba(255, 255, 255, 0.5)),
    rgba(255, 255, 255, 0.44);
  color: rgba(75, 85, 99, 0.72);
  border-color: rgba(255, 255, 255, 0.74);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
    0 6rpx 16rpx rgba(17, 24, 39, 0.02);
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

.sl-button--danger {
  background: linear-gradient(145deg, rgba(255, 245, 247, 0.96), rgba(255, 234, 239, 0.84));
  color: #e54866;
  border: 1rpx solid rgba(229, 72, 102, 0.16);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
    0 10rpx 24rpx rgba(229, 72, 102, 0.08);
}

.sl-button--danger:active {
  background: linear-gradient(145deg, rgba(255, 239, 243, 0.98), rgba(255, 226, 232, 0.9));
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.94),
    0 6rpx 16rpx rgba(229, 72, 102, 0.06);
}

.sl-button--danger[disabled] {
  background: linear-gradient(145deg, rgba(255, 245, 247, 0.92), rgba(255, 238, 241, 0.88));
  color: rgba(229, 72, 102, 0.72);
  border-color: rgba(229, 72, 102, 0.1);
  box-shadow:
    inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
    0 6rpx 16rpx rgba(229, 72, 102, 0.04);
}

.sl-theme--dark .sl-button--danger {
  background: rgba(229, 72, 102, 0.12);
  border-color: rgba(229, 72, 102, 0.25);
  box-shadow: inset 0 1rpx 0 rgba(255, 255, 255, 0.05);
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
    linear-gradient(145deg, var(--sl-bg-glass-pure), var(--sl-panel-lowlight)),
    var(--sl-bg-glass-tint);
  border: 1rpx solid var(--sl-border-glass);
  box-shadow:
    inset 0 1rpx 0 var(--sl-border-glass),
    inset 0 -1rpx 0 rgba(59, 89, 255, 0.08),
    var(--sl-glass-shadow);
  overflow: hidden;
  pointer-events: auto;
  transition: background-color 0.24s ease, border-color 0.24s ease, box-shadow 0.24s ease;

  /* #ifdef H5 || APP-PLUS */
  backdrop-filter: blur(22rpx) saturate(1.4);
  -webkit-backdrop-filter: blur(22rpx) saturate(1.4);
  /* #endif */

  .sl-theme--dark & {
    background: rgba(8, 13, 24, 0.88);
  }
}

.s-liquid-tabbar__shell::before {
  content: '';
  position: absolute;
  left: 18rpx;
  right: 18rpx;
  top: 8rpx;
  height: 36rpx;
  border-radius: 999rpx;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.4), rgba(255, 255, 255, 0));
  pointer-events: none;

  .sl-theme--dark & {
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.06), rgba(255, 255, 255, 0));
  }
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
  color: var(--sl-text-muted);
  font-size: 21rpx;
  transition: color 0.24s ease;
}

.s-liquid-tabbar__item:active {
  transform: scale(0.94);
}

.s-liquid-tabbar__item--active {
  color: var(--sl-color-primary);
  background:
    linear-gradient(145deg, var(--sl-bg-glass-pure), rgba(255, 255, 255, 0.1)),
    rgba(59, 89, 255, 0.06);
  box-shadow:
    inset 0 1rpx 0 var(--sl-border-glass),
    0 10rpx 24rpx rgba(59, 89, 255, 0.08);

  .sl-theme--dark & {
    background: rgba(143, 112, 255, 0.08);
  }
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
