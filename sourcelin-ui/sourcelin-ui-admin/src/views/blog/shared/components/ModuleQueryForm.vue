<template>
  <div class="filter-section">
    <el-form :model="queryParams" :inline="true">
      <el-form-item v-for="field in queryFields" :key="field.prop" :label="field.label">
        <el-input
          v-if="field.type === 'input' || !field.type"
          v-model="queryParams[field.prop]"
          :placeholder="field.placeholder || `请输入${field.label}`"
          clearable
          :style="{ width: field.width || '220px' }"
          @keyup.enter="emit('query')"
        />
        <el-select
          v-else-if="field.type === 'select'"
          v-model="queryParams[field.prop]"
          :placeholder="field.placeholder || `请选择${field.label}`"
          clearable
          :style="{ width: field.width || '220px' }"
        >
          <el-option
            v-for="option in field.options || []"
            :key="`${field.prop}-${option.value}`"
            :label="option.label"
            :value="option.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item class="search-buttons">
        <el-button type="primary" :icon="Search" @click="emit('query')">搜索</el-button>
        <el-button :icon="Refresh" @click="emit('reset')">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { Refresh, Search } from "@element-plus/icons-vue";
import type { QueryField } from "../types/module-list-shell";

defineProps<{
  queryParams: Record<string, any>;
  queryFields: QueryField[];
}>();

const emit = defineEmits<{
  query: [];
  reset: [];
}>();
</script>
