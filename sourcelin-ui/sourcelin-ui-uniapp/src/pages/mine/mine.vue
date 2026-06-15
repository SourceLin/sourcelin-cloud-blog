<template>
  <view class="mine" :class="themeStore.themeClass">
    <view class="mine__hero">
      <image class="mine__hero-bg" src="/static/header/beijing.jpg" mode="aspectFill" />
      <view class="mine__hero-mask" />
      <view class="mine__hero-body">
        <image class="mine__hero-logo" src="/static/logo/logo.png" mode="aspectFit" />
        <view class="mine__hero-title">SOURCELIN</view>
        <view class="mine__hero-subtitle">Blog Studio</view>
      </view>
    </view>

    <view class="mine__content">
      <view class="mine__profile" @tap="onTapProfile">
        <image class="mine__avatar" :src="avatarUrl" mode="aspectFill" />
        <view class="mine__profile-text">
          <view class="mine__nickname">{{ greetingText }}</view>
          <view class="mine__hint">{{ hintText }}</view>
          <view v-if="userStore.isLoggedIn && userStore.isBlogger" class="mine__quick-stats">
            <text>{{ userStore.userInfo?.articleCount || 0 }} 篇文章</text>
            <text>{{ userStore.userInfo?.followerCount || 0 }} 位关注者</text>
          </view>
        </view>
        <button class="mine__profile-action sl-button sl-button--primary sl-button--sm" @tap.stop="onTapProfile">
          {{ userStore.isLoggedIn ? '资料' : '登录' }}
        </button>
      </view>

      <view class="mine__banner">
        <image class="mine__banner-bg" src="/static/backgrounds/home-bg.jpg" mode="aspectFill" />
        <view class="mine__banner-mask" />
        <view class="mine__banner-title">欢迎来到 圆圈博客</view>
        <view class="mine__banner-desc">收藏好文、整理资料，在移动端轻松回访每一篇值得重读的内容。</view>
      </view>

      <view class="mine__section mine__section--assets">
        <view class="mine__section-head">
          <view class="mine__section-title">{{ assetsSectionTitle }}</view>
        </view>
        <view class="mine__asset-grid">
          <view
            v-for="entry in coreEntries"
            :key="entry.key"
            class="mine__asset-card"
            hover-class="mine__asset-card--hover"
            @tap="onTapEntry(entry.key)"
          >
            <view class="mine__asset-icon">
              <uni-icons :type="entry.icon" size="24" :color="entry.iconColor" />
              <view v-if="entry.key === 'messages' && unreadMessageCount > 0" class="mine__feature-badge">
                {{ unreadMessageCount > 99 ? '99+' : unreadMessageCount }}
              </view>
            </view>
            <view class="mine__asset-info">
              <view class="mine__asset-title">{{ entry.text }}</view>
              <view class="mine__asset-desc">{{ entry.desc }}</view>
            </view>
          </view>
        </view>
      </view>

      <view class="mine__section mine__section--tools">
        <view class="mine__section-head">
          <view class="mine__section-title">设置与服务</view>
        </view>
        <view class="mine__menu">
          <view
            v-for="entry in secondaryEntries"
            :key="entry.key"
            class="mine__menu-item"
            hover-class="mine__menu-item--hover"
            @tap="onTapEntry(entry.key)"
          >
            <view class="mine__menu-main">
              <view class="mine__menu-icon">
                <uni-icons :type="entry.icon" size="21" :color="entry.iconColor" />
              </view>
              <view class="mine__menu-copy">
                <view class="mine__menu-title">{{ entry.text }}</view>
                <view class="mine__menu-desc">{{ entry.desc }}</view>
              </view>
            </view>
            <uni-icons class="mine__menu-arrow" type="right" size="17" color="currentColor" />
          </view>
        </view>
      </view>

      <view v-if="userStore.isLoggedIn" class="mine__logout" @tap="onLogout">
        退出登录
      </view>

      <view class="mine__support">
        <image class="mine__support-logo" src="/static/logo/logo.png" mode="aspectFit" />
        <view class="mine__support-name">圆圈博客</view>
        <view class="mine__support-text">由 Sourcelin 提供技术支持</view>
      </view>
    </view>

    <view v-if="loginSheetVisible" class="mine-login" @tap="closeLoginSheet()">
      <view class="mine-login__sheet" @tap.stop>
        <view class="mine-login__handle" />
        <image class="mine-login__logo" src="/static/logo/logo.png" mode="aspectFit" />
        <view class="mine-login__title">欢迎加入 圆圈博客</view>
        <view class="mine-login__desc">登录后同步收藏、资料与阅读偏好。</view>

        <button class="mine-login__primary sl-button sl-button--primary" :disabled="wechatSubmitting" @tap="loginWithWechat">
          <s-inline-loading v-if="wechatSubmitting" text="微信登录中" light />
          <text v-else>微信一键登录</text>
        </button>

        <button class="mine-login__ghost sl-button sl-button--secondary" @tap="closeLoginSheet()">暂时跳过</button>

        <view class="mine-login__switch sl-button sl-button--ghost sl-button--sm" @tap="togglePasswordLogin">
          {{ showPasswordLogin ? '收起账号密码登录' : '绑定已有账号 / 登录' }}
        </view>

        <view v-if="showPasswordForm" class="mine-login__form">
          <view class="mine-login__field">
            <text class="mine-login__label">账号</text>
            <input
              v-model.trim="form.username"
              class="mine-login__input"
              placeholder="用户名 / 邮箱"
              placeholder-class="mine-login__placeholder"
            >
          </view>

          <view class="mine-login__field">
            <text class="mine-login__label">密码</text>
            <input
              v-model.trim="form.password"
              class="mine-login__input"
              password
              placeholder="请输入密码"
              placeholder-class="mine-login__placeholder"
              @confirm="submitPasswordLogin"
            >
          </view>

          <view v-if="captcha.captchaEnabled" class="mine-login__field mine-login__field--captcha">
            <view class="mine-login__captcha-input">
              <text class="mine-login__label">验证码</text>
              <input
                v-model.trim="form.captchaCode"
                class="mine-login__input"
                placeholder="输入验证码"
                placeholder-class="mine-login__placeholder"
                @confirm="submitPasswordLogin"
              >
            </view>
            <image
              v-if="captchaImage"
              class="mine-login__captcha"
              :src="captchaImage"
              mode="scaleToFill"
              @tap="loadCaptcha"
            />
          </view>

          <button
            class="mine-login__submit sl-button sl-button--primary"
            :disabled="submitting"
            @tap="submitPasswordLogin"
          >
            <s-inline-loading v-if="submitting" text="登录中" light />
            <text v-else>账号密码登录</text>
          </button>

          <view
            class="mine-login__bind-link sl-button sl-button--ghost sl-button--sm"
            :class="{ 'mine-login__bind-link--disabled': submitting }"
            @tap="submitBindLogin"
          >
            <s-inline-loading v-if="submitting" text="处理中" />
            <text v-else>登录并绑定微信</text>
          </view>
        </view>

        <view class="mine-login__agreement" @tap="toggleAgreement">
          <view class="mine-login__check" :class="{ 'mine-login__check--active': agreementChecked }" />
          <view class="mine-login__agreement-text">
            允许我们在必要场景下，合理使用您的个人信息，并同意
            <text class="mine-login__agreement-link" @tap.stop="openPolicyPage('privacy-policy')">《隐私政策》</text>
            、
            <text class="mine-login__agreement-link" @tap.stop="openPolicyPage('user-agreement')">《用户协议》</text>
            等内容
          </view>
        </view>
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
  </view>
</template>

<script setup lang="ts">
// 我的：登录态展示与基础入口（骨架）
import { computed, reactive, ref, watch } from 'vue';
import { onPageScroll, onShow } from '@dcloudio/uni-app';
import {
  bindMiniProgramAccount,
  fetchCaptcha,
  loginByMiniProgramCode,
  loginByPassword,
  logoutRemote,
  prepareMiniProgramBind
} from '@/shared/api/auth.api';
import type { CaptchaInfo, LoginToken } from '@/modules/auth/types/auth';
import { fetchUnreadMessageCount } from '@/modules/message/api/message.api';
import { fetchCurrentUserInfo } from '@/modules/user/api/user.api';
import { hideNativeTabbar, liquidTabItems, switchLiquidTab } from '@/shared/utils/liquid-tabbar';
import { replayPendingAction } from '@/shared/utils/pending-actions';
import { applyH5Seo, buildSeoTitle, extractSeoSummary } from '@/shared/utils/seo';
import { mapFrontUserInfo } from '@/shared/utils/user-mapper';
import { useUserStore } from '@/stores/user';
import { useThemeStore } from '@/stores/theme';
import { useCapabilityStore } from '@/stores/capability';
import { AUTH_LOGIN_SUCCESS_EVENT, reset401Guard, type LoginSuccessEventDetail } from '@/utils/auth';
import { showInfoToast, showSuccessToast } from '@/utils/feedback';
import { BusinessError } from '@/utils/error';
import { normalizeAssetUrl } from '@/utils/url';
import type { LegalArticleType } from '@/modules/site/types/legal';
import { useBackToTop } from '@/shared/composables/useBackToTop';
import { useMiniAccess } from '@/shared/composables/useMiniAccess';

const userStore = useUserStore();
const themeStore = useThemeStore();
const capabilityStore = useCapabilityStore();
const { can, resolveLiquidTabs } = useMiniAccess();
const { backToTopVisible, handlePageScroll } = useBackToTop();
const activeTabPath = 'pages/mine/mine';
const visibleLiquidTabItems = computed(() => resolveLiquidTabs(liquidTabItems));
const loginSheetVisible = ref(false);
const agreementChecked = ref(false);
const submitting = ref(false);
const wechatSubmitting = ref(false);
const showPasswordLogin = ref(false);
const postLoginTargetUrl = ref('');
const unreadMessageCount = ref(0);

const form = reactive({
  username: '',
  password: '',
  captchaCode: '',
  captchaUuid: ''
});

const captcha = reactive<CaptchaInfo>({
  captchaEnabled: true,
  uuid: '',
  img: ''
});

const displayName = computed(() =>
  userStore.isLoggedIn ? userStore.userInfo?.nickname || userStore.userInfo?.username || '已登录用户' : '未登录'
);

const greetingText = computed(() =>
  userStore.isLoggedIn ? `Hi，${displayName.value}` : 'Hi，读者，欢迎您'
);

const hintText = computed(() =>
  userStore.isLoggedIn
    ? (userStore.isBlogger ? '管理资料、文章与移动端创作入口' : '同步收藏、资料与基础阅读设置')
    : '登录后同步收藏、资料与阅读偏好'
);

const avatarUrl = computed(() => normalizeAssetUrl(userStore.userInfo?.avatar) || '/static/header/avatar.jpg');


const assetsSectionTitle = computed(() =>
  capabilityStore.capabilities.reviewSafeMode ? '我的功能' : '互动与创作'
);
const showPasswordForm = computed(() => showPasswordLogin.value);
const captchaImage = computed(() => {
  if (!captcha.img) return '';
  return captcha.img.startsWith('data:') ? captcha.img : `data:image/png;base64,${captcha.img}`;
});

watch([displayName, hintText], () => {
  applyH5Seo({
    title: buildSeoTitle('我的'),
    description: extractSeoSummary(
      userStore.isLoggedIn
        ? (userStore.isBlogger
          ? `${displayName.value} 的移动端主页，管理资料、文章内容与站点维护入口。`
          : `${displayName.value} 的移动端主页，管理资料、收藏与阅读偏好。`)
        : '登录后同步收藏、资料与阅读偏好。'
    ),
    keywords: ['我的', '用户中心', '收藏', '资料', userStore.isLoggedIn ? displayName.value : '登录']
  });
}, { immediate: true });

interface Entry {
  key: 'interactions' | 'follows' | 'profile' | 'settings' | 'articles' | 'about' | 'policies' | 'links' | 'navigation' | 'messages';
  text: string;
  desc: string;
  url: string;
  icon: string;
  iconColor: string;
}

const entries: Entry[] = [
  { key: 'interactions', text: '我的收藏', desc: '回看收藏过的文章与内容', url: '/pages-user/interactions/interactions?tab=collect', icon: 'star-filled', iconColor: '#FFB800' },
  { key: 'follows', text: '关注/粉丝', desc: '管理关注与回访', url: '/pages-user/follows/follows', icon: 'heart-filled', iconColor: '#FF5B75' },
  { key: 'profile', text: '个人资料', desc: '编辑昵称、头像与简介', url: '/pages-user/profile/profile', icon: 'person-filled', iconColor: '#3B59FF' },
  { key: 'settings', text: '体验设置', desc: '外观、推荐与订阅消息', url: '/pages-user/settings/settings', icon: 'gear-filled', iconColor: '#7C4DFF' },
  { key: 'articles', text: '我的文章', desc: '管理草稿与已发布内容', url: '/pages-user/articles/articles', icon: 'compose', iconColor: '#8F70FF' },
  { key: 'policies', text: '协议与政策', desc: '查看用户协议与隐私政策', url: '/pages-about/policies/policies', icon: 'compose', iconColor: '#3B59FF' },
  { key: 'about', text: '关于本站', desc: '查看站点介绍与作者信息', url: '/pages-about/index/index', icon: 'info-filled', iconColor: '#00B42A' },
  // { key: 'navigation', text: '网站导航', desc: '快速进入常用站点入口', url: '/pages-about/navigation/navigation', icon: 'location-filled', iconColor: '#FF7D00' },
  { key: 'links', text: '友情链接', desc: '浏览合作站点与推荐链接', url: '/pages-about/links/links', icon: 'link', iconColor: '#1DD1A1' },
  { key: 'messages', text: '消息中心', desc: '查看回复、点赞与系统通知', url: '/pages-messages/index/index', icon: 'chat-filled', iconColor: '#00D2D3' },
];
const coreEntryKeys: Entry['key'][] = ['interactions', 'articles', 'follows', 'messages'];
const coreEntries = computed(() => {
  return entries.filter((entry) => {
    if (!coreEntryKeys.includes(entry.key)) return false;

    if (entry.key === 'interactions') {
      return can('favoriteEnabled');
    }
    if (entry.key === 'articles') {
      return userStore.isBlogger && can('articlePublishEnabled');
    }
    if (entry.key === 'follows') {
      return userStore.isBlogger && can('followEnabled');
    }
    if (entry.key === 'messages') {
      return userStore.isBlogger && can('messageCenterEnabled');
    }
    return true;
  });
});

const secondaryEntries = computed(() => {
  return entries.filter((entry) => {
    if (coreEntryKeys.includes(entry.key)) return false;
    
    if (entry.key === 'profile') return can('profileEnabled');
    if (entry.key === 'settings') return can('settingsEnabled');
    if (entry.key === 'policies') return can('policyEnabled');
    if (entry.key === 'about') return can('aboutEnabled');
    if (entry.key === 'links') return can('friendLinkEnabled');
    if (entry.key === 'navigation') return can('navigationEnabled');
    return true;
  });
});

const loginRequiredKeys = computed<Entry['key'][]>(() => {
  const keys: Entry['key'][] = ['profile', 'interactions'];
  if (userStore.isBlogger) {
    keys.push('follows', 'articles', 'messages');
  }
  return keys;
});

onShow(async () => {
  await capabilityStore.waitUntilReady();
  hideNativeTabbar();
  themeStore.syncNativeArea();
  refreshCurrentUser();
});

onPageScroll(handlePageScroll);

async function refreshCurrentUser(): Promise<void> {
  if (!userStore.isLoggedIn) {
    unreadMessageCount.value = 0;
    return;
  }
  try {
    // skipAuthRedirect: 避免 401 时自动跳到登录页，改为本地静默清除
    const currentUser = await fetchCurrentUserInfo({ skipAuthRedirect: true, skipErrorToast: true });
    userStore.updateUserInfo(mapFrontUserInfo(currentUser));
    // 用户信息拉取成功后 token 必然有效，此时再拉未读数
    try {
      const unread = await fetchUnreadMessageCount();
      unreadMessageCount.value = unread.total || 0;
    } catch {
      // 未读数拉取失败不阻塞用户信息刷新
    }
  } catch (err) {
    // 401 时静默清除登录态，不触发自动跳转（避免在 mine 页反复跳登录）
    if (err instanceof BusinessError && err.httpStatus === 401) {
      userStore.logout();
      unreadMessageCount.value = 0;
      return;
    }
    // 网络异常等其他错误静默忽略
  }
}

function onTapProfile(): void {
  if (!userStore.isLoggedIn) {
    openLoginSheet('/pages-user/profile/profile');
    return;
  }
  uni.navigateTo({ url: '/pages-user/profile/profile' });
}

function onTapEntry(key: Entry['key']): void {
  const entry = entries.find((item) => item.key === key);
  if (!entry) return;
  if (!isEntryVisible(key)) return;
  if (!userStore.isLoggedIn && loginRequiredKeys.value.includes(key)) {
    openLoginSheet(entry.url);
    return;
  }
  uni.navigateTo({ url: entry.url });
}

function isEntryVisible(key: Entry['key']): boolean {
  if (key === 'interactions') {
    return can('favoriteEnabled');
  }
  if (key === 'articles') return userStore.isBlogger && can('articlePublishEnabled');
  if (key === 'follows') return userStore.isBlogger && can('followEnabled');
  if (key === 'messages') return userStore.isBlogger && can('messageCenterEnabled');
  if (key === 'profile') return can('profileEnabled');
  if (key === 'settings') return can('settingsEnabled');
  if (key === 'policies') return can('policyEnabled');
  if (key === 'about') return can('aboutEnabled');
  if (key === 'links') return can('friendLinkEnabled');
  if (key === 'navigation') return can('navigationEnabled');
  return true;
}

function openLoginSheet(targetUrl = ''): void {
  postLoginTargetUrl.value = targetUrl;
  loginSheetVisible.value = true;
}

function closeLoginSheet(force = false): void {
  if (!force && (submitting.value || wechatSubmitting.value)) return;
  loginSheetVisible.value = false;
  showPasswordLogin.value = false;
  if (!force) {
    postLoginTargetUrl.value = '';
  }
}

function toggleAgreement(): void {
  agreementChecked.value = !agreementChecked.value;
}

function ensureAgreement(): boolean {
  if (agreementChecked.value) return true;
  showInfoToast('请先阅读并同意协议');
  return false;
}

async function togglePasswordLogin(): Promise<void> {
  showPasswordLogin.value = !showPasswordLogin.value;
  if (showPasswordLogin.value && captcha.captchaEnabled && !captcha.img) {
    await loadCaptcha();
  }
}

async function loadCaptcha(): Promise<void> {
  try {
    const data = await fetchCaptcha();
    captcha.captchaEnabled = data.captchaEnabled;
    captcha.uuid = data.uuid || '';
    captcha.img = data.img || '';
    form.captchaUuid = captcha.uuid || '';
    form.captchaCode = '';
  } catch {
    captcha.captchaEnabled = true;
  }
}

function validatePasswordForm(): boolean {
  if (!ensureAgreement()) return false;
  if (!form.username) {
    showInfoToast('请输入账号');
    return false;
  }
  if (!form.password) {
    showInfoToast('请输入密码');
    return false;
  }
  if (captcha.captchaEnabled && !form.captchaCode) {
    showInfoToast('请输入验证码');
    return false;
  }
  return true;
}

async function loginWithWechat(): Promise<void> {
  if (wechatSubmitting.value || !ensureAgreement()) return;
  if (!isWechatMiniProgram()) {
    showInfoToast('当前仅支持微信小程序快捷登录');
    return;
  }

  wechatSubmitting.value = true;
  try {
    const code = await getWechatCode();
    const result = await loginByMiniProgramCode(code);
    if (result.token) {
      await finalizeLogin(result.token, result.newUser ? '已通过微信登录' : '微信登录成功');
      return;
    }
    throw new Error('微信登录结果缺少 token');
  } finally {
    wechatSubmitting.value = false;
  }
}

function isWechatMiniProgram(): boolean {
  let supported = false;
  // #ifdef MP-WEIXIN
  supported = true;
  // #endif
  return supported;
}

function getWechatCode(): Promise<string> {
  return new Promise((resolve, reject) => {
    uni.login({
      success: (res) => {
        if (res.code) {
          resolve(res.code);
          return;
        }
        reject(new Error('微信登录 code 获取失败'));
      },
      fail: (error) => reject(error)
    });
  });
}

async function submitPasswordLogin(): Promise<void> {
  if (submitting.value || !validatePasswordForm()) return;
  submitting.value = true;
  try {
    const token = await loginByPassword({
      username: form.username,
      password: form.password,
      captchaCode: form.captchaCode,
      captchaUuid: form.captchaUuid
    });
    await finalizeLogin(token, '登录成功');
  } catch {
    if (captcha.captchaEnabled) {
      await loadCaptcha();
    }
  } finally {
    submitting.value = false;
  }
}

async function submitBindLogin(): Promise<void> {
  if (submitting.value || !validatePasswordForm()) return;
  if (!isWechatMiniProgram()) {
    showInfoToast('当前仅支持微信小程序绑定');
    return;
  }
  submitting.value = true;
  try {
    const code = await getWechatCode();
    const bindTicket = await prepareMiniProgramBind(code);
    const token = await bindMiniProgramAccount({
      bindToken: bindTicket.bindToken,
      username: form.username,
      password: form.password,
      captchaCode: form.captchaCode,
      captchaUuid: form.captchaUuid
    });
    await finalizeLogin(token, '绑定成功');
  } catch {
    if (captcha.captchaEnabled) {
      await loadCaptcha();
    }
  } finally {
    submitting.value = false;
  }
}

async function finalizeLogin(token: LoginToken, successTitle: string): Promise<void> {
  userStore.loginWithToken({
    token: token.token,
    options: {
      tokenName: token.tokenName,
      tokenPrefix: token.tokenPrefix,
      expiresIn: token.expiresIn
    }
  });
  userStore.updateUserInfo(null);
  try {
    const currentUser = await fetchCurrentUserInfo();
    userStore.updateUserInfo(mapFrontUserInfo(currentUser));
  } catch {
    // 用户资料稍后由 onShow 补拉，不阻塞登录完成。
  }
  const actions = userStore.consumePendingActions();
  await Promise.all(actions.map((action) => replayPendingAction(action).catch(() => undefined)));
  reset401Guard();
  uni.$emit(AUTH_LOGIN_SUCCESS_EVENT, { actions } satisfies LoginSuccessEventDetail);
  const targetUrl = postLoginTargetUrl.value;
  postLoginTargetUrl.value = '';
  closeLoginSheet(true);
  showSuccessToast(successTitle);
  if (targetUrl) {
    uni.navigateTo({ url: targetUrl });
  }
}

function onLogout(): void {
  uni.showModal({
    title: '提示',
    content: '确认退出登录？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await logoutRemote();
        } catch {
          // 本地退出优先，远端会话失效不阻塞用户操作。
        }
        userStore.logout();
        showInfoToast('已退出登录');
      }
    }
  });
}

function openPolicyPage(type: LegalArticleType): void {
  uni.navigateTo({ url: `/pages-about/policy/policy?type=${type}` });
}
</script>

<style lang="scss" scoped>
.mine {
  --mine-primary: var(--sl-color-primary);
  --mine-primary-soft: var(--sl-color-primary-soft);
  --mine-text-main: var(--sl-text-main);
  --mine-text-sub: var(--sl-text-sub);
  --mine-glass-pure: var(--sl-bg-glass-pure);
  --mine-glass-tint: var(--sl-bg-glass-tint);
  --mine-border-glass: var(--sl-border-glass);
  --mine-shadow-glass: var(--sl-glass-shadow);

  min-height: 100vh;
  padding-bottom: calc(172rpx + env(safe-area-inset-bottom));
  background: var(--sl-page-bg, #f5f7fb);
  transition: background-color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1);

  &.sl-theme--dark {
    background: var(--sl-page-bg, #0f172a);
    .mine__profile,
    .mine__section,
    .mine__menu-item,
    .mine__asset-card {
      background: var(--sl-card-glass-bg);
      border-color: var(--sl-border-light);
      box-shadow: var(--sl-shadow-soft);
    }

    .mine__menu-icon,
    .mine__asset-icon {
      background: var(--sl-card-glass-bg);
      border-color: var(--sl-border-light);
      box-shadow: var(--sl-shadow-soft);
    }

    .mine__nickname,
    .mine__section-title,
    .mine__asset-title,
    .mine__menu-title {
      color: var(--sl-text-main);
      text-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.5);
    }

    .mine__asset-desc {
      color: rgba(149, 164, 185, 0.85);
    }

    .mine__menu-desc,
    .mine__menu-arrow {
      color: var(--sl-text-sub);
    }

    .mine__feature-badge {
      background: #ef4444;
    }

    .mine__hero-mask {
      background:
        linear-gradient(180deg, rgba(8, 13, 24, 0.08) 0%, rgba(8, 13, 24, 0.28) 52%, rgba(9, 9, 11, 0.92) 100%),
        rgba(8, 13, 24, 0.18);
    }
  }

  &__hero {
    position: relative;
    height: 560rpx;
    overflow: hidden;
    box-sizing: border-box;
  }

  &__hero-bg,
  &__banner-bg {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    display: block;
  }

  &__hero-mask {
    position: absolute;
    inset: 0;
    z-index: 1;
    background:
      linear-gradient(180deg, rgba(255, 255, 255, 0.04) 0%, rgba(17, 24, 39, 0.06) 52%, rgba(245, 247, 250, 0.35) 100%),
      rgba(17, 24, 39, 0.03);
    pointer-events: none;
  }

  &__hero-body {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 112rpx;
    z-index: 2;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12rpx;
  }

  &__hero-logo {
    width: 78rpx;
    height: 78rpx;
    padding: 10rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.72);
    border: 1rpx solid rgba(255, 255, 255, 0.65);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
      0 12rpx 28rpx rgba(17, 24, 39, 0.1);

    .sl-theme--dark & {
      background: rgba(18, 27, 46, 0.82);
      border-color: rgba(154, 176, 255, 0.2);
    }
  }

  &__hero-title {
    color: #fff;
    font-size: 56rpx;
    font-weight: 800;
    letter-spacing: 0.14em;
    text-align: center;
    text-shadow: 0 12rpx 36rpx rgba(17, 24, 39, 0.25);
  }

  &__hero-subtitle {
    color: rgba(255, 255, 255, 0.95);
    font-size: 24rpx;
    font-weight: 600;
    letter-spacing: 0.22em;
    text-align: center;
    text-transform: uppercase;
    text-shadow: 0 4rpx 16rpx rgba(17, 24, 39, 0.2);
  }

  &__content {
    position: relative;
    margin-top: 20rpx;
    padding: 0 30rpx;
    z-index: 2;
  }

  &__profile {
    position: relative;
    z-index: 3;
    margin-top: -72rpx;
    display: flex;
    align-items: center;
    min-height: 154rpx;
    padding: 28rpx 26rpx;
    border-radius: 28rpx;
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      var(--mine-glass-pure);
    border: 1rpx solid var(--mine-border-glass);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.92),
      0 24rpx 52rpx rgba(17, 24, 39, 0.14),
      0 8rpx 20rpx rgba(59, 89, 255, 0.06);
    transition: transform 0.2s cubic-bezier(0.25, 0.8, 0.25, 1), background-color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1), border-color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1), box-shadow 0.2s ease;
  }

  &__profile:active {
    transform: translateY(2rpx);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
      0 16rpx 36rpx rgba(17, 24, 39, 0.1);
  }

  &__avatar {
    width: 116rpx;
    height: 116rpx;
    border-radius: 50%;
    margin-right: 28rpx;
    overflow: hidden;
    border: 4rpx solid rgba(255, 255, 255, 0.96);
    box-shadow: 0 12rpx 26rpx rgba(17, 24, 39, 0.12);
  }

  &__profile-text {
    flex: 1;
    min-width: 0;
  }

  &__nickname {
    color: var(--mine-text-main);
    font-size: 38rpx;
    font-weight: 800;
    line-height: 1.35;
    letter-spacing: -0.01em;
  }

  &__hint {
    margin-top: 8rpx;
    color: var(--mine-text-sub);
    font-size: 25rpx;
  }

  &__quick-stats {
    display: flex;
    flex-wrap: wrap;
    gap: 14rpx;
    margin-top: 14rpx;
    color: var(--mine-primary);
    font-size: 22rpx;
  }

  &__profile-action {
    width: 132rpx;
    height: 70rpx;
    line-height: 70rpx;
    margin: 0;
    padding: 0;
    border-radius: 999rpx;
    background:
      linear-gradient(135deg, var(--mine-primary), var(--mine-primary-soft));
    color: #fff;
    font-size: 28rpx;
    font-weight: 700;
    box-shadow: 0 12rpx 26rpx rgba(59, 89, 255, 0.22);
  }

  &__profile-action::after {
    border: none;
  }

  &__profile-action:active {
    transform: scale(0.96);
  }

  &__banner {
    position: relative;
    height: 220rpx;
    margin-top: 22rpx;
    border-radius: 24rpx;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 0 42rpx;
    box-sizing: border-box;

    .sl-theme--dark & {
      filter: brightness(0.94) contrast(1.03);
    }
  }

  &__banner-mask {
    position: absolute;
    inset: 0;
    background:
      linear-gradient(135deg, rgba(59, 89, 255, 0.16) 0%, rgba(143, 112, 255, 0.06) 100%),
      linear-gradient(180deg, rgba(255, 255, 255, 0.08) 0%, rgba(17, 24, 39, 0.05) 100%);
  }

  &__banner-title,
  &__banner-desc {
    position: relative;
    z-index: 1;
    color: #fff;
    text-shadow:
      0 4rpx 12rpx rgba(17, 24, 39, 0.2),
      0 1rpx 2rpx rgba(17, 24, 39, 0.28);
  }

  &__banner-title {
    font-size: 34rpx;
    font-weight: 800;
  }

  &__banner-desc {
    max-width: 520rpx;
    margin-top: 10rpx;
    font-size: 22rpx;
    line-height: 1.55;
  }

  &__section {
    margin-top: 22rpx;
    padding: 28rpx 24rpx 24rpx;
    border-radius: 28rpx;
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      var(--mine-glass-pure);
    border: 1rpx solid var(--mine-border-glass);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.82),
      0 12rpx 34rpx rgba(17, 24, 39, 0.05);
  }

  &__section-head {
    margin-bottom: 20rpx;
  }

  &__section-title {
    color: var(--mine-text-main);
    font-size: 34rpx;
    font-weight: 800;
  }

  &__asset-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 14rpx;
  }

  &__asset-card {
    min-height: 200rpx;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 26rpx;
    border-radius: 30rpx;
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      var(--mine-glass-pure);
    border: 1rpx solid var(--mine-border-glass);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.88),
      0 16rpx 36rpx rgba(17, 24, 39, 0.05);
    transition: transform 0.2s cubic-bezier(0.25, 1, 0.5, 1), box-shadow 0.2s ease;
  }

  &__asset-card--hover {
    opacity: 0.9;
  }

  &__asset-card:active {
    transform: scale(0.97);
  }

  &__asset-icon {
    position: relative;
    width: 72rpx;
    height: 72rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 22rpx;
    background:
      linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg));
    border: 1rpx solid var(--sl-control-border);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 8rpx 20rpx rgba(17, 24, 39, 0.04);
    transition: transform 0.2s cubic-bezier(0.25, 1, 0.5, 1), box-shadow 0.2s ease;
  }

  &__feature-badge {
    position: absolute;
    top: -8rpx;
    right: -10rpx;
    min-width: 30rpx;
    height: 30rpx;
    padding: 0 8rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 999rpx;
    background: #ff5b75;
    color: #fff;
    font-size: 18rpx;
    font-weight: 700;
    box-sizing: border-box;
  }

  &__asset-info {
    margin-top: 18rpx;
  }

  &__asset-title {
    color: var(--mine-text-main);
    font-size: 28rpx;
    font-weight: 800;
    line-height: 1.35;
  }

  &__asset-desc {
    margin-top: 6rpx;
    color: var(--mine-text-sub);
    font-size: 22rpx;
    line-height: 1.4;
  }

  &__menu {
    display: flex;
    flex-direction: column;
    gap: 12rpx;
  }

  &__menu-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 18rpx;
    padding: 18rpx 18rpx;
    border-radius: 22rpx;
    background:
      linear-gradient(145deg, var(--sl-panel-highlight), var(--sl-panel-lowlight)),
      rgba(255, 255, 255, 0.44);
    border: 1rpx solid rgba(255, 255, 255, 0.7);
    transition: transform 0.2s cubic-bezier(0.25, 1, 0.5, 1), box-shadow 0.2s ease;
  }

  &__menu-item--hover {
    opacity: 0.92;
  }

  &__menu-item:active {
    transform: scale(0.985);
  }

  &__menu-main {
    flex: 1;
    min-width: 0;
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  &__menu-icon {
    width: 64rpx;
    height: 64rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 22rpx;
    background:
      linear-gradient(145deg, var(--sl-control-bg-strong), var(--sl-control-bg));
    border: 1rpx solid var(--sl-control-border);
    box-shadow:
      inset 0 1rpx 0 rgba(255, 255, 255, 0.9),
      0 8rpx 18rpx rgba(17, 24, 39, 0.03);
    flex-shrink: 0;
  }

  &__menu-copy {
    flex: 1;
    min-width: 0;
  }

  &__menu-title {
    color: var(--mine-text-main);
    font-size: 26rpx;
    font-weight: 700;
    line-height: 1.35;
  }

  &__menu-desc {
    margin-top: 6rpx;
    color: var(--mine-text-sub);
    font-size: 20rpx;
    line-height: 1.5;
  }

  &__menu-arrow {
    color: var(--sl-text-muted);
    flex-shrink: 0;
  }

  &__logout {
    width: 280rpx;
    margin: 22rpx auto 0;
    border-radius: 999rpx;
    background: rgba(255, 255, 255, 0.8);
    text-align: center;
    color: #e54866;
    font-size: 26rpx;
    padding: 20rpx 0;
    transition: background-color 0.24s ease;

    .sl-theme--dark & {
      background: rgba(18, 27, 46, 0.62);
      border: 1rpx solid var(--sl-border-glass);
    }
  }

  &__support {
    margin-top: 84rpx;
    text-align: center;
  }

  &__support-logo {
    width: 64rpx;
    height: 64rpx;
  }

  &__support-name {
    margin-top: 8rpx;
    color: var(--mine-text-main);
    font-size: 22rpx;
    font-weight: 800;
    letter-spacing: 0.08em;
  }

  &__support-text {
    margin-top: 12rpx;
    color: var(--sl-text-muted);
    font-size: 24rpx;
  }
}

.mine-login {
  position: fixed;
  inset: 0;
  z-index: 120;
  display: flex;
  align-items: flex-end;
  background: rgba(8, 13, 24, 0.46);
  /* #ifdef H5 || APP-PLUS */
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  /* #endif */

  &__sheet {
    position: relative;
    width: 100%;
    max-height: 88vh;
    padding: 18rpx 48rpx calc(42rpx + env(safe-area-inset-bottom));
    box-sizing: border-box;
    border-radius: 42rpx 42rpx 0 0;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.98));
    border-top: 1rpx solid var(--sl-border-glass);
    overflow-y: auto;
    transition: background-color 0.24s cubic-bezier(0.25, 0.8, 0.25, 1);

    .sl-theme--dark & {
      background: linear-gradient(180deg, rgba(18, 27, 46, 0.98), rgba(8, 13, 24, 0.98));
    }
  }

  &__handle {
    width: 78rpx;
    height: 8rpx;
    margin: 0 auto 34rpx;
    border-radius: 999rpx;
    background: rgba(17, 24, 39, 0.12);
  }

  &__logo {
    width: 88rpx;
    height: 88rpx;
    display: block;
    margin-bottom: 28rpx;
  }

  &__title {
    color: var(--sl-text-main);
    font-size: 42rpx;
    font-weight: 800;
    line-height: 1.25;
  }

  &__desc {
    margin-top: 18rpx;
    color: var(--sl-text-sub);
    font-size: 29rpx;
    line-height: 1.55;
  }

  &__primary,
  &__ghost,
  &__submit {
    height: 92rpx;
    line-height: 92rpx;
    border-radius: 999rpx;
    font-size: 31rpx;
    font-weight: 700;
  }

  &__primary::after,
  &__ghost::after,
  &__submit::after {
    border: none;
  }

  &__primary:active,
  &__ghost:active,
  &__submit:active {
    transform: scale(0.98);
  }

  &__primary {
    margin-top: 74rpx;
    background: linear-gradient(135deg, var(--sl-color-primary), var(--sl-color-primary-soft));
    color: #fff;
    box-shadow: 0 16rpx 36rpx rgba(59, 89, 255, 0.24);
  }

  &__ghost {
    margin-top: 22rpx;
    background: var(--sl-control-bg);
    color: var(--sl-color-primary);
    border: 2rpx solid rgba(59, 89, 255, 0.34);
  }

  &__switch {
    width: 260rpx;
    margin: 24rpx auto 0;
  }

  &__form {
    margin-top: 32rpx;
  }

  &__field {
    @include sl-input;
    margin-bottom: 18rpx;
    border-radius: 22rpx;
    padding: 18rpx 22rpx;
  }

  &__field--captcha {
    display: flex;
    align-items: center;
    gap: 16rpx;
  }

  &__captcha-input {
    flex: 1;
    min-width: 0;
  }

  &__label {
    display: block;
    margin-bottom: 8rpx;
    color: $color-text-tertiary;
    font-size: 22rpx;
  }

  &__input {
    width: 100%;
    height: 46rpx;
    color: var(--sl-text-main);
    font-size: 28rpx;
  }

  &__placeholder {
      color: $color-text-secondary;
  }

  &__captcha {
    flex: 0 0 184rpx;
    width: 184rpx;
    height: 66rpx;
    border-radius: 12rpx;
    background: var(--sl-control-bg);
    overflow: hidden;
  }

  &__submit {
    margin-top: 22rpx;
    background: linear-gradient(135deg, var(--sl-color-primary), var(--sl-color-primary-soft));
    color: #fff;
    box-shadow: 0 14rpx 32rpx rgba(59, 89, 255, 0.2);
  }

  &__bind-link {
    width: 260rpx;
    margin: 20rpx auto 0;
  }

  &__bind-link--disabled {
    opacity: 0.72;
  }

  &__agreement {
    display: flex;
    align-items: flex-start;
    gap: 18rpx;
    margin-top: 30rpx;
  }

  &__check {
    width: 36rpx;
    height: 36rpx;
    flex: 0 0 36rpx;
    border-radius: 50%;
    border: 3rpx solid rgba(75, 85, 99, 0.28);
    box-sizing: border-box;
  }

  &__check--active {
    border-color: var(--sl-color-primary);
    background: radial-gradient(circle, var(--sl-color-primary) 0 42%, rgba(59, 89, 255, 0) 48%);
  }

  &__agreement-text {
    color: var(--sl-text-sub);
    font-size: 24rpx;
    line-height: 1.65;
  }

  &__agreement-link {
    color: var(--sl-color-primary);
    font-weight: 700;
  }
}
</style>
