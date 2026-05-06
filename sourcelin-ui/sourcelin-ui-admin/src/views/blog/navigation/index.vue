<template>
  <ModuleListShell
    title="导航目录"
    description="导航站点条目管理，包含推荐、状态与排序。"
    :module-api="NavigationAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      add: 'blog:navigation:add',
      edit: 'blog:navigation:edit',
      remove: 'blog:navigation:remove',
    }"
  />
</template>

<script setup lang="ts">
import NavigationAPI from "@/api/blog/navigation";
import ModuleListShell from "../shared/ModuleListShell.vue";
import {
  ENABLE_DISABLE_OPTIONS,
  ENABLE_DISABLE_TAG_MAP,
  YES_NO_OPTIONS,
  YES_NO_TAG_MAP,
} from "../shared/constants";

defineOptions({
  name: "BlogNavigation",
  inheritAttrs: false,
});

const queryFields = [
  { prop: "keywords", label: "关键字", type: "input", placeholder: "名称/描述" },
  { prop: "status", label: "状态", type: "select", options: ENABLE_DISABLE_OPTIONS },
];

const columns = [
  { prop: "name", label: "名称", minWidth: 140 },
  { prop: "icon", label: "图标", type: "icon", width: 140 },
  { prop: "url", label: "地址", minWidth: 220 },
  { prop: "category", label: "分类", minWidth: 120 },
  { prop: "source", label: "来源", width: 90, align: "center" as const },
  {
    prop: "isRecommend",
    label: "推荐",
    type: "tag",
    width: 80,
    align: "center" as const,
    tagMap: YES_NO_TAG_MAP,
  },
  {
    prop: "status",
    label: "状态",
    type: "tag",
    width: 80,
    align: "center" as const,
    tagMap: ENABLE_DISABLE_TAG_MAP,
  },
  { prop: "orderNum", label: "排序", width: 80, align: "center" as const },
  { prop: "clickCount", label: "点击", width: 80, align: "center" as const },
];

const formFields = [
  { prop: "name", label: "名称", type: "input", required: true },
  { prop: "url", label: "地址", type: "input", required: true, span: 24 },
  { prop: "category", label: "分类", type: "input" },
  { prop: "source", label: "来源", type: "input" },
  { prop: "icon", label: "图标", type: "icon" },
  { prop: "cover", label: "封面", type: "input", span: 24 },
  { prop: "isRecommend", label: "推荐", type: "select", options: YES_NO_OPTIONS },
  { prop: "status", label: "状态", type: "select", options: ENABLE_DISABLE_OPTIONS },
  { prop: "orderNum", label: "排序", type: "number", min: 0, max: 9999 },
  { prop: "description", label: "描述", type: "textarea", rows: 3, span: 24 },
];

const initialForm = {
  isRecommend: 0,
  status: 0,
  orderNum: 1,
};
</script>
