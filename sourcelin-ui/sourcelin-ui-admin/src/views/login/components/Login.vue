<template>
  <div class="auth-panel-form">
    <div class="auth-panel-form__head">
      <p class="auth-panel-form__subtitle" text-center>欢迎回来</p>
    </div>
    <el-form
      ref="loginFormRef"
      :model="loginFormData"
      :rules="loginRules"
      size="large"
      :validate-on-rule-change="false"
    >
      <!-- 用户名 -->
      <el-form-item prop="username">
        <el-input
          v-model.trim="loginFormData.username"
          :placeholder="t('login.username')"
          autocomplete="username"
          spellcheck="false"
        >
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <!-- 密码 -->
      <el-tooltip :visible="isCapsLock" :content="t('login.capsLock')" placement="right">
        <el-form-item prop="password">
          <el-input
            v-model.trim="loginFormData.password"
            :placeholder="t('login.password')"
            type="password"
            autocomplete="current-password"
            show-password
            @keyup="checkCapsLock"
            @keyup.enter="handleLoginSubmit"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-tooltip>

      <!-- 验证码（仅在后端开启图形验证码时展示） -->
      <el-form-item v-if="captchaEnabled" prop="captchaCode">
        <div flex items-center gap-10px>
          <el-input
            v-model.trim="loginFormData.captchaCode"
            :placeholder="t('login.captchaCode')"
            clearable
            autocomplete="one-time-code"
            class="flex-1"
            @keyup.enter="handleLoginSubmit"
          >
            <template #prefix>
              <div class="i-svg:captcha" />
            </template>
          </el-input>
          <div cursor-pointer h-44px w-140px flex-center @click="getCaptcha">
            <el-icon v-if="codeLoading" class="is-loading" size="20"><Loading /></el-icon>
            <img
              v-else-if="captchaImageSrc"
              border-rd-4px
              w-full
              h-full
              block
              object-cover
              shadow="[0_0_0_1px_var(--el-border-color)_inset]"
              :src="captchaImageSrc"
              alt=""
              title="点击刷新验证码"
              @error="getCaptcha"
            />
            <el-text v-else type="info" size="small">点击获取验证码</el-text>
          </div>
        </div>
      </el-form-item>

      <div class="auth-panel-form__assist">
        <el-checkbox v-model="loginFormData.rememberMe">{{ t("login.rememberMe") }}</el-checkbox>
        <span class="auth-panel-form__entry">管理员入口</span>
      </div>

      <!-- 登录按钮 -->
      <el-form-item class="auth-panel-form__submit">
        <el-button
          :loading="loading"
          :disabled="!captchaConfigLoaded"
          type="primary"
          class="w-full"
          @click="handleLoginSubmit"
        >
          {{ t("login.login") }}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
<script setup lang="ts">
import type { FormInstance } from "element-plus";
import AuthAPI from "@/api/auth";
import type { LoginRequest } from "@/types/api";
import router from "@/router";
import { useUserStore } from "@/store";
import { AuthStorage } from "@/utils/auth";

const { t } = useI18n();
const userStore = useUserStore();
const route = useRoute();

onMounted(() => getCaptcha());

const loginFormRef = ref<FormInstance>();
const loading = ref(false);
// 是否大写锁定
const isCapsLock = ref(false);
/** 是否已从接口获知验证码开关（避免未拉取配置时误判为关闭） */
const captchaConfigLoaded = ref(false);
/** 后端是否开启管理员图形验证码（与 ShearCaptchaService#createCaptcha 一致） */
const captchaEnabled = ref(false);
/** 验证码图片 data URL（纯 base64 会补全前缀以便 img 展示） */
const captchaImageSrc = ref("");
// 记住我
const rememberMe = AuthStorage.getRememberMe();
const loginFormData = ref<LoginRequest>({
  username: "admin",
  password: "admin123",
  loginType: "admin",
  captchaUuid: "",
  captchaCode: "",
  rememberMe,
});

const loginRules = computed(() => {
  return {
    username: [
      {
        required: true,
        trigger: "blur",
        message: t("login.message.username.required"),
      },
    ],
    password: [
      {
        required: true,
        trigger: "blur",
        message: t("login.message.password.required"),
      },
      {
        min: 6,
        message: t("login.message.password.min"),
        trigger: "blur",
      },
    ],
    ...(captchaEnabled.value
      ? {
          captchaCode: [
            {
              required: true,
              trigger: "blur",
              message: t("login.message.captchaCode.required"),
            },
          ],
        }
      : {}),
  };
});

// 获取验证码
const codeLoading = ref(false);

function toCaptchaDataUrl(raw: string | undefined): string {
  if (!raw?.trim()) return "";
  const s = raw.trim();
  if (s.startsWith("data:")) return s;
  return `data:image/png;base64,${s}`;
}

function getCaptcha() {
  codeLoading.value = true;
  AuthAPI.getCaptcha()
    .then((data) => {
      const enabled = data.captchaEnabled === true;
      captchaEnabled.value = enabled;
      if (enabled) {
        loginFormData.value.captchaUuid = data.uuid ?? "";
        captchaImageSrc.value = toCaptchaDataUrl(data.img);
      } else {
        loginFormData.value.captchaUuid = "";
        loginFormData.value.captchaCode = "";
        captchaImageSrc.value = "";
      }
    })
    .catch(() => {
      captchaEnabled.value = false;
      loginFormData.value.captchaUuid = "";
      loginFormData.value.captchaCode = "";
      captchaImageSrc.value = "";
    })
    .finally(() => {
      codeLoading.value = false;
      captchaConfigLoaded.value = true;
    });
}

/**
 * 登录提交
 */
async function handleLoginSubmit() {
  // 1. 表单验证
  const valid = await loginFormRef.value?.validate().then(
    () => true,
    () => false
  );
  if (!valid) return;

  loading.value = true;
  try {
    // 2. 执行登录
    await userStore.login(loginFormData.value).then(
      async () => {
        // 登录成功，跳转到目标页面
        const redirectPath = (route.query.redirect as string) || "/";
        await router.push(decodeURIComponent(redirectPath));
      },
      (error) => {
        // 登录失败，刷新验证码
        getCaptcha();
        throw error;
      }
    );
  } finally {
    loading.value = false;
  }
}

// 检查输入大小写
function checkCapsLock(event: KeyboardEvent) {
  // 防止浏览器密码自动填充时报错
  if (event instanceof KeyboardEvent) {
    isCapsLock.value = event.getModifierState("CapsLock");
  }
}
</script>

<style lang="scss" scoped>
.auth-panel-form {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
}

.auth-panel-form__head {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
  margin-bottom: 0.45rem;
}

.auth-panel-form__title {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 650;
  letter-spacing: 0.02em;
}

.auth-panel-form__subtitle {
  margin: 0;
  font-size: 0.875rem;
  color: var(--el-text-color-regular);
}

.auth-panel-form__assist {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  margin-top: 0.1rem;
  margin-bottom: 0.35rem;
}

.auth-panel-form__entry {
  font-size: 0.75rem;
  color: var(--el-text-color-placeholder);
}

.auth-panel-form__submit {
  margin-top: 0.1rem;
}

:deep(.el-form-item) {
  margin-bottom: 1.05rem;
}

:deep(.el-button--primary) {
  height: 46px;
  font-weight: 600;
  border-radius: 12px;
}
</style>
