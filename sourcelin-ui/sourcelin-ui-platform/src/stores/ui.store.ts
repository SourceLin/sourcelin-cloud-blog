import { defineStore } from 'pinia'
import { ref } from 'vue'

export interface ToolbarState {
  visible: boolean
  enter: boolean
}

const TOOLBAR_STORAGE_KEY = 'toolbar-state'

export const useUiStore = defineStore('ui', () => {
  const toolbar = ref<ToolbarState>({
    visible: false,
    enter: true
  })

  const searchDialogVisible = ref(false)
  const loginFlag = ref(false)
const drawerVisible = ref(false)
  const sayPublishVisible = ref(false)
  const startupLoadingVisible = ref(false)
  const startupMinDurationDone = ref(false)
  const startupRouteReady = ref(false)
  const startupHomeReady = ref(false)
  const startupStartedAt = ref(0)
  const startupRequiresHomeReady = ref(false)

  function setToolbar(nextToolbar: ToolbarState) {
    toolbar.value = nextToolbar
    localStorage.setItem(TOOLBAR_STORAGE_KEY, JSON.stringify(nextToolbar))
  }

function setDrawerVisible(value: boolean) {
    drawerVisible.value = value
  }

  function setSayPublishVisible(value: boolean) {
    sayPublishVisible.value = value
  }

  function setLoginFlag(value: boolean) {
    loginFlag.value = value
  }

  function setSearchDialogVisible(value: boolean) {
    searchDialogVisible.value = value
  }

  function resetStartupLoading() {
    startupLoadingVisible.value = false
    startupMinDurationDone.value = false
    startupRouteReady.value = false
    startupHomeReady.value = false
    startupStartedAt.value = 0
    startupRequiresHomeReady.value = false
  }

  function finishStartupLoadingIfReady() {
    const routeReady = startupRouteReady.value
    const minDurationDone = startupMinDurationDone.value
    const homeReady = startupRequiresHomeReady.value ? startupHomeReady.value : true

    if (routeReady && minDurationDone && homeReady) {
      startupLoadingVisible.value = false
    }
  }

  function startStartupLoading(options: { requiresHomeReady?: boolean } = {}) {
    resetStartupLoading()
    startupLoadingVisible.value = true
    startupStartedAt.value = Date.now()
    startupRequiresHomeReady.value = Boolean(options.requiresHomeReady)
  }

  function markStartupRouteReady() {
    startupRouteReady.value = true
    finishStartupLoadingIfReady()
  }

  function markStartupMinDurationDone() {
    startupMinDurationDone.value = true
    finishStartupLoadingIfReady()
  }

  function markStartupHomeReady() {
    startupHomeReady.value = true
    finishStartupLoadingIfReady()
  }

  function initFromStorage() {
    const storedToolbar = localStorage.getItem(TOOLBAR_STORAGE_KEY)
    if (!storedToolbar) {
      return
    }

    try {
      toolbar.value = JSON.parse(storedToolbar) as ToolbarState
    } catch {
      // ignore parse error
    }
  }

  initFromStorage()

return {
    toolbar,
    searchDialogVisible,
    loginFlag,
    drawerVisible,
    sayPublishVisible,
    startupLoadingVisible,
    startupMinDurationDone,
    startupRouteReady,
    startupHomeReady,
    startupStartedAt,
    startupRequiresHomeReady,
    setToolbar,
setDrawerVisible,
    setSayPublishVisible,
    setLoginFlag,
    setSearchDialogVisible,
    initFromStorage,
    resetStartupLoading,
    startStartupLoading,
    markStartupRouteReady,
    markStartupMinDurationDone,
    markStartupHomeReady,
    finishStartupLoadingIfReady
  }
})

