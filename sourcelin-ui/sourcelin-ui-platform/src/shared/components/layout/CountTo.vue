<template>
  <span class="count-to">
    <slot :value="displayValue">{{ displayValue }}</slot>
  </span>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'

interface Props {
  value: number
  duration?: number
  start?: number
  decimals?: number
}

const props = withDefaults(defineProps<Props>(), {
  duration: 1200,
  start: 0,
  decimals: 0
})

const displayValue = ref(props.start)
const rafId = ref<number | null>(null)
const startTime = ref(0)

const target = computed(() => Number(props.value || 0))

const stop = () => {
  if (rafId.value !== null) {
    cancelAnimationFrame(rafId.value)
    rafId.value = null
  }
}

const animate = (timestamp: number) => {
  if (!startTime.value) {
    startTime.value = timestamp
  }
  const elapsed = timestamp - startTime.value
  const progress = Math.min(elapsed / props.duration, 1)
  const current = props.start + (target.value - props.start) * progress
  const factor = Math.pow(10, props.decimals)
  displayValue.value = Math.round(current * factor) / factor

  if (progress < 1) {
    rafId.value = requestAnimationFrame(animate)
  } else {
    stop()
  }
}

const startAnimation = () => {
  stop()
  startTime.value = 0
  displayValue.value = props.start
  rafId.value = requestAnimationFrame(animate)
}

watch(target, () => startAnimation())

onMounted(() => {
  startAnimation()
})
</script>

<style scoped>
.count-to {
  display: inline-flex;
  align-items: baseline;
}
</style>

