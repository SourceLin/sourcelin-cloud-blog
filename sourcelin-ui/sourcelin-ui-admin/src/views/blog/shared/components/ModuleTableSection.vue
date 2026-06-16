<template>
  <el-card shadow="hover" class="table-section">
    <div class="table-section__toolbar">
      <div class="table-section__toolbar--actions">
        <el-button
          v-if="enableCreate && hasCreatePermission"
          type="success"
          :icon="Plus"
          @click="emit('create')"
        >
          新增
        </el-button>
        <el-button
          v-if="enableDelete && hasDeletePermission"
          type="danger"
          :icon="Delete"
          :disabled="selectedIds.length === 0"
          @click="emit('delete')"
        >
          删除
        </el-button>
      </div>
    </div>

    <el-table
      v-loading="loading"
      :data="displayRows"
      border
      stripe
      :row-key="idKey"
      :default-expand-all="treeDefaultExpandAll"
      :tree-props="{ children: treeChildrenKey, hasChildren: 'hasChildren' }"
      class="table-section__content"
      @selection-change="emit('selectionChange', $event)"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column
        v-for="column in columns"
        :key="column.prop"
        :prop="column.prop"
        :label="column.label"
        :width="column.width"
        :min-width="column.minWidth || 120"
        :align="column.align || 'left'"
        show-overflow-tooltip
      >
        <template #default="{ row }">
          <template v-if="column.type === 'image'">
            <el-image
              v-if="row[column.prop]"
              :src="row[column.prop]"
              fit="cover"
              style="width: 40px; height: 40px; border-radius: 4px"
              preview-teleported
              :preview-src-list="[row[column.prop]]"
            />
            <span v-else>-</span>
          </template>
          <template v-else-if="column.type === 'icon'">
            <span v-if="row[column.prop]" class="icon-cell">
              <el-icon v-if="resolveElementIconName(row[column.prop])" class="icon-cell__icon">
                <component :is="resolveElementIconName(row[column.prop])!" />
              </el-icon>
              <span v-else :class="`i-svg:${row[column.prop]}`" class="icon-cell__icon" />
            </span>
            <span v-else>-</span>
          </template>
          <template v-else-if="column.type === 'tag'">
            <el-tag :type="resolveTagType(column, row[column.prop])">
              {{ resolveTagLabel(column, row[column.prop]) }}
            </el-tag>
          </template>
          <template v-else-if="column.formatter">
            {{ column.formatter(row) }}
          </template>
          <template v-else>
            {{ row[column.prop] ?? "-" }}
          </template>
        </template>
      </el-table-column>

      <el-table-column fixed="right" label="操作" width="260" align="center">
        <template #default="{ row }">
          <ModuleActionColumn
            :row="row"
            :enable-edit="enableEdit"
            :enable-delete="enableDelete"
            :has-edit-permission="hasEditPermission"
            :has-delete-permission="hasDeletePermission"
            :row-actions="rowActions"
            :resolve-button-type="resolveButtonType"
            :is-row-action-visible="isRowActionVisible"
            :edit-text="editText"
            @edit="emit('edit', $event)"
            @delete="emit('delete', $event)"
            @row-action="emit('rowAction', $event, row)"
          />
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-if="total > 0"
      v-model:total="totalProxy"
      v-model:page="queryParams.page"
      v-model:page-size="queryParams.pageSize"
      @pagination="emit('pagination')"
    />
  </el-card>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { Delete, Plus } from "@element-plus/icons-vue";
import ModuleActionColumn from "./ModuleActionColumn.vue";
import type { ElButtonType, RowAction, TableColumn } from "../types/module-list-shell";

const props = defineProps<{
  loading: boolean;
  displayRows: Record<string, any>[];
  idKey: string;
  treeDefaultExpandAll: boolean;
  treeChildrenKey: string;
  columns: TableColumn[];
  total: number;
  queryParams: Record<string, any>;
  selectedIds: Array<string | number>;
  enableCreate: boolean;
  enableEdit: boolean;
  enableDelete: boolean;
  hasCreatePermission: boolean;
  hasEditPermission: boolean;
  hasDeletePermission: boolean;
  rowActions: RowAction[];
  resolveElementIconName: (icon?: string | null) => string;
  resolveTagType: (
    column: TableColumn,
    value: unknown
  ) => "success" | "warning" | "danger" | "info" | "primary";
  resolveTagLabel: (column: TableColumn, value: unknown) => string;
  resolveButtonType: (type?: string) => ElButtonType;
  isRowActionVisible: (action: RowAction, row: Record<string, any>) => boolean;
  editText?: string;
}>();

const emit = defineEmits<{
  create: [];
  delete: [row?: Record<string, any>];
  selectionChange: [selection: Record<string, any>[]];
  pagination: [];
  edit: [row: Record<string, any>];
  rowAction: [action: RowAction, row: Record<string, any>];
}>();

const totalProxy = computed({
  get: () => props.total,
  set: () => undefined,
});
</script>
