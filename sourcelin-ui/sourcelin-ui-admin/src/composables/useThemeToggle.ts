import { ThemeMode } from "@/enums";
import { useSettingsStore } from "@/store";

export type ThemeTogglePayload = {
  nextTheme: ThemeMode.LIGHT | ThemeMode.DARK;
};

export function useThemeToggle() {
  const settingsStore = useSettingsStore();

  const handleThemeToggle = ({ nextTheme }: ThemeTogglePayload) => {
    settingsStore.theme = nextTheme;
  };

  return {
    handleThemeToggle,
  };
}
