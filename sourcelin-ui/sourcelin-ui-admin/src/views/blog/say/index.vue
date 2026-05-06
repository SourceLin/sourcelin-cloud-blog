<template>
  <ModuleListShell
    title="说说管理"
    description="说说内容、状态与互动数据治理。"
    :module-api="SayAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      add: 'blog:say:add',
      edit: 'blog:say:edit',
      remove: 'blog:say:remove',
    }"
    :serialize-form="serializeForm"
    :deserialize-form="deserializeForm"
  />
</template>

<script setup lang="ts">
import SayAPI from "@/api/blog/say";
import ModuleListShell from "../shared/ModuleListShell.vue";
import { SAY_TREEHOLE_STATUS_OPTIONS } from "../shared/constants";

defineOptions({
  name: "BlogSay",
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
  { prop: "likeCount", label: "点赞", width: 80, align: "center" as const },
  { prop: "commentCount", label: "评论", width: 80, align: "center" as const },
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
  { prop: "status", label: "状态", type: "select", options: SAY_TREEHOLE_STATUS_OPTIONS },
  { prop: "images", label: "图片", type: "image", multiple: true, span: 24 },
  { prop: "content", label: "内容", type: "textarea", required: true, span: 24, rows: 6 },
];

const initialForm = {
  status: 0,
  images: [] as string[],
};

function parseImageList(images?: string | string[]): string[] {
  if (Array.isArray(images)) return images.filter(Boolean);
  return String(images || "")
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

function deserializeForm(row: Record<string, any>) {
  return {
    ...row,
    images: parseImageList(row.images),
  };
}

function serializeForm(form: Record<string, any>) {
  const payload = { ...form };
  payload.images = Array.isArray(payload.images) ? payload.images.join(",") : payload.images || "";
  delete payload.userNickname;
  return payload;
}
</script>
