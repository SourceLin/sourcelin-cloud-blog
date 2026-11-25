<template>
  <div :class="['navbar-actions', navbarActionsClass]">
    <!-- 桌面端工具项 -->
    <template v-if="isDesktop">
      <!-- 布局大小 -->
      <div class="navbar-actions__item">
        <SizeSelect />
      </div>

      <!-- 主题切换 -->
      <div class="navbar-actions__item navbar-actions__item--theme">
        <ThemeSwitch @toggle="handleThemeToggle" />
      </div>

      <!-- 全屏 -->
      <div class="navbar-actions__item">
        <Fullscreen />
      </div>

      <!-- 通知 -->
      <div class="navbar-actions__item">
        <NoticeDropdown />
      </div>
    </template>

    <!-- 用户菜单 -->
    <div class="navbar-actions__item">
      <el-dropdown trigger="click">
        <div class="user-profile">
          <div style="width: 28px; height: 28px; overflow: hidden; border-radius: 50%">
            <img
              :src="userStore.userInfo.avatar"
              class="user-profile__avatar"
              style="width: 100%; height: 100%; object-fit: cover; object-position: center"
            />
          </div>
          <span class="user-profile__name">
            {{ userStore.userInfo.nickName || userStore.userInfo.userName }}
          </span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="handleProfileClick">个人中心</el-dropdown-item>
            <el-dropdown-item divided @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <!-- 系统设置 -->
    <div v-if="settingStore.showSettings" class="navbar-actions__item" @click="handleSettingsClick">
      <div class="i-svg:setting" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from "vue-router";
import ThemeSwitch from "@/components/ThemeSwitch/index.vue";
import { useThemeToggle } from "@/composables";
import { DeviceEnum, ThemeMode } from "@/enums/settings";
import { useAppStore, useSettingsStore, useUserStore } from "@/store";

// 导入子组件
import Fullscreen from "@/components/Fullscreen/index.vue";
import SizeSelect from "@/components/SizeSelect/index.vue";
import NoticeDropdown from "@/components/NoticeDropdown/index.vue";

const appStore = useAppStore();
const settingStore = useSettingsStore();
const userStore = useUserStore();
const { handleThemeToggle } = useThemeToggle();

const route = useRoute();
const router = useRouter();

// 是否为桌面设备
const isDesktop = computed(() => appStore.device === DeviceEnum.DESKTOP);

/**
 * 打开个人中心页面
 */
function handleProfileClick() {
  router.push({ name: "Profile" });
}

const navbarActionsClass = computed(() =>
  settingStore.resolvedTheme === ThemeMode.DARK
    ? "navbar-actions--white-text"
    : "navbar-actions--dark-text"
);

/**
 * 退出登录
 */
function logout() {
  ElMessageBox.confirm("确定注销并退出系统吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
    lockScroll: false,
  }).then(() => {
    userStore.logout().then(() => {
      router.push(`/login?redirect=${route.fullPath}`);
    });
  });
}

/**
 * 打开系统设置页面
 */
function handleSettingsClick() {
  settingStore.settingsVisible = true;
}
</script>

<style lang="scss" scoped>
.navbar-actions {
  display: flex;
  align-items: center;
  min-height: 44px;

  &__item {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 44px; /* 增加最小点击区域到44px，符合人机交互标准 */
    height: 44px;
    padding: 0 8px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;

    // 确保子元素居中
    > * {
      display: flex;
      align-items: center;
      justify-content: center;
    }

    // 确保 Element Plus 组件可以正常工作
    :deep(.el-dropdown),
    :deep(.el-tooltip) {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 100%;
      height: 44px;
    }

    // 图标样式
    :deep([class^="i-svg:"]) {
      font-size: 18px;
      line-height: 1;
      color: var(--el-text-color-regular);
      transition: color 0.3s;
    }

    &:hover {
      background: var(--el-fill-color-light);

      :deep([class^="i-svg:"]) {
        color: var(--el-color-primary);
      }
    }
  }

  &__item--theme {
    :deep(.theme-switch) {
      color: inherit;
    }
  }

  .user-profile {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 44px;
    padding: 0 8px;

    &__avatar {
      flex-shrink: 0;
      width: 28px;
      height: 28px;
      border-radius: 50%;
    }

    &__name {
      margin-left: 8px;
      color: var(--el-text-color-regular);
      white-space: nowrap;
      transition: color 0.3s;
    }
  }
}

// 暗色主题的工具栏文字
.navbar-actions--white-text {
  .navbar-actions__item {
    :deep([class^="i-svg:"]) {
      color: color-mix(in srgb, var(--el-color-white) 85%, transparent);
    }

    &:hover {
      background: color-mix(in srgb, var(--el-color-white) 10%, transparent);

      :deep([class^="i-svg:"]) {
        color: var(--el-color-white);
      }
    }
  }

  .navbar-actions__item--theme {
    color: color-mix(in srgb, var(--el-color-white) 85%, transparent);

    &:hover {
      color: var(--el-color-white);
    }
  }

  .user-profile__name {
    color: color-mix(in srgb, var(--el-color-white) 85%, transparent);
  }
}

// 亮色主题的工具栏文字
.navbar-actions--dark-text {
  .navbar-actions__item {
    :deep([class^="i-svg:"]) {
      color: var(--el-text-color-regular) !important;
    }

    &:hover {
      background: var(--toolbar-hover-bg);

      :deep([class^="i-svg:"]) {
        color: var(--el-color-primary) !important;
      }
    }
  }

  .navbar-actions__item--theme {
    color: var(--el-text-color-regular);

    &:hover {
      color: var(--el-color-primary);
    }
  }

  .user-profile__name {
    color: var(--el-text-color-regular) !important;
  }
}

// 确保下拉菜单中的图标不受影响
::v-deep(.el-dropdown-menu) {
  [class^="i-svg:"] {
    color: var(--el-text-color-regular) !important;

    &:hover {
      color: var(--el-color-primary) !important;
    }
  }
}
</style>
