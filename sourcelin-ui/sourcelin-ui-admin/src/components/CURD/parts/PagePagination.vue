<template>
  <div v-if="showPagination" class="mt-4">
    <el-scrollbar :class="['h-8!', { 'flex-x-end': pagePosition === 'right' }]">
      <el-pagination
        v-bind="pagination"
        @size-change="emit('sizeChange', $event)"
        @current-change="emit('currentChange', $event)"
      />
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import type { IObject } from "../types";

defineProps<{
  showPagination: boolean;
  pagination: IObject;
  pagePosition?: string;
}>();

const emit = defineEmits<{
  sizeChange: [value: number];
  currentChange: [value: number];
}>();
</script>
