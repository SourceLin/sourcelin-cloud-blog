<template>
  <el-tooltip :content="tooltipText" placement="bottom">
    <button ref="switchRef" class="theme-switch" type="button" @click="toggleTheme">
      <el-icon :size="18">
        <component :is="isDark ? Sunny : Moon" />
      </el-icon>
    </button>
  </el-tooltip>
</template>

<script setup lang="ts">
import { Moon, Sunny } from "@element-plus/icons-vue";
import { ThemeMode } from "@/enums";
import { useSettingsStore } from "@/store";

const emit = defineEmits<{
  toggle: [{ nextTheme: ThemeMode.LIGHT | ThemeMode.DARK }];
}>();

const { t } = useI18n();
const settingsStore = useSettingsStore();
const switchRef = ref<HTMLButtonElement>();

const isDark = computed(() => settingsStore.resolvedTheme === ThemeMode.DARK);

const tooltipText = computed(() => (isDark.value ? t("login.light") : t("login.dark")));

function toggleTheme() {
  const nextTheme = isDark.value ? ThemeMode.LIGHT : ThemeMode.DARK;
  emit("toggle", { nextTheme });
}
</script>

<style lang="scss" scoped>
.theme-switch {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  color: inherit;
  cursor: pointer;
  background: transparent;
  border: 0;
  border-radius: 999px;
  transition:
    background-color 0.2s ease,
    transform 0.2s ease;

  &:hover {
    background-color: var(--el-fill-color);
    transform: scale(1.02);
  }
}
</style>
