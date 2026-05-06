<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import SButton from '@/shared/components/ui/SButton.vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SSurfacePanel from '@/shared/components/ui/SSurfacePanel.vue'
import { appIcons, type AppIconName } from '@/shared/components/ui/icons'

interface Props {
  title?: string
  message?: string
  actionText?: string
  size?: 'small' | 'default' | 'large'
  variant?: 'page' | 'section' | 'inline'
  icon?: AppIconName
  hideIcon?: boolean
  descriptionMode?: 'plain' | 'typewriter'
  typewriterSpeed?: number
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  message: '暂无数据',
  actionText: '',
  size: 'default',
  variant: 'section',
  icon: undefined,
  hideIcon: false,
  descriptionMode: 'plain',
  typewriterSpeed: 42
})

const emit = defineEmits<{
  action: []
}>()

const descriptionText = computed(() => props.message || '暂无数据')
const typedDescription = ref(descriptionText.value)
let typewriterTimer: ReturnType<typeof setInterval> | null = null

function stopTypewriter() {
  if (typewriterTimer) {
    clearInterval(typewriterTimer)
    typewriterTimer = null
  }
}

function startTypewriter(text: string) {
  stopTypewriter()
  typedDescription.value = ''
  if (!text) {
    return
  }

  const speed = Math.max(props.typewriterSpeed, 16)
  let cursor = 0

  typewriterTimer = setInterval(() => {
    cursor += 1
    typedDescription.value = text.slice(0, cursor)

    if (cursor >= text.length) {
      stopTypewriter()
    }
  }, speed)
}

watch(
  [() => props.descriptionMode, descriptionText],
  ([mode, text]) => {
    if (mode === 'typewriter') {
      startTypewriter(text)
      return
    }

    stopTypewriter()
    typedDescription.value = text
  },
  { immediate: true }
)

onBeforeUnmount(() => {
  stopTypewriter()
})

const renderedDescription = computed(() =>
  props.descriptionMode === 'typewriter' ? typedDescription.value : descriptionText.value
)

const showTypewriterCaret = computed(() => {
  if (props.descriptionMode !== 'typewriter') {
    return false
  }

  return typedDescription.value.length < descriptionText.value.length
})

const resolvedIcon = computed(() => appIcons[props.icon ?? 'sparkles'])

const iconSize = computed(() => {
  const sizes = {
    small: props.variant === 'inline' ? 16 : 24,
    default: props.variant === 'inline' ? 18 : 28,
    large: props.variant === 'inline' ? 20 : 34
  } as const

  return sizes[props.size]
})

const iconShellSize = computed(() => {
  if (props.variant === 'inline') {
    const sizes = {
      small: '2rem',
      default: '2.3rem',
      large: '2.6rem'
    } as const
    return sizes[props.size]
  }

  const sizes = {
    small: '3.3rem',
    default: props.variant === 'page' ? '4.6rem' : '4rem',
    large: props.variant === 'page' ? '5.4rem' : '4.8rem'
  } as const
  return sizes[props.size]
})

const shellVariant = computed(() => (props.variant === 'inline' ? 'inset' : 'soft'))
</script>

<template>
  <div
    class="platform-empty"
    :class="[
      `platform-empty--${variant}`,
      `platform-empty--${size}`,
      { 'platform-empty--icon-hidden': hideIcon }
    ]"
    role="status"
    aria-live="polite"
  >
    <SSurfacePanel class="platform-empty__shell" :variant="shellVariant">
      <SSurfacePanel
        v-if="!hideIcon"
        class="platform-empty__icon-shell"
        variant="inset"
        :style="{ '--empty-icon-shell-size': iconShellSize }"
      >
        <slot name="icon">
          <SIcon :icon="resolvedIcon" :size="iconSize" />
        </slot>
      </SSurfacePanel>

      <div class="platform-empty__content">
        <h3 v-if="title || $slots.title" class="platform-empty__title">
          <slot name="title">{{ title }}</slot>
        </h3>
        <p class="platform-empty__description">
          <slot name="description">{{ renderedDescription }}</slot>
          <span v-if="showTypewriterCaret && !$slots.description" class="platform-empty__caret" aria-hidden="true" />
        </p>
      </div>

      <slot name="extra" />

      <div v-if="$slots.action || actionText" class="platform-empty__actions">
        <slot name="action">
          <SButton variant="site" @click="emit('action')">
            {{ actionText }}
          </SButton>
        </slot>
      </div>
    </SSurfacePanel>
  </div>
</template>

<style lang="scss" scoped>
.platform-empty {
  --empty-shell-gap: 0.88rem;
  --empty-content-gap: 0.32rem;
  --empty-actions-offset: 0.06rem;
  width: 100%;
  animation: platform-empty-enter var(--transition-base) ease;
}

.platform-empty__shell {
  display: grid;
  justify-items: center;
  text-align: center;
  row-gap: var(--empty-shell-gap);
  width: 100%;
  border: 1px solid var(--border-panel-subtle);
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-soft);
}

.platform-empty--page {
  --empty-shell-gap: 0.96rem;
  --empty-content-gap: 0.34rem;
  --empty-actions-offset: 0.08rem;
}

.platform-empty--inline {
  --empty-shell-gap: 0.7rem;
  --empty-content-gap: 0.22rem;
  --empty-actions-offset: 0;
}

.platform-empty--page .platform-empty__shell {
  min-height: clamp(15rem, 28vh, 19rem);
  padding: clamp(2.1rem, 5vw, 3.1rem) clamp(1rem, 3vw, 1.7rem);
}

.platform-empty--section .platform-empty__shell {
  min-height: clamp(10.2rem, 21vh, 13rem);
  padding: clamp(1.5rem, 3.2vw, 2.2rem) clamp(1rem, 2.4vw, 1.45rem);
}

.platform-empty--inline .platform-empty__shell {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  align-items: center;
  justify-items: start;
  text-align: left;
  gap: 0.7rem;
  min-height: 0;
  padding: 0.7rem 0.9rem;
  box-shadow: var(--highlight-panel-soft), var(--shadow-panel-inline);
}

.platform-empty__icon-shell {
  width: var(--empty-icon-shell-size);
  height: var(--empty-icon-shell-size);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  color: color-mix(in srgb, var(--primary-color) 78%, var(--text-secondary));
  border: 1px solid var(--border-panel-badge-accent);
  background: var(--surface-panel-chip-accent);
  box-shadow: var(--highlight-panel-chip);
}

.platform-empty--inline .platform-empty__icon-shell {
  border-radius: var(--border-radius-lg);
}

.platform-empty__content {
  display: grid;
  row-gap: var(--empty-content-gap);
  justify-items: center;
  max-width: min(32rem, 100%);
}

.platform-empty--inline .platform-empty__content {
  justify-items: start;
  max-width: 100%;
}

.platform-empty__title {
  margin: 0;
  font-size: clamp(1.02rem, 2vw, 1.2rem);
  font-weight: 700;
  color: var(--title-color);
  line-height: 1.2;
}

.platform-empty--small .platform-empty__title {
  font-size: 0.96rem;
}

.platform-empty--large .platform-empty__title {
  font-size: clamp(1.12rem, 2.2vw, 1.32rem);
}

.platform-empty__description {
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.92rem;
  line-height: 1.7;
  white-space: pre-wrap;
}

.platform-empty--inline .platform-empty__description {
  font-size: 0.85rem;
  line-height: 1.55;
}

.platform-empty__actions {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-top: var(--empty-actions-offset);
}

.platform-empty__caret {
  display: inline-block;
  width: 2px;
  height: 1em;
  margin-left: 2px;
  background: var(--primary-color);
  animation: platform-empty-caret 1s step-end infinite;
  vertical-align: -2px;
}

.platform-empty--icon-hidden .platform-empty__shell {
  grid-template-columns: minmax(0, 1fr);
}

.platform-empty--icon-hidden .platform-empty__content {
  justify-items: center;
}

@keyframes platform-empty-enter {
  from {
    opacity: 0;
    transform: translateY(6px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes platform-empty-caret {
  50% {
    opacity: 0;
  }
}
</style>


