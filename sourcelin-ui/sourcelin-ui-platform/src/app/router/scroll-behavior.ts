import type { RouterScrollBehavior } from 'vue-router'

export const resolveAppScrollPosition: RouterScrollBehavior = (to, from, savedPosition) => {
  if (savedPosition) {
    return savedPosition
  }

  if (to.path === from.path) {
    return false
  }

  return { left: 0, top: 0 }
}
