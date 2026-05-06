<template>
  <header class="section-prelude">
    <div class="prelude-shell sourcelin-prelude-surface">
      <span class="prelude-cap" aria-hidden="true" />
      <component :is="titleTag" class="prelude-title">{{ title }}</component>
      <span class="prelude-bridge" aria-hidden="true" />
      <span class="prelude-orb" aria-hidden="true">
        <span class="prelude-orb-core" />
      </span>
    </div>
  </header>
</template>

<script setup lang="ts">
interface Props {
  title: string
  titleTag?: 'h2' | 'h3'
}

withDefaults(defineProps<Props>(), {
  titleTag: 'h2',
})
</script>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.section-prelude {
  display: grid;
}

.prelude-shell {
  display: flex;
  align-items: center;
  gap: 0.48rem;
  padding: 0.92rem 0.75rem 0.92rem 0.66rem;
  border-radius: calc(var(--border-radius-xl) + 4px);
}

.prelude-cap {
  flex-shrink: 0;
  width: clamp(0.9rem, 1.5vw, 1.4rem);
  height: 1px;
  border-radius: 999px;
  background: linear-gradient(90deg, transparent, color-mix(in srgb, var(--primary-color) 76%, transparent));
  opacity: 0.72;
  transform: translateY(1px);
}

.prelude-orb {
  position: relative;
  flex-shrink: 0;
  width: 0.58rem;
  height: 0.58rem;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  outline: 1.5px solid color-mix(in srgb, var(--primary-color) 30%, transparent);
  outline-offset: 0;
  box-shadow:
    0 0 0 4px color-mix(in srgb, var(--primary-color) 8%, transparent),
    0 0 16px color-mix(in srgb, var(--primary-color) 16%, transparent);
  animation: prelude-orb-pulse 3.8s ease-in-out infinite;
  transform-origin: center;
}

.prelude-orb-core {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  /* 增加琥珀金伴色高光（遵循 80/15/5 配比） */
  background: linear-gradient(180deg, color-mix(in srgb, var(--primary-color) 72%, var(--secondary-color)), color-mix(in srgb, var(--secondary-color) 74%, var(--companion-color)));
  box-shadow: inset 0 1px 0 color-mix(in srgb, var(--surface-white-25) 96%, transparent);
}

.prelude-bridge {
  flex: 1 1 auto;
  flex-shrink: 0;
  min-width: clamp(1.25rem, 2vw, 3rem);
  height: 1px;
  border-radius: 999px;
  background: linear-gradient(90deg, color-mix(in srgb, var(--primary-color) 82%, transparent), transparent 86%);
  opacity: 0.78;
  transform: translateY(1px);
}

.prelude-title {
  display: flex;
  align-items: center;
  flex-shrink: 0;
  min-width: 0;
  margin: 0;
  transform: translateY(-0.01rem);
  font-family: var(--font-mono), monospace;
  font-size: clamp(0.92rem, 1.08vw, 1.15rem);
  line-height: 1.08;
  font-weight: 700;
  letter-spacing: 0.09em;
  text-transform: uppercase;
  color: color-mix(in srgb, var(--title-color) 90%, var(--primary-color));
  white-space: nowrap;
}

@media (max-width: 1080px) {
  .prelude-shell {
    padding: 0.88rem 0.68rem 0.88rem 0.62rem;
  }
}

@include sourcelin-down(sm) {
  .prelude-shell {
    gap: 0.52rem;
    padding: 0.44rem 0.58rem 0.44rem 0.56rem;
  }

  .prelude-cap {
    width: 0.78rem;
  }

  .prelude-orb {
    width: 0.52rem;
    height: 0.52rem;
  }

  .prelude-bridge {
    min-width: 1rem;
  }

  .prelude-title {
    font-size: 0.96rem;
  }
}

@keyframes prelude-orb-pulse {
  0%,
  100% {
    transform: scale(1);
    box-shadow:
      0 0 0 4px color-mix(in srgb, var(--primary-color) 8%, transparent),
      0 0 16px color-mix(in srgb, var(--primary-color) 16%, transparent);
  }

  50% {
    transform: scale(1.09);
    box-shadow:
      0 0 0 8px color-mix(in srgb, var(--primary-color) 7%, transparent),
      0 0 26px color-mix(in srgb, var(--primary-color) 30%, transparent);
  }
}
</style>
