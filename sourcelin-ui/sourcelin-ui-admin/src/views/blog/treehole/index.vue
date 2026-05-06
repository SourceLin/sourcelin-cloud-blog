<template>
  <ModuleListShell
    title="树洞管理"
    description="树洞内容、状态与客户端来源信息管理。"
    :module-api="TreeholeAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      add: 'blog:treehole:add',
      edit: 'blog:treehole:edit',
      remove: 'blog:treehole:remove',
    }"
  />
</template>

<script setup lang="ts">
import TreeholeAPI from "@/api/blog/treehole";
import ModuleListShell from "../shared/ModuleListShell.vue";
import { SAY_TREEHOLE_STATUS_OPTIONS } from "../shared/constants";

defineOptions({
  name: "BlogTreehole",
  inheritAttrs: false,
});

const statusTagMap = {
  0: { label: "正常", type: "success" },
  1: { label: "置顶", type: "warning" },
};

const queryFields = [
  { prop: "userNickname", label: "用户昵称", type: "input", placeholder: "请输入用户昵称" },
  { prop: "keywords", label: "内容关键字", type: "input", placeholder: "请输入内容关键字" },
  { prop: "status", label: "状态", type: "select", options: SAY_TREEHOLE_STATUS_OPTIONS },
];

const columns = [
  { prop: "userNickname", label: "用户", minWidth: 120 },
  { prop: "content", label: "内容", minWidth: 260 },
  { prop: "ipAddress", label: "IP", width: 120 },
  { prop: "browser", label: "浏览器", width: 120 },
  { prop: "system", label: "系统", width: 120 },
  {
    prop: "status",
    label: "状态",
    type: "tag",
    width: 90,
    align: "center" as const,
    tagMap: statusTagMap,
  },
  { prop: "createTime", label: "创建时间", minWidth: 170 },
];

const formFields = [
  { prop: "userId", label: "用户ID", type: "number", min: 1, hiddenOnEdit: true },
  { prop: "userNickname", label: "用户昵称", type: "input" },
  { prop: "status", label: "状态", type: "select", options: SAY_TREEHOLE_STATUS_OPTIONS },
  { prop: "content", label: "内容", type: "textarea", required: true, span: 24, rows: 6 },
];

const initialForm = {
  status: 0,
};
</script>
