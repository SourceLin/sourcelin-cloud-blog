import { computed } from 'vue'
import { useThemeStore } from '@/stores/theme'

const PALETTE_VARIABLES = [
  '--primary-color',
  '--primary-hover',
  '--secondary-color',
  '--success-color',
  '--warning-color',
  '--error-color',
  '--info-color'
] as const

const PALETTE_FALLBACKS = [
  'var(--primary-color)',
  'var(--primary-hover)',
  'var(--secondary-color)',
  'var(--success-color)',
  'var(--warning-color)',
  'var(--error-color)',
  'var(--info-color)'
] as const

function readCssVariable(name: string) {
  if (typeof window === 'undefined') {
    return ''
  }

  return getComputedStyle(document.documentElement).getPropertyValue(name).trim()
}

export function useThemePalette() {
  const themeStore = useThemeStore()

  return computed(() => {
    void themeStore.themeName

    const palette = PALETTE_VARIABLES
      .map((variable) => readCssVariable(variable))
      .filter(Boolean)

    return palette.length > 0 ? palette : [...PALETTE_FALLBACKS]
  })
}

