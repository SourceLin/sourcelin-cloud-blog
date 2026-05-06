<template>
  <ModuleListShell
    title="博客公告"
    description="管理博客消息中心的广播公告。"
    :module-api="AnnouncementAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :form-fields="formFields"
    :initial-form="initialForm"
    :permissions="{
      add: 'blog:announcement:add',
      edit: 'blog:announcement:edit',
      remove: 'blog:announcement:remove',
    }"
    :row-actions="rowActions"
    :serialize-form="serializeForm"
    :deserialize-form="deserializeForm"
  />
</template>

<script setup lang="ts">
import { ElMessage } from "element-plus";
import AnnouncementAPI from "@/api/blog/announcement";
import BlogUserAPI from "@/api/blog/user";
import type { BlogUserItem } from "@/types/api";
import { useDictStore } from "@/store";
import ModuleListShell from "../shared/ModuleListShell.vue";

defineOptions({
  name: "BlogAnnouncement",
  inheritAttrs: false,
});

const SCOPE_DICT = "blog_announcement_scope_type";
const STATUS_DICT = "blog_announcement_publish_status";

const dictStore = useDictStore();
const { loadDictItems, getDictItems } = dictStore;
const userOptions = ref<Array<{ label: string; value: string }>>([]);

const BLOG_USER_ROLE_OPTIONS = [
  { label: "普通用户", value: "1" },
  { label: "博主", value: "2" },
];

const scopeOptions = computed(() =>
  getDictItems(SCOPE_DICT).map((item) => ({
    label: item.label,
    value: item.value,
    tagType: item.tagType,
  }))
);

const publishStatusOptions = computed(() =>
  getDictItems(STATUS_DICT).map((item) => ({
    label: item.label,
    value: item.value,
    tagType: item.tagType,
  }))
);

const scopeTagMap = computed<Record<string, any>>(() =>
  Object.fromEntries(
    scopeOptions.value.map((item) => [
      item.value,
      { label: item.label, type: item.tagType ?? "info" },
    ])
  )
);

const publishStatusTagMap = computed<Record<string, any>>(() =>
  Object.fromEntries(
    publishStatusOptions.value.map((item) => [
      item.value,
      { label: item.label, type: item.tagType ?? "info" },
    ])
  )
);

const queryFields = computed(() => [
  { prop: "title", label: "标题", type: "input", placeholder: "请输入公告标题" },
  {
    prop: "scopeType",
    label: "作用域",
    type: "select",
    options: scopeOptions.value,
    width: "160px",
  },
  {
    prop: "publishStatus",
    label: "发布状态",
    type: "select",
    options: publishStatusOptions.value,
    width: "160px",
  },
]);

const columns = computed(() => [
  { prop: "title", label: "标题", minWidth: 220 },
  {
    prop: "scopeType",
    label: "作用域",
    type: "tag" as const,
    minWidth: 120,
    tagMap: scopeTagMap.value,
  },
  {
    prop: "publishStatus",
    label: "发布状态",
    type: "tag" as const,
    minWidth: 120,
    tagMap: publishStatusTagMap.value,
  },
  { prop: "publishTime", label: "发布时间", minWidth: 180 },
  { prop: "expireTime", label: "过期时间", minWidth: 180 },
]);

const formFields = computed(() => [
  { prop: "title", label: "标题", type: "input", required: true, span: 24 },
  {
    prop: "scopeType",
    label: "作用域",
    type: "select",
    required: true,
    options: scopeOptions.value,
  },
  {
    prop: "publishStatus",
    label: "发布状态",
    type: "select",
    required: true,
    options: publishStatusOptions.value,
  },
  { prop: "publishTime", label: "发布时间", type: "datetime", placeholder: "请选择发布时间" },
  { prop: "expireTime", label: "过期时间", type: "datetime", placeholder: "请选择过期时间" },
  {
    prop: "scopePayload",
    label: "分群参数",
    type: "textarea",
    rows: 3,
    span: 24,
    placeholder: "请输入分群 JSON 参数",
    visibleWhen: (form: Record<string, any>) => form.scopeType === "segment",
  },
  {
    prop: "scopeUserIds",
    label: "指定用户",
    type: "select",
    multiple: true,
    required: true,
    span: 24,
    options: userOptions.value,
    placeholder: "请选择接收公告的用户",
    visibleWhen: (form: Record<string, any>) => form.scopeType === "assign",
  },
  {
    prop: "scopeRoleTypes",
    label: "用户角色",
    type: "select",
    multiple: true,
    required: true,
    span: 24,
    options: BLOG_USER_ROLE_OPTIONS,
    placeholder: "请选择接收公告的用户角色",
    visibleWhen: (form: Record<string, any>) => form.scopeType === "role",
  },
  { prop: "content", label: "正文", type: "editor", required: true, span: 24 },
]);

const initialForm = {
  scopeType: "all",
  publishStatus: "0",
  scopeUserIds: [],
  scopeRoleTypes: [],
};

function parseScopePayload(payload?: string): string[] {
  if (!payload) return [];
  try {
    const parsed = JSON.parse(payload);
    return Array.isArray(parsed) ? parsed.map((item) => String(item)) : [];
  } catch {
    return [];
  }
}

function toNumericJson(values?: Array<string | number>): string {
  const ids = (values || []).map((item) => Number(item)).filter((item) => Number.isFinite(item));
  return JSON.stringify(ids);
}

function deserializeForm(row: Record<string, any>) {
  const scopeType = row.scopeType || "all";
  return {
    ...row,
    scopeUserIds: scopeType === "assign" ? parseScopePayload(row.scopePayload) : [],
    scopeRoleTypes: scopeType === "role" ? parseScopePayload(row.scopePayload) : [],
  };
}

function serializeForm(form: Record<string, any>) {
  const payload = { ...form };
  if (payload.scopeType === "all") {
    payload.scopePayload = null;
  } else if (payload.scopeType === "assign") {
    payload.scopePayload = toNumericJson(payload.scopeUserIds);
  } else if (payload.scopeType === "role") {
    payload.scopePayload = toNumericJson(payload.scopeRoleTypes);
  }
  delete payload.scopeUserIds;
  delete payload.scopeRoleTypes;
  return payload;
}

const rowActions = [
  {
    key: "publish",
    label: "发布",
    type: "primary",
    permission: "blog:announcement:publish",
    visible: (row: Record<string, any>) => row.publishStatus !== "1",
    onClick: async (row: Record<string, any>) => {
      await AnnouncementAPI.publish(row.id);
      ElMessage.success("发布成功");
    },
  },
  {
    key: "offline",
    label: "下线",
    type: "warning",
    permission: "blog:announcement:offline",
    visible: (row: Record<string, any>) => row.publishStatus === "1",
    onClick: async (row: Record<string, any>) => {
      await AnnouncementAPI.offline(row.id);
      ElMessage.success("下线成功");
    },
  },
];

onMounted(async () => {
  const [, , users] = await Promise.all([
    loadDictItems(SCOPE_DICT),
    loadDictItems(STATUS_DICT),
    BlogUserAPI.getPage({ page: 1, pageSize: 1000 }),
  ]);
  userOptions.value = (users.items || []).map((item: BlogUserItem) => ({
    label: item.nickname || item.username || String(item.id),
    value: String(item.id),
  }));
});
</script>
