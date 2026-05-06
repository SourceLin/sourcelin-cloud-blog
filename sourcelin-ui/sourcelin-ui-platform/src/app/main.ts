import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useThemeStore } from '@/stores/theme.store'
import { useUiStore } from '@/stores/ui.store'
import { useDictStore } from '@/stores/dict.store'

import '@/assets/styles/index.scss'

const STARTUP_LOADING_MIN_DURATION = 1400

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
useThemeStore(pinia).init()
useDictStore(pinia).ensureDicts(['blog_status', 'blog_comment_audit_status'])

const uiStore = useUiStore(pinia)
const initialRoute = router.resolve(window.location.pathname + window.location.search)

uiStore.startStartupLoading({
  requiresHomeReady: initialRoute.path === '/'
})

window.setTimeout(() => {
  uiStore.markStartupMinDurationDone()
}, STARTUP_LOADING_MIN_DURATION)

app.use(router)
app.mount('#app')
