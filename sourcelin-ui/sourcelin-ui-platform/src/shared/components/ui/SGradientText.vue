<template>
  <component
    :is="as"
    class="s-gradient-text"
    :class="[`s-gradient-text--${tone}`, { 'is-animated': animated }]"
    v-bind="$attrs"
  >
    <slot />
  </component>
</template>

<script setup lang="ts">
interface Props {
  as?: string
  tone?: 'brand' | 'soft' | 'accent' | 'glass' | 'glass-strong' | 'glass-soft' | 'prism'
  animated?: boolean
}

withDefaults(defineProps<Props>(), {
  as: 'span',
  tone: 'brand',
  animated: false
})
</script>

<style scoped lang="scss">
.s-gradient-text {
  display: inline-flex;
  align-items: center;
  gap: 0.24em;
  background: linear-gradient(
    var(--s-gradient-angle, 135deg),
    var(--s-gradient-start),
    var(--s-gradient-mid),
    var(--s-gradient-end)
  );
  background-size: 200% 200%;
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  color: transparent;
  font: inherit;
  letter-spacing: inherit;
  text-transform: inherit;
}

.s-gradient-text--brand {
  --s-gradient-start: var(--primary-color);
  --s-gradient-mid: color-mix(in srgb, var(--secondary-color) 64%, var(--primary-color));
  --s-gradient-end: var(--secondary-color);
}

.s-gradient-text--soft {
  --s-gradient-start: color-mix(in srgb, var(--text-primary) 72%, var(--primary-color));
  --s-gradient-mid: color-mix(in srgb, var(--primary-color) 58%, var(--text-secondary));
  --s-gradient-end: color-mix(in srgb, var(--secondary-color) 70%, var(--text-primary));
}

.s-gradient-text--accent {
  --s-gradient-start: var(--secondary-color);
  --s-gradient-mid: color-mix(in srgb, var(--accent-color) 70%, var(--secondary-color));
  --s-gradient-end: var(--primary-color);
}

.s-gradient-text--glass {
  --s-gradient-start: color-mix(in srgb, var(--primary-color) 74%, var(--text-primary));
  --s-gradient-mid: color-mix(in srgb, var(--secondary-color) 68%, var(--primary-color));
  --s-gradient-end: color-mix(in srgb, var(--accent-color) 72%, var(--secondary-color));
}

.s-gradient-text--glass-strong {
  --s-gradient-start: color-mix(in srgb, var(--primary-color) 82%, var(--text-primary));
  --s-gradient-mid: color-mix(in srgb, var(--secondary-color) 76%, var(--primary-color));
  --s-gradient-end: color-mix(in srgb, var(--accent-color) 80%, var(--secondary-color));
}

.s-gradient-text--glass-soft {
  --s-gradient-start: color-mix(in srgb, var(--text-primary) 78%, var(--primary-color));
  --s-gradient-mid: color-mix(in srgb, var(--primary-color) 46%, var(--text-secondary));
  --s-gradient-end: color-mix(in srgb, var(--secondary-color) 52%, var(--text-primary));
}

/* 棱镜分光 tone：靛蓝 → 琥珀金 → 极光绿 */
.s-gradient-text--prism {
  --s-gradient-angle: 120deg;
  --s-gradient-start: var(--primary-color);
  --s-gradient-mid: color-mix(in srgb, var(--companion-color) 58%, var(--secondary-color));
  --s-gradient-end: color-mix(in srgb, var(--aurora-color) 68%, var(--companion-color));
}

.is-animated {
  animation: gradient-shift 8s ease-in-out infinite;
}

@keyframes gradient-shift {
  0%,
  100% {
    background-position: 0% 50%;
  }

  50% {
    background-position: 100% 50%;
  }
}
</style>
