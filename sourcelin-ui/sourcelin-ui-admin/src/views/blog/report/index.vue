<template>
  <ModuleListShell
    title="内容举报"
    description="小程序和前台提交的内容举报处理闭环。"
    :module-api="ContentReportAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      edit: 'blog:report:edit',
      remove: 'blog:report:remove',
    }"
    :enable-create="false"
  />
</template>

<script setup lang="ts">
import ContentReportAPI from "@/api/blog/report";
import ModuleListShell from "../shared/ModuleListShell.vue";
import type { FormField, QueryField, TableColumn } from "../shared/types/module-list-shell";

defineOptions({
  name: "BlogReport",
  inheritAttrs: false,
});

const statusOptions = [
  { label: "待处理", value: "PENDING" },
  { label: "处理中", value: "PROCESSING" },
  { label: "已处理", value: "RESOLVED" },
  { label: "已驳回", value: "REJECTED" },
];

const statusTagMap = {
  PENDING: { label: "待处理", type: "warning" },
  PROCESSING: { label: "处理中", type: "primary" },
  RESOLVED: { label: "已处理", type: "success" },
  REJECTED: { label: "已驳回", type: "info" },
};

const queryFields: QueryField[] = [
  { prop: "status", label: "处理状态", type: "select", options: statusOptions, width: "160px" },
  { prop: "targetType", label: "目标类型", type: "input", placeholder: "article / say / treehole" },
  { prop: "targetId", label: "目标ID", type: "input", placeholder: "请输入目标ID" },
  { prop: "userId", label: "举报用户", type: "input", placeholder: "请输入用户ID" },
];

const columns: TableColumn[] = [
  { prop: "id", label: "ID", width: 90 },
  { prop: "status", label: "状态", type: "tag", minWidth: 110, tagMap: statusTagMap },
  { prop: "targetType", label: "目标类型", minWidth: 110 },
  { prop: "targetId", label: "目标ID", minWidth: 110 },
  { prop: "userId", label: "举报用户", minWidth: 110 },
  { prop: "reason", label: "举报原因", minWidth: 160 },
  { prop: "pagePath", label: "来源页面", minWidth: 180 },
  { prop: "clientIp", label: "客户端IP", minWidth: 140 },
  { prop: "createTime", label: "提交时间", minWidth: 170 },
];

const formFields: FormField[] = [
  { prop: "id", label: "举报ID", type: "input", disabled: true },
  { prop: "userId", label: "举报用户", type: "input", disabled: true },
  { prop: "targetType", label: "目标类型", type: "input", disabled: true },
  { prop: "targetId", label: "目标ID", type: "input", disabled: true },
  { prop: "reason", label: "举报原因", type: "input", disabled: true, span: 24 },
  { prop: "detail", label: "补充说明", type: "textarea", rows: 4, disabled: true, span: 24 },
  { prop: "pagePath", label: "来源页面", type: "input", disabled: true, span: 24 },
  { prop: "clientIp", label: "客户端IP", type: "input", disabled: true },
  { prop: "userAgent", label: "用户代理", type: "textarea", rows: 3, disabled: true, span: 24 },
  { prop: "status", label: "处理状态", type: "select", required: true, options: statusOptions },
  { prop: "remark", label: "处理备注", type: "textarea", rows: 3, span: 24 },
];

const initialForm = {
  status: "PENDING",
};
</script>
