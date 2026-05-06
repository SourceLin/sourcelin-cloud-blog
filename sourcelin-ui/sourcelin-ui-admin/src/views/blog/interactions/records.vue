<template>
  <ModuleListShell
    title="互动行为记录"
    description="统一查询点赞与收藏行为记录。"
    :module-api="InteractionAPI"
    id-key="recordKey"
    :query-fields="queryFields"
    :columns="columns"
    :row-actions="rowActions"
    :enable-create="false"
    :enable-edit="false"
    :enable-delete="true"
    :permissions="{ remove: 'blog:interaction:remove' }"
  />
</template>

<script setup lang="ts">
import { ElMessageBox } from "element-plus";
import InteractionAPI from "@/api/blog/interaction";
import ModuleListShell from "../shared/ModuleListShell.vue";

defineOptions({
  name: "BlogInteractionRecords",
  inheritAttrs: false,
});

const queryFields = [
  {
    prop: "actionType",
    label: "行为类型",
    type: "select",
    options: [
      { label: "点赞", value: "LIKE" },
      { label: "收藏", value: "COLLECT" },
    ],
  },
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
  {
    prop: "state",
    label: "状态",
    type: "select",
    options: [
      { label: "全部", value: "all" },
      { label: "有效", value: "active" },
      { label: "已取消", value: "inactive" },
    ],
  },
];

const ACTION_TYPE_TAG_MAP = {
  LIKE: { label: "点赞", type: "success" },
  COLLECT: { label: "收藏", type: "warning" },
};

const TARGET_TYPE_TAG_MAP = {
  article: { label: "文章", type: "primary" },
  say: { label: "说说", type: "success" },
  treehole: { label: "树洞", type: "warning" },
};

const STATE_TAG_MAP = {
  true: { label: "有效", type: "success" },
  false: { label: "已取消", type: "info" },
};

const columns = [
  {
    prop: "actionType",
    label: "行为",
    type: "tag",
    width: 100,
    align: "center" as const,
    tagMap: ACTION_TYPE_TAG_MAP,
  },
  { prop: "userId", label: "用户ID", width: 100, align: "center" as const },
  {
    prop: "targetType",
    label: "目标类型",
    type: "tag",
    width: 100,
    align: "center" as const,
    tagMap: TARGET_TYPE_TAG_MAP,
  },
  { prop: "targetId", label: "目标ID", width: 110, align: "center" as const },
  {
    prop: "active",
    label: "状态",
    type: "tag",
    width: 100,
    align: "center" as const,
    tagMap: STATE_TAG_MAP,
  },
  { prop: "createTime", label: "创建时间", minWidth: 170 },
  { prop: "updateTime", label: "更新时间", minWidth: 170 },
];

const rowActions = [
  {
    key: "detail",
    label: "详情",
    permission: "blog:interaction:query",
    onClick: async (row: Record<string, any>) => {
      const actionType = row.actionType === "COLLECT" ? "COLLECT" : "LIKE";
      const id = Number(row.id || 0);
      if (!id) {
        ElMessageBox.alert("当前记录缺少主键ID，无法查看详情。", "提示", {
          confirmButtonText: "知道了",
        });
        return;
      }
      const detail = await InteractionAPI.getActionDetail(actionType, id);
      const text = [
        `记录ID：${detail.id ?? "-"}`,
        `行为类型：${detail.actionType === "COLLECT" ? "收藏" : "点赞"}`,
        `用户ID：${detail.userId ?? "-"}`,
        `目标类型：${formatTargetType(detail.targetType)}`,
        `目标ID：${detail.targetId ?? "-"}`,
        `状态：${detail.active ? "有效" : "已取消"}`,
        `创建时间：${detail.createTime ?? "-"}`,
        `更新时间：${detail.updateTime ?? "-"}`,
      ].join("\n");
      await ElMessageBox.alert(text, "互动记录详情", {
        confirmButtonText: "关闭",
      });
    },
  },
];

function formatTargetType(targetType?: string) {
  if (targetType === "say") return "说说";
  if (targetType === "treehole") return "树洞";
  return "文章";
}
</script>
