import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export type ThemeName = 'light' | 'dark'
export type ThemeDirection = 'to-dark' | 'to-light' | null

const THEME_STORAGE_KEY = 'theme-mode'

export const useThemeStore = defineStore('theme', () => {
  const themeName = ref<ThemeName>('light')
  const lastDirection = ref<ThemeDirection>(null)
  const initialized = ref(false)
  const hasStoredPreference = ref(false)

  let mediaQueryList: MediaQueryList | null = null
  let mediaQueryListener: ((event: MediaQueryListEvent) => void) | null = null

  const isDark = computed(() => themeName.value === 'dark')

  function applyTheme(theme: ThemeName) {
    if (typeof document === 'undefined') {
      return
    }

    document.documentElement.setAttribute('data-theme', theme)
    document.documentElement.style.colorScheme = theme
  }

  function resolveSystemTheme(): ThemeName {
    if (typeof window === 'undefined' || !window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'light'
    }
    return 'dark'
  }

  function setTheme(nextTheme: ThemeName, options?: { persist?: boolean; trackDirection?: boolean }) {
    const { persist = true, trackDirection = true } = options ?? {}

    if (trackDirection && themeName.value !== nextTheme) {
      lastDirection.value = nextTheme === 'dark' ? 'to-dark' : 'to-light'
    }

    themeName.value = nextTheme
    applyTheme(nextTheme)

    if (typeof window === 'undefined') {
      return
    }

    if (persist) {
      hasStoredPreference.value = true
      window.localStorage.setItem(THEME_STORAGE_KEY, nextTheme)
      return
    }

    if (!hasStoredPreference.value) {
      window.localStorage.removeItem(THEME_STORAGE_KEY)
    }
  }

  function setDark(value: boolean) {
    setTheme(value ? 'dark' : 'light')
  }

  function toggle() {
    setTheme(isDark.value ? 'light' : 'dark')
  }

  function init() {
    if (initialized.value) {
      applyTheme(themeName.value)
      return
    }

    const savedTheme = typeof window === 'undefined'
      ? null
      : window.localStorage.getItem(THEME_STORAGE_KEY)

    hasStoredPreference.value = savedTheme === 'light' || savedTheme === 'dark'

    const initialTheme = hasStoredPreference.value
      ? savedTheme as ThemeName
      : resolveSystemTheme()

    setTheme(initialTheme, {
      persist: hasStoredPreference.value,
      trackDirection: false
    })

    if (typeof window !== 'undefined') {
      mediaQueryList = window.matchMedia('(prefers-color-scheme: dark)')
      mediaQueryListener = (event) => {
        if (!hasStoredPreference.value) {
          setTheme(event.matches ? 'dark' : 'light', {
            persist: false,
            trackDirection: false
          })
        }
      }

      if (mediaQueryList.addEventListener) {
        mediaQueryList.addEventListener('change', mediaQueryListener)
      } else {
        mediaQueryList.addListener(mediaQueryListener)
      }
    }

    initialized.value = true
  }

  return {
    themeName,
    isDark,
    lastDirection,
    initialized,
    init,
    toggle,
    setTheme,
    setDark
  }
})

