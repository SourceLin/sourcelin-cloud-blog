<template>
  <ModuleListShell
    title="关注管理"
    description="用户关注关系查询与删除治理。"
    :module-api="FollowAPI"
    id-key="id"
    :query-fields="queryFields"
    :columns="columns"
    :enable-create="false"
    :enable-edit="false"
    :enable-delete="true"
    :permissions="{ remove: 'blog:follow:remove' }"
  />
</template>

<script setup lang="ts">
import FollowAPI from "@/api/blog/follow";
import ModuleListShell from "../shared/ModuleListShell.vue";

defineOptions({
  name: "BlogFollow",
  inheritAttrs: false,
});

const queryFields = [
  { prop: "userId", label: "用户ID", type: "input", placeholder: "请输入用户ID" },
  { prop: "followUserId", label: "关注用户ID", type: "input", placeholder: "请输入目标用户ID" },
];

const columns = [
  { prop: "id", label: "关系ID", width: 90, align: "center" as const },
  {
    prop: "userId",
    label: "用户",
    width: 120,
    formatter: (row: Record<string, any>) => row.user?.nickname || row.userId,
  },
  {
    prop: "followUserId",
    label: "关注用户",
    width: 120,
    formatter: (row: Record<string, any>) => row.targetUser?.nickname || row.followUserId,
  },
  { prop: "createTime", label: "创建时间", minWidth: 170 },
];
</script>
