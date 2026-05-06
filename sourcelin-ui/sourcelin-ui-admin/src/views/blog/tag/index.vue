<template>
  <ModuleListShell
    title="标签管理"
    description="标签名称、图标、摘要与排序管理。"
    :module-api="TagAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      add: 'blog:tag:add',
      edit: 'blog:tag:edit',
      remove: 'blog:tag:remove',
    }"
  />
</template>

<script setup lang="ts">
import TagAPI from "@/api/blog/tag";
import ModuleListShell from "../shared/ModuleListShell.vue";

defineOptions({
  name: "BlogTag",
  inheritAttrs: false,
});

const queryFields = [
  { prop: "keywords", label: "关键字", type: "input", placeholder: "标签名称/摘要" },
];

const columns = [
  { prop: "name", label: "标签名称", minWidth: 150 },
  { prop: "icon", label: "图标", type: "icon", minWidth: 140 },
  { prop: "summary", label: "摘要", minWidth: 220 },
  { prop: "orderNum", label: "排序", width: 90, align: "center" as const },
  { prop: "clickCount", label: "点击量", width: 90, align: "center" as const },
];

const formFields = [
  { prop: "name", label: "标签名称", type: "input", required: true },
  { prop: "icon", label: "图标", type: "icon" },
  { prop: "orderNum", label: "排序", type: "number", min: 0, max: 9999 },
  { prop: "summary", label: "摘要", type: "textarea", rows: 3, span: 24 },
];

const initialForm = {
  orderNum: 1,
};
</script>
