<template>
  <div class="flex flex-col md:flex-row justify-between gap-y-2.5 mb-2.5">
    <div class="toolbar-left flex gap-y-2.5 gap-x-2 md:gap-x-3 flex-wrap">
      <template v-for="(btn, index) in toolbarLeftBtn" :key="index">
        <el-button
          v-hasPerm="btn.perm ?? '*:*:*'"
          v-bind="btn.attrs"
          :disabled="btn.name === 'delete' && removeIds.length === 0"
          @click="emit('toolbar', btn.name)"
        >
          {{ btn.text }}
        </el-button>
      </template>
    </div>

    <div class="toolbar-right flex gap-y-2.5 gap-x-2 md:gap-x-3 flex-wrap">
      <template v-for="(btn, index) in toolbarRightBtn" :key="index">
        <el-popover v-if="btn.name === 'filter'" placement="bottom" trigger="click">
          <template #reference>
            <el-button v-bind="btn.attrs"></el-button>
          </template>
          <el-scrollbar max-height="350px">
            <template v-for="col in cols" :key="col.prop">
              <el-checkbox v-if="col.prop" v-model="col.show" :label="col.label" />
            </template>
          </el-scrollbar>
        </el-popover>
        <el-button
          v-else
          v-hasPerm="btn.perm ?? '*:*:*'"
          v-bind="btn.attrs"
          @click="emit('toolbar', btn.name)"
        ></el-button>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { IObject } from "../types";

type ToolbarButton = {
  name: string;
  text?: string;
  attrs?: IObject;
  perm?: string | null;
};

defineProps<{
  toolbarLeftBtn: ToolbarButton[];
  toolbarRightBtn: ToolbarButton[];
  cols: IObject[];
  removeIds: Array<number | string>;
}>();

const emit = defineEmits<{
  toolbar: [name: string];
}>();
</script>
