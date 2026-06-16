<template>
  <el-button
    v-if="enableEdit && hasEditPermission"
    type="primary"
    link
    size="small"
    :icon="Edit"
    @click="emit('edit', row)"
  >
    {{ editText || "编辑" }}
  </el-button>
  <el-button
    v-if="enableDelete && hasDeletePermission"
    type="danger"
    link
    size="small"
    :icon="Delete"
    @click="emit('delete', row)"
  >
    删除
  </el-button>
  <template v-for="action in rowActions" :key="action.key">
    <template v-if="!action.permission">
      <el-button
        v-if="isRowActionVisible(action, row)"
        :type="resolveButtonType(action.type)"
        link
        size="small"
        @click="emit('rowAction', action, row)"
      >
        {{ action.label }}
      </el-button>
    </template>
    <template v-else>
      <el-button
        v-if="isRowActionVisible(action, row)"
        v-hasPerm="action.permission"
        :type="resolveButtonType(action.type)"
        link
        size="small"
        @click="emit('rowAction', action, row)"
      >
        {{ action.label }}
      </el-button>
    </template>
  </template>
</template>

<script setup lang="ts">
import { Delete, Edit } from "@element-plus/icons-vue";
import type { ElButtonType, RowAction } from "../types/module-list-shell";

defineProps<{
  row: Record<string, any>;
  enableEdit: boolean;
  enableDelete: boolean;
  hasEditPermission: boolean;
  hasDeletePermission: boolean;
  rowActions: RowAction[];
  resolveButtonType: (type?: string) => ElButtonType;
  isRowActionVisible: (action: RowAction, row: Record<string, any>) => boolean;
  editText?: string;
}>();

const emit = defineEmits<{
  edit: [row: Record<string, any>];
  delete: [row: Record<string, any>];
  rowAction: [action: RowAction, row: Record<string, any>];
}>();
</script>
