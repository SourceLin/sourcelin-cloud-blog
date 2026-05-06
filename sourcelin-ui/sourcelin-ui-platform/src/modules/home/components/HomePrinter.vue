<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'

interface Props {
  printerInfo?: string
  duration?: number
  delay?: number
  working?: boolean
  once?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  printerInfo: '',
  duration: 100,
  delay: 3000,
  working: true,
  once: false
})

const content = ref('')
const cursor = ref(0)
const print = ref(true)
let timer: number | null = null
let timeout: number | null = null

const clearTimers = () => {
  if (timeout !== null) {
    window.clearTimeout(timeout)
    timeout = null
  }
  if (timer !== null) {
    window.clearInterval(timer)
    timer = null
  }
}

const work = () => {
  let next = cursor.value + (print.value ? 1 : -1)
  const length = props.printerInfo.length

  if (print.value) {
    if (next === length + 1) {
      next -= 2
      print.value = false
    }
  } else if (next === -1) {
    next += 2
    print.value = true
  }

  cursor.value = next
}

const start = () => {
  clearTimers()
  timeout = window.setTimeout(() => {
    timer = window.setInterval(() => {
      work()
      const length = props.printerInfo.length

      if (cursor.value === 0 || (cursor.value === length && !props.once)) {
        if (timer !== null) {
          window.clearInterval(timer)
          timer = null
        }
        start()
      } else if (cursor.value === length && props.once) {
        if (timer !== null) {
          window.clearInterval(timer)
          timer = null
        }
      }
    }, props.duration)
  }, props.delay)
}

const resetPrinter = () => {
  cursor.value = 0
  clearTimers()

  if (props.working) {
    start()
    return
  }

  content.value = props.printerInfo
}

watch(() => props.working, resetPrinter)
watch(() => props.printerInfo, resetPrinter)
watch(cursor, () => {
  content.value = props.printerInfo.slice(0, cursor.value)
})

onMounted(() => {
  if (props.working) {
    start()
    return
  }

  content.value = props.printerInfo
})

onBeforeUnmount(() => {
  clearTimers()
})
</script>

<template>
  <div class="printer-shell">
    <span class="typewriter-text">{{ content }}<span class="cursor-blink">|</span></span>
  </div>
</template>

<style scoped lang="scss">
.printer-shell {
  display: inline-flex;
}

.typewriter-text {
  font-size: 1.4rem;
  color: var(--surface-white-90);
  text-shadow: 0 2px 10px var(--surface-black-30);
  font-family: 'Georgia', serif;
  font-style: italic;
}

.cursor-blink {
  margin-left: 2px;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}
</style>

