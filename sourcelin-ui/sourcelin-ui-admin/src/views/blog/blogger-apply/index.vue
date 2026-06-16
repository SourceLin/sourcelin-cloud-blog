<template>
  <ModuleListShell
    title="博主申请"
    description="管理与审核前台普通用户申请成为博主的记录。"
    :module-api="adaptedApi"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      edit: 'blog:blogger:audit',
    }"
    :enable-create="false"
    :enable-delete="false"
    edit-text="审核"
    :deserialize-form="deserializeForm"
  />
</template>

<script setup lang="ts">
import { computed } from "vue";
import BloggerApplyAPI from "@/api/blog/bloggerApply";
import ModuleListShell from "../shared/ModuleListShell.vue";
import type { FormField, QueryField, TableColumn } from "../shared/types/module-list-shell";

defineOptions({
  name: "BloggerApply",
  inheritAttrs: false,
});

const deserializeForm = (row: any) => ({
  ...row,
  status: row.status === 0 ? 1 : row.status,
});

const adaptedApi = {
  getPage: (params: any) => BloggerApplyAPI.listApply(params),
  update: (id: any, data: any) =>
    BloggerApplyAPI.auditApply({
      id: Number(id),
      status: data.status,
      auditOpinion: data.auditOpinion,
    }),
};

const statusOptions = [
  { label: "待审核", value: 0 },
  { label: "已通过", value: 1 },
  { label: "已拒绝", value: 2 },
];

const statusTagMap = {
  0: { label: "待审核", type: "warning" },
  1: { label: "已通过", type: "success" },
  2: { label: "已拒绝", type: "danger" },
};

const queryFields: QueryField[] = [
  { prop: "username", label: "用户账号", type: "input", placeholder: "请输入用户账号" },
  { prop: "status", label: "审核状态", type: "select", options: statusOptions, width: "160px" },
];

const columns: TableColumn[] = [
  { prop: "id", label: "ID", width: 90 },
  { prop: "username", label: "用户账号", minWidth: 120 },
  { prop: "nickname", label: "用户昵称", minWidth: 120 },
  { prop: "contact", label: "联系方式", minWidth: 130 },
  { prop: "reason", label: "申请理由", minWidth: 200 },
  { prop: "status", label: "状态", type: "tag", minWidth: 110, tagMap: statusTagMap },
  { prop: "auditOpinion", label: "审批意见", minWidth: 160 },
  { prop: "auditBy", label: "审批人", minWidth: 110 },
  { prop: "auditTime", label: "审批时间", minWidth: 170 },
  { prop: "createTime", label: "申请时间", minWidth: 170 },
];

const formFields: FormField[] = [
  { prop: "id", label: "申请ID", type: "input", disabled: true },
  { prop: "username", label: "用户账号", type: "input", disabled: true },
  { prop: "nickname", label: "用户昵称", type: "input", disabled: true },
  { prop: "contact", label: "联系方式", type: "input", disabled: true },
  { prop: "reason", label: "申请理由", type: "textarea", disabled: true, span: 24, rows: 3 },
  {
    prop: "status",
    label: "审核结果",
    type: "select",
    required: true,
    options: [
      { label: "通过", value: 1 },
      { label: "拒绝", value: 2 },
    ],
  },
  { prop: "auditOpinion", label: "审批意见", type: "textarea", span: 24, rows: 3 },
];

const initialForm = {
  status: undefined,
  auditOpinion: "",
};
</script>
