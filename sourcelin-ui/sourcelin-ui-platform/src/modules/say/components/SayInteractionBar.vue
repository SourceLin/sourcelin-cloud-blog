<script setup lang="ts">
import { ref } from 'vue'
import SIcon from '@/shared/components/ui/SIcon.vue'
import SPopoverMenu from '@/shared/components/ui/SPopoverMenu.vue'
import SPopconfirm from '@/shared/components/ui/SPopconfirm.vue'
import { appIcons } from '@/shared/components/ui/icons'

const props = defineProps<{
  liked: boolean
  collected: boolean
  likeCount: number
  collectCount: number
  commentPanelVisible: boolean
  headerCommentTotal: number
  canDeleteSay: boolean
  likedIconColor: string
  collectedIconColor: string
}>()

const emit = defineEmits<{
  toggleLike: [event: MouseEvent]
  toggleCollect: []
  commentAction: []
  delete: []
}>()

const actionMenuOpen = ref(false)

const sayIcons = {
  likeOutline: appIcons.heart,
  likeFilled: appIcons.heartFilled,
  comment: appIcons.comment,
  collect: appIcons.star
} as const

function closeActionMenu() {
  actionMenuOpen.value = false
}

function handleDeleteConfirm() {
  closeActionMenu()
  emit('delete')
}
</script>

<template>
  <footer class="say-footer">
    <div class="say-footer__primary">
      <button
        type="button"
        class="say-action say-action--like"
        :class="{ 'is-active': liked }"
        @click="emit('toggleLike', $event)"
      >
        <SIcon
          :icon="liked ? sayIcons.likeFilled : sayIcons.likeOutline"
          :size="16"
          :color="likedIconColor"
        />
        <span>{{ likeCount }}</span>
      </button>

      <button
        type="button"
        class="say-action say-action--collect"
        :class="{ 'is-active': collected }"
        @click="emit('toggleCollect')"
      >
        <SIcon
          :icon="sayIcons.collect"
          :size="16"
          :color="collectedIconColor"
        />
        <span>{{ collectCount }}</span>
      </button>

      <button
        type="button"
        class="say-action say-action--comment"
        :class="{ 'is-open': commentPanelVisible }"
        @click="emit('commentAction')"
      >
        <SIcon :icon="sayIcons.comment" :size="16" />
        <span>{{ headerCommentTotal }}</span>
      </button>
    </div>

    <div v-if="canDeleteSay" class="say-footer__more">
      <SPopoverMenu v-model:show="actionMenuOpen" placement="top-end" trigger="click" :flip="false">
        <template #trigger>
          <button
            type="button"
            class="say-action say-action--more"
            :class="{ 'is-open': actionMenuOpen }"
            @click.stop
          >
            <SIcon :icon="appIcons.moreHorizontal" :size="18" />
          </button>
        </template>

        <div class="say-action-menu">
          <SPopconfirm
            :show-icon="false"
            positive-text="确认删除"
            negative-text="取消"
            @positive-click="handleDeleteConfirm"
          >
            <template #trigger>
              <button
                type="button"
                class="say-action-menu__item say-action-menu__item--danger"
                @click.stop
              >
                <SIcon :icon="appIcons.trash" :size="16" />
                <span>删除</span>
              </button>
            </template>
            删除后不可恢复，确定继续吗？
          </SPopconfirm>
        </div>
      </SPopoverMenu>
    </div>
  </footer>
</template>

<style scoped lang="scss">
@import '@/shared/styles/responsive';

.say-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding-top: 18px;
  border-top: 1px solid var(--border-panel-subtle);
}

.say-footer__primary {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.say-footer__more {
  position: relative;
  flex-shrink: 0;
}

.say-action {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 38px;
  padding: 0 14px;
  border: 1px solid var(--border-panel-subtle);
  border-radius: 999px;
  background: color-mix(in srgb, var(--surface-panel-chip) 86%, transparent);
  font-size: 0.84rem;
  font-weight: 600;
  color: var(--text-secondary);
  cursor: pointer;
  transition:
    transform var(--transition-base),
    border-color var(--transition-base),
    background var(--transition-base),
    color var(--transition-base),
    box-shadow var(--transition-base);
}

.say-action:hover,
.say-action.is-open {
  border-color: var(--border-panel-default);
  background: var(--surface-panel-chip-accent);
  box-shadow: var(--shadow-panel-inline);
}

.say-action--like.is-active {
  color: var(--error-color);
  border-color: color-mix(in srgb, var(--error-color) 36%, var(--border-panel-default));
  background: color-mix(in srgb, var(--error-color) 10%, var(--surface-panel-chip-accent));
}

.say-action--collect.is-active {
  color: var(--warning-color);
  border-color: color-mix(in srgb, var(--warning-color) 36%, var(--border-panel-default));
  background: color-mix(in srgb, var(--warning-color) 12%, var(--surface-panel-chip-accent));
}

.say-action--comment.is-open {
  color: var(--primary-color);
}

.say-action--more {
  min-width: 38px;
  width: 38px;
  padding: 0;
}

.say-action--more.is-open {
  color: var(--primary-color);
}

.say-action:focus-visible {
  outline: none;
  border-color: var(--border-interactive-hover);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--primary-color) 16%, transparent);
}

.say-action--like:active,
.say-action--comment:active {
  transform: translateY(1px);
}

.say-action-menu {
  min-width: 142px;
  display: grid;
  gap: 6px;
  padding: 2px;
}

.say-action-menu__item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  min-height: 34px;
  padding: 0 10px;
  border: 1px solid transparent;
  border-radius: 10px;
  background: transparent;
  color: var(--text-primary);
  font-size: 0.82rem;
  font-weight: 600;
  cursor: pointer;
  transition:
    color var(--transition-base),
    background var(--transition-base),
    border-color var(--transition-base);
}

.say-action-menu__item:hover {
  color: var(--text-primary);
  background: color-mix(in srgb, var(--surface-panel-chip-quiet) 92%, var(--surface-panel-default));
  border-color: color-mix(in srgb, var(--border-panel-subtle) 90%, transparent);
}

.say-action-menu__item--danger {
  color: var(--error-color);
}

.say-action-menu__item--danger:hover {
  color: var(--error-color);
  border-color: color-mix(in srgb, var(--error-color) 24%, transparent);
  background: color-mix(in srgb, var(--error-color) 12%, var(--surface-panel-chip-quiet));
}

@include sourcelin-down(sm) {
  .say-footer {
    align-items: flex-start;
  }

  .say-footer__primary {
    display: grid;
    flex: 1;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 8px;
  }

  .say-footer__primary .say-action {
    width: 100%;
  }

  .say-footer__more {
    margin-left: auto;
  }

  .say-action-menu__item {
    min-height: 36px;
  }
}
</style>