<template>
  <div class="app-container admin-ui-page">
    <el-card shadow="hover" class="ui-config-section">
      <template #header>
        <div class="ui-config-section__header">
          <div>
            <div class="ui-config-section__title">管理后台 UI 默认配置</div>
            <div class="ui-config-section__desc">
              管理系统默认主题、布局、侧边栏风格与用户可见策略。本地浏览器偏好不会写回这里。
            </div>
          </div>
          <div class="ui-config-section__actions">
            <el-button :icon="Refresh" @click="loadAdminUiConfig">重新加载</el-button>
            <el-button
              v-hasPerm="['system:admin-ui:edit']"
              type="primary"
              :loading="uiConfigSaving"
              @click="handleSaveAdminUiConfig"
            >
              保存默认配置
            </el-button>
          </div>
        </div>
      </template>

      <el-form
        ref="adminUiConfigFormRef"
        v-loading="uiConfigLoading"
        :model="adminUiConfigForm"
        label-width="140px"
        class="ui-config-form"
      >
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="主题偏好">
              <el-select v-model="adminUiConfigForm.defaults.themePreference">
                <el-option label="跟随系统" value="system" />
                <el-option label="浅色" value="light" />
                <el-option label="深色" value="dark" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="默认布局">
              <el-select v-model="adminUiConfigForm.defaults.layout">
                <el-option label="左侧布局" value="left" />
                <el-option label="顶部布局" value="top" />
                <el-option label="混合布局" value="mix" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="侧边栏风格">
              <el-select v-model="adminUiConfigForm.defaults.sidebarAppearance">
                <el-option label="经典蓝" value="classic" />
                <el-option label="极简白" value="minimal" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="默认主题色">
              <el-color-picker
                v-model="adminUiConfigForm.defaults.themeColor"
                :predefine="adminUiConfigForm.presets"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="页面切换动画">
              <el-select v-model="adminUiConfigForm.defaults.pageSwitchingAnimation">
                <el-option
                  v-for="item in animationOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="组件尺寸">
              <el-select v-model="adminUiConfigForm.defaults.componentSize">
                <el-option label="默认" value="default" />
                <el-option label="大" value="large" />
                <el-option label="小" value="small" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="显示页签栏">
              <el-switch v-model="adminUiConfigForm.defaults.showTagsView" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="显示 Logo">
              <el-switch v-model="adminUiConfigForm.defaults.showAppLogo" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>用户可配置策略</el-divider>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="允许自定义主题色">
              <el-switch v-model="adminUiConfigForm.policy.allowCustomThemeColor" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="允许切换布局">
              <el-switch v-model="adminUiConfigForm.policy.allowUserLayoutSwitch" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="允许切换侧边栏风格">
              <el-switch v-model="adminUiConfigForm.policy.allowUserSidebarAppearanceSwitch" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider>主题色预设</el-divider>

        <el-form-item label="预设色列表">
          <el-select
            v-model="adminUiConfigForm.presets"
            multiple
            filterable
            allow-create
            default-first-option
            placeholder="输入十六进制颜色并回车，例如 #4B55FA"
            class="ui-config-form__preset-select"
          >
            <el-option
              v-for="preset in adminUiConfigForm.presets"
              :key="preset"
              :label="preset"
              :value="preset"
            >
              <div class="preset-option">
                <span class="preset-option__swatch" :style="{ backgroundColor: preset }"></span>
                <span>{{ preset }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: "AdminUiConfig",
  inheritAttrs: false,
});

import { Refresh } from "@element-plus/icons-vue";
import { PageSwitchingAnimationOptions } from "@/enums";
import AdminUiAPI from "@/api/system/admin-ui";
import type { AdminUiConfigAggregate } from "@/types/api";
import { ElMessage, type FormInstance } from "element-plus";
import { adminUiConfigFallback } from "@/settings";

function createAdminUiConfigForm(): AdminUiConfigAggregate {
  return {
    defaults: { ...adminUiConfigFallback.defaults },
    policy: { ...adminUiConfigFallback.policy },
    presets: [...adminUiConfigFallback.presets],
  };
}

const animationOptions = Object.values(PageSwitchingAnimationOptions).map((item) => ({
  label: item.label,
  value: item.value,
}));

const adminUiConfigFormRef = ref<FormInstance>();
const uiConfigLoading = ref(false);
const uiConfigSaving = ref(false);
const adminUiConfigForm = reactive<AdminUiConfigAggregate>(createAdminUiConfigForm());

function normalizeAdminUiConfig(data?: Partial<AdminUiConfigAggregate>) {
  const fallback = createAdminUiConfigForm();
  return {
    defaults: {
      ...fallback.defaults,
      ...(data?.defaults || {}),
    },
    policy: {
      ...fallback.policy,
      ...(data?.policy || {}),
    },
    presets:
      data?.presets && data.presets.length > 0 ? [...new Set(data.presets)] : fallback.presets,
  };
}

async function loadAdminUiConfig() {
  uiConfigLoading.value = true;
  try {
    const data = await AdminUiAPI.getDefaults();
    Object.assign(adminUiConfigForm, normalizeAdminUiConfig(data));
  } catch (error) {
    Object.assign(adminUiConfigForm, createAdminUiConfigForm());
    ElMessage.error("加载管理后台 UI 默认配置失败");
    console.error(error);
  } finally {
    uiConfigLoading.value = false;
  }
}

async function handleSaveAdminUiConfig() {
  uiConfigSaving.value = true;
  try {
    const payload = normalizeAdminUiConfig(adminUiConfigForm);
    payload.presets = payload.presets.filter((item) => /^#([0-9a-fA-F]{6})$/.test(item));
    if (payload.presets.length === 0) {
      ElMessage.error("请至少保留一个合法的十六进制主题色预设");
      return;
    }
    await AdminUiAPI.updateDefaults(payload);
    Object.assign(adminUiConfigForm, payload);
    ElMessage.success("管理后台 UI 默认配置已更新");
  } catch (error) {
    ElMessage.error("保存管理后台 UI 默认配置失败");
    console.error(error);
  } finally {
    uiConfigSaving.value = false;
  }
}

onMounted(() => {
  loadAdminUiConfig();
});
</script>

<style scoped lang="scss">
.admin-ui-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ui-config-section {
  &__header {
    display: flex;
    gap: 16px;
    align-items: flex-start;
    justify-content: space-between;
  }

  &__title {
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
  }

  &__desc {
    margin-top: 6px;
    font-size: 13px;
    color: var(--el-text-color-secondary);
  }

  &__actions {
    display: flex;
    gap: 12px;
    align-items: center;
  }
}

.ui-config-form {
  &__preset-select {
    width: 100%;
  }
}

.preset-option {
  display: inline-flex;
  gap: 8px;
  align-items: center;

  &__swatch {
    width: 14px;
    height: 14px;
    border: 1px solid var(--el-border-color);
    border-radius: 999px;
  }
}

@media (width <= 992px) {
  .ui-config-section {
    &__header {
      flex-direction: column;
      align-items: stretch;
    }

    &__actions {
      justify-content: flex-end;
    }
  }
}
</style>
