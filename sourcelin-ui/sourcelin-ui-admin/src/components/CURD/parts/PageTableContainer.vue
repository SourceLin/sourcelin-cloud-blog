<template>
  <el-table
    ref="tableRef"
    v-loading="loading"
    v-bind="contentConfig.table"
    :data="pageData"
    :row-key="pk"
    class="flex-1"
    @selection-change="emit('selectionChange', $event)"
    @filter-change="emit('filterChange', $event)"
  >
    <template v-for="col in cols" :key="col.prop">
      <el-table-column v-if="col.show" v-bind="col">
        <template #default="scope">
          <template v-if="col.templet === 'image'">
            <template v-if="col.prop">
              <template v-if="Array.isArray(scope.row[col.prop])">
                <template v-for="(item, index) in scope.row[col.prop]" :key="item">
                  <el-image
                    :src="item"
                    :preview-src-list="scope.row[col.prop]"
                    :initial-index="Number(index)"
                    :preview-teleported="true"
                    :style="`width: ${col.imageWidth ?? 40}px; height: ${col.imageHeight ?? 40}px`"
                  />
                </template>
              </template>
              <template v-else>
                <el-image
                  :src="scope.row[col.prop]"
                  :preview-src-list="[scope.row[col.prop]]"
                  :preview-teleported="true"
                  :style="`width: ${col.imageWidth ?? 40}px; height: ${col.imageHeight ?? 40}px`"
                />
              </template>
            </template>
          </template>
          <template v-else-if="col.templet === 'list'">
            <template v-if="col.prop">
              {{ (col.selectList ?? {})[scope.row[col.prop]] }}
            </template>
          </template>
          <template v-else-if="col.templet === 'url'">
            <template v-if="col.prop">
              <el-link type="primary" :href="scope.row[col.prop]" target="_blank">
                {{ scope.row[col.prop] }}
              </el-link>
            </template>
          </template>
          <template v-else-if="col.templet === 'switch'">
            <template v-if="col.prop">
              <el-switch
                v-model="scope.row[col.prop]"
                :active-value="col.activeValue ?? 1"
                :inactive-value="col.inactiveValue ?? 0"
                :inline-prompt="true"
                :active-text="col.activeText ?? ''"
                :inactive-text="col.inactiveText ?? ''"
                :validate-event="false"
                :disabled="!hasButtonPerm(col.prop)"
                @change="
                  pageData.length > 0 && handleModify(col.prop, scope.row[col.prop], scope.row)
                "
              />
            </template>
          </template>
          <template v-else-if="col.templet === 'input'">
            <template v-if="col.prop">
              <el-input
                v-model="scope.row[col.prop]"
                :type="col.inputType ?? 'text'"
                :disabled="!hasButtonPerm(col.prop)"
                @blur="handleModify(col.prop, scope.row[col.prop], scope.row)"
              />
            </template>
          </template>
          <template v-else-if="col.templet === 'price'">
            <template v-if="col.prop">
              {{ `${col.priceFormat ?? ""}${scope.row[col.prop]}` }}
            </template>
          </template>
          <template v-else-if="col.templet === 'percent'">
            <template v-if="col.prop">{{ scope.row[col.prop] }}%</template>
          </template>
          <template v-else-if="col.templet === 'icon'">
            <template v-if="col.prop">
              <template v-if="isElementPlusIconName(scope.row[col.prop])">
                <el-icon>
                  <component :is="scope.row[col.prop]" />
                </el-icon>
              </template>
              <template v-else>
                <div class="i-svg:{{ scope.row[col.prop] }}" />
              </template>
            </template>
          </template>
          <template v-else-if="col.templet === 'date'">
            <template v-if="col.prop">
              {{
                scope.row[col.prop]
                  ? useDateFormat(scope.row[col.prop], col.dateFormat ?? "YYYY-MM-DD HH:mm:ss")
                      .value
                  : ""
              }}
            </template>
          </template>
          <template v-else-if="col.templet === 'tool'">
            <template v-for="(btn, index) in tableToolbarBtn" :key="index">
              <el-button
                v-if="btn.render === undefined || btn.render(scope.row)"
                v-hasPerm="btn.perm ?? '*:*:*'"
                v-bind="btn.attrs"
                @click="
                  handleOperate({
                    name: btn.name,
                    row: scope.row,
                    column: scope.column,
                    $index: scope.$index,
                  })
                "
              >
                {{ btn.text }}
              </el-button>
            </template>
          </template>
          <template v-else-if="col.templet === 'custom'">
            <slot :name="col.slotName ?? col.prop" :prop="col.prop" v-bind="scope" />
          </template>
        </template>
      </el-table-column>
    </template>
  </el-table>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useDateFormat } from "@vueuse/core";
import type { TableInstance } from "element-plus";
import type { IContentConfig, IObject, IOperateData } from "../types";

type ToolbarButton = {
  name: string;
  text?: string;
  attrs?: IObject;
  render?: (row: IObject) => boolean;
  perm?: string | null;
};

defineProps<{
  contentConfig: IContentConfig;
  loading: boolean;
  pageData: IObject[];
  pk: string;
  cols: IObject[];
  tableToolbarBtn: ToolbarButton[];
  hasButtonPerm: (action: string) => boolean;
  handleModify: (field: string, value: boolean | string | number, row: Record<string, any>) => void;
  handleOperate: (data: IOperateData) => void;
  isElementPlusIconName: (icon?: string | null) => boolean;
}>();

const emit = defineEmits<{
  selectionChange: [selection: IObject[]];
  filterChange: [filters: IObject];
}>();

const tableRef = ref<TableInstance>();

defineExpose({
  clearSelection: () => tableRef.value?.clearSelection(),
});
</script>
