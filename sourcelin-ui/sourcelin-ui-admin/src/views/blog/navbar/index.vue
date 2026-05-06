<template>
  <ModuleListShell
    title="门户导航栏"
    description="门户导航栏目录维护（名称、路径、排序、组件、可见性）。"
    :module-api="NavbarAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :tree-config="treeConfig"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      add: 'blog:navbar:add',
      edit: 'blog:navbar:edit',
      remove: 'blog:navbar:remove',
    }"
  />
</template>

<script setup lang="ts">
import NavbarAPI from "@/api/blog/navbar";
import ModuleListShell from "../shared/ModuleListShell.vue";
import { VISIBLE_TAG_MAP } from "../shared/constants";

defineOptions({
  name: "BlogNavbar",
  inheritAttrs: false,
});

const NAVBAR_TYPE_META = {
  M: { label: "目录", tagType: "primary" },
  C: { label: "菜单", tagType: "success" },
  F: { label: "按钮", tagType: "warning" },
} as const;

const NAVBAR_TYPE_OPTIONS = Object.entries(NAVBAR_TYPE_META).map(([value, item]) => ({
  value,
  label: item.label,
}));

const NAVBAR_TYPE_TAG_MAP = Object.fromEntries(
  Object.entries(NAVBAR_TYPE_META).map(([value, item]) => [
    value,
    { label: item.label, type: item.tagType },
  ])
);

const NAVBAR_FRAME_OPTIONS = [
  { label: "是", value: "0" },
  { label: "否", value: "1" },
];

const visibleOptions = Object.entries(VISIBLE_TAG_MAP).map(([value, item]) => ({
  value,
  label: item.label,
}));

const queryFields = [
  { prop: "keywords", label: "关键字", type: "input", placeholder: "导航名称/摘要" },
];

const columns = [
  { prop: "name", label: "名称", minWidth: 140 },
  { prop: "icon", label: "图标", type: "icon", width: 140 },
  { prop: "summary", label: "摘要", minWidth: 180 },
  { prop: "orderNum", label: "排序", width: 90, align: "center" as const },
  { prop: "path", label: "路径", minWidth: 140 },
  { prop: "component", label: "组件", minWidth: 180 },
  {
    prop: "type",
    label: "类型",
    type: "tag",
    width: 90,
    align: "center" as const,
    tagMap: NAVBAR_TYPE_TAG_MAP,
  },
  {
    prop: "visible",
    label: "显示",
    type: "tag",
    width: 90,
    align: "center" as const,
    tagMap: VISIBLE_TAG_MAP,
  },
];

const treeConfig = {
  enabled: true,
  parentKey: "parentId",
  childrenKey: "children",
  defaultExpandAll: true,
};

const formFields = [
  {
    prop: "parentId",
    label: "上级菜单",
    type: "treeSelect",
    treeRootLabel: "顶级（无父级）",
    treeRootValue: 0,
    required: true,
  },
  { prop: "name", label: "名称", type: "input", required: true },
  { prop: "summary", label: "摘要", type: "input" },
  { prop: "icon", label: "图标", type: "icon" },
  { prop: "path", label: "路径", type: "input", required: true },
  { prop: "component", label: "组件", type: "input", span: 24 },
  {
    prop: "orderNum",
    label: "排序",
    type: "number",
    min: 0,
    max: 9999,
    placeholder: "数字越小越靠前",
  },
  { prop: "type", label: "类型", type: "select", options: NAVBAR_TYPE_OPTIONS },
  { prop: "isFrame", label: "外链", type: "select", options: NAVBAR_FRAME_OPTIONS },
  { prop: "visible", label: "可见", type: "select", options: visibleOptions },
];

const initialForm = {
  parentId: 0,
  orderNum: 0,
  type: "C",
  isFrame: "1",
  visible: "0",
};
</script>
