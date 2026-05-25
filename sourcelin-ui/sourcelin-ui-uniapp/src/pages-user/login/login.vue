<template>
  <view class="login s-container">
    <view class="login__halo" />
    <view class="login__panel s-card">
      <view class="login__brand">
        <image class="login__logo" src="/static/logo/logo.png" mode="aspectFit" />
        <view class="login__eyebrow">Sourcelin Mini</view>
      </view>
      <view class="login__title">微信快捷登录</view>
      <view class="login__desc">优先推荐微信快捷登录，减少输入成本并保留小程序原生体验。</view>

      <button class="login__wechat" :disabled="wechatSubmitting" @tap="loginWithWechat">
        <s-inline-loading v-if="wechatSubmitting" text="微信登录中" light />
        <text v-else>一键使用微信登录</text>
      </button>

      <view class="login__switch" @tap="togglePasswordLogin">
        {{ showPasswordLogin ? '收起账号密码登录' : '绑定已有账号 / 备用登录' }}
      </view>

      <view v-if="showPasswordForm" class="login__form">
        <view class="login__field">
          <text class="login__label">账号密码备用登录</text>
          <input
            v-model.trim="form.username"
            class="login__input"
            placeholder="请输入用户名 / 邮箱"
            placeholder-class="login__placeholder"
            confirm-type="next"
          >
        </view>

        <view class="login__field">
          <text class="login__label">密码</text>
          <input
            v-model.trim="form.password"
            class="login__input"
            password
            placeholder="请输入密码"
            placeholder-class="login__placeholder"
            confirm-type="done"
            @confirm="submitPasswordLogin"
          >
        </view>

        <view v-if="captcha.captchaEnabled" class="login__field login__field--captcha">
          <view class="login__captcha-input">
            <text class="login__label">验证码</text>
            <input
              v-model.trim="form.captchaCode"
              class="login__input"
              placeholder="输入图片验证码"
              placeholder-class="login__placeholder"
              confirm-type="done"
              @confirm="submitPasswordLogin"
            >
          </view>
          <image
            v-if="captchaImage"
            class="login__captcha"
            :src="captchaImage"
            mode="scaleToFill"
            @tap="loadCaptcha"
          />
        </view>
      </view>

      <button
        v-if="showPasswordForm"
        class="login__button"
        :disabled="submitting"
        @tap="submitPasswordLogin"
      >
        <s-inline-loading v-if="submitting" text="登录中" light />
        <text v-else>账号密码登录</text>
      </button>

      <view v-if="showPasswordForm" class="login__bind-action" :class="{ 'login__bind-action--disabled': submitting }" @tap="submitBindLogin">
        <s-inline-loading v-if="submitting" text="处理中" />
        <text v-else>登录并绑定微信</text>
      </view>

      <view class="login__tip">
        微信登录优先，账号密码保留为绑定已有账号或备用登录。
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import {
  bindMiniProgramAccount,
  fetchCaptcha,
  loginByMiniProgramCode,
  loginByPassword,
  prepareMiniProgramBind
} from '@/shared/api/auth.api';
import type { CaptchaInfo, LoginToken } from '@/modules/auth/types/auth';
import { fetchCurrentUserInfo } from '@/shared/api/user.api';
import { mapFrontUserInfo } from '@/shared/utils/user-mapper';
import { replayPendingAction } from '@/shared/utils/pending-actions';
import { useUserStore } from '@/stores/user';
import { AUTH_LOGIN_SUCCESS_EVENT, type LoginSuccessEventDetail } from '@/utils/auth';
import { showInfoToast, showSuccessToast } from '@/utils/feedback';

const userStore = useUserStore();
const submitting = ref(false);
const wechatSubmitting = ref(false);
const showPasswordLogin = ref(false);

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

const captchaImage = computed(() => {
  if (!captcha.img) return '';
  return captcha.img.startsWith('data:') ? captcha.img : `data:image/png;base64,${captcha.img}`;
});

const showPasswordForm = computed(() => showPasswordLogin.value);

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

function validateForm(): boolean {
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

async function submitPasswordLogin(): Promise<void> {
  if (submitting.value || !validateForm()) return;
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
  if (submitting.value || !validateForm()) return;
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

async function togglePasswordLogin(): Promise<void> {
  showPasswordLogin.value = !showPasswordLogin.value;
  if (showPasswordLogin.value && captcha.captchaEnabled && !captcha.img) {
    await loadCaptcha();
  }
}

async function loginWithWechat(): Promise<void> {
  if (wechatSubmitting.value) return;
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

async function finalizeLogin(token: LoginToken, successTitle: string): Promise<void> {
  let tokenSaved = false;
  try {
    userStore.loginWithToken({
      token: token.token,
      options: {
        tokenName: token.tokenName,
        tokenPrefix: token.tokenPrefix,
        expiresIn: token.expiresIn
      }
    });
    tokenSaved = true;
    userStore.updateUserInfo(null);
    try {
      const currentUser = await fetchCurrentUserInfo();
      userStore.updateUserInfo(mapFrontUserInfo(currentUser));
    } catch {
      // 用户资料接口失败不阻塞登录主流程，后续页面 onShow 再补拉。
    }
    const actions = await replayPendingActions();
    uni.$emit(AUTH_LOGIN_SUCCESS_EVENT, { actions } satisfies LoginSuccessEventDetail);
    showSuccessToast(successTitle);
    backToPreviousPage();
  } catch (error) {
    if (tokenSaved) {
      userStore.logout();
    }
    throw error;
  }
}

async function replayPendingActions() {
  const actions = userStore.consumePendingActions();
  await Promise.all(actions.map((action) => replayPendingAction(action).catch(() => undefined)));
  return actions;
}

function backToPreviousPage(): void {
  const pages = getCurrentPages();
  if (pages.length > 1) {
    uni.navigateBack();
    return;
  }
  uni.switchTab({ url: '/pages/mine/mine' });
}
</script>

<style lang="scss" scoped>
.login {
  position: relative;
  min-height: 100vh;
  padding-top: 72rpx;
  overflow: hidden;
  box-sizing: border-box;

  &__halo {
    position: absolute;
    top: 40rpx;
    right: -180rpx;
    width: 460rpx;
    height: 460rpx;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(31, 111, 235, 0.18), rgba(255, 255, 255, 0));
  }

  &__panel {
    position: relative;
    z-index: 1;
    padding: 40rpx 32rpx 34rpx;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, 0.62), rgba(255, 255, 255, 0.3)),
      rgba(255, 255, 255, 0.28);
  }

  &__brand {
    display: inline-flex;
    align-items: center;
    gap: 12rpx;
    min-height: 58rpx;
    margin-bottom: $space-sm;
    padding: 8rpx 18rpx 8rpx 10rpx;
    border-radius: 999rpx;
    background: rgba(255, 255, 255, 0.44);
    border: 1rpx solid rgba(255, 255, 255, 0.68);
  }

  &__logo {
    width: 42rpx;
    height: 42rpx;
    border-radius: 50%;
  }

  &__eyebrow {
    color: $color-primary;
    font-size: 22rpx;
    font-weight: 600;
  }

  &__title {
    font-size: 42rpx;
    font-weight: 700;
    margin-bottom: $space-xs;
    line-height: 1.2;
  }

  &__desc,
  &__tip {
    color: $color-text-secondary;
    font-size: 24rpx;
    line-height: 1.6;
  }

  &__wechat {
    height: 88rpx;
    line-height: 88rpx;
    margin-top: 30rpx;
    border-radius: 999rpx;
    background: linear-gradient(135deg, #0f9f7f, #18c79c);
    color: #fff;
    font-size: 30rpx;
    font-weight: 600;
    box-shadow: 0 18rpx 42rpx rgba(15, 159, 127, 0.22);
  }

  &__switch {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 60rpx;
    margin: 18rpx auto 0;
    border-radius: 999rpx;
    color: $color-primary;
    font-size: 24rpx;
    font-weight: 600;
  }

  &__form {
    margin-top: 24rpx;
  }

  &__field {
    margin-bottom: 18rpx;
    padding: 18rpx 22rpx;
    border-radius: 22rpx;
    background: rgba(255, 255, 255, 0.62);
    border: 1rpx solid rgba(255, 255, 255, 0.72);
    box-sizing: border-box;
  }

  &__field--captcha {
    display: flex;
    align-items: center;
    gap: $space-sm;
    padding-right: 14rpx;
  }

  &__captcha-input {
    flex: 1;
    min-width: 0;
  }

  &__label {
    display: block;
    color: $color-text-tertiary;
    font-size: 22rpx;
    margin-bottom: 10rpx;
  }

  &__input {
    width: 100%;
    height: 44rpx;
    color: $color-text;
    font-size: 28rpx;
  }

  &__placeholder {
    color: $color-text-tertiary;
  }

  &__captcha {
    flex: 0 0 184rpx;
    width: 184rpx;
    height: 66rpx;
    border-radius: $radius-sm;
    background: $color-bg;
    overflow: hidden;
  }

  &__button {
    height: 82rpx;
    line-height: 82rpx;
    margin-top: 22rpx;
    border-radius: 999rpx;
    background: linear-gradient(135deg, $color-primary, #6f8fff);
    color: #fff;
    font-size: 30rpx;
    font-weight: 600;
    box-shadow: 0 18rpx 42rpx rgba(31, 111, 235, 0.24);
  }

  &__button[disabled] {
    opacity: 0.72;
  }

  &__bind-action {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 18rpx;
    color: $color-primary;
    font-size: 24rpx;
    font-weight: 600;
    text-align: center;
  }

  &__bind-action--disabled {
    opacity: 0.72;
  }

  &__tip {
    margin-top: 18rpx;
    text-align: center;
    font-size: 22rpx;
  }
}
</style>
