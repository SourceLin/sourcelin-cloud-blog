<template>
  <ModuleListShell
    title="收藏管理"
    description="用户收藏关系查询与删除治理。"
    :module-api="CollectAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :enable-create="false"
    :enable-edit="false"
    :enable-delete="true"
    :permissions="{ remove: 'blog:collect:remove' }"
  />
</template>

<script setup lang="ts">
import CollectAPI from "@/api/blog/collect";
import ModuleListShell from "../shared/ModuleListShell.vue";

defineOptions({
  name: "BlogCollect",
  inheritAttrs: false,
});

const queryFields = [
  { prop: "userId", label: "用户ID", type: "input", placeholder: "请输入用户ID" },
  {
    prop: "targetType",
    label: "目标类型",
    type: "select",
    options: [
      { label: "文章", value: "article" },
      { label: "说说", value: "say" },
      { label: "树洞", value: "treehole" },
    ],
  },
  { prop: "targetId", label: "目标ID", type: "input", placeholder: "请输入目标ID" },
];

const columns = [
  { prop: "id", label: "收藏ID", width: 90, align: "center" as const },
  {
    prop: "userId",
    label: "用户",
    width: 120,
    formatter: (row: Record<string, any>) => row.user?.nickname || row.userId,
  },
  {
    prop: "targetType",
    label: "目标类型",
    width: 100,
    align: "center" as const,
    formatter: (row: Record<string, any>) => {
      if (row.targetType === "say") return "说说";
      if (row.targetType === "treehole") return "树洞";
      return "文章";
    },
  },
  { prop: "targetId", label: "目标ID", width: 100, align: "center" as const },
  {
    prop: "targetTitle",
    label: "目标内容",
    minWidth: 220,
    formatter: (row: Record<string, any>) => {
      if (row.targetType === "say") return row.say?.content ?? "-";
      if (row.targetType === "treehole") return row.treehole?.content ?? "-";
      return row.article?.title ?? "-";
    },
  },
  { prop: "createTime", label: "创建时间", minWidth: 170 },
];
</script>
