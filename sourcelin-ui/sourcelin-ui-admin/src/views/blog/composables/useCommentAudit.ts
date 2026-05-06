import { computed, unref, type MaybeRef } from "vue";
import CommentAPI from "@/api/blog/comment";
import { COMMENT_STATUS_OPTIONS, COMMENT_STATUS_TAG_MAP } from "../shared/constants";

type SelectOption = {
  label: string;
  value: string | number;
};

type TagMap = Record<string, { label: string; type?: string }>;

export function useCommentAudit(
  commentSourceOptions: MaybeRef<SelectOption[]> = [],
  commentSourceTagMap: MaybeRef<TagMap> = {}
) {
  const queryFields = computed(() => [
    { prop: "keywords", label: "关键字", type: "input", placeholder: "评论内容" },
    { prop: "status", label: "审核状态", type: "select", options: COMMENT_STATUS_OPTIONS },
    { prop: "source", label: "评论来源", type: "select", options: unref(commentSourceOptions) },
  ]);

  const columns = computed(() => [
    { prop: "content", label: "评论内容", minWidth: 260 },
    { prop: "ipSource", label: "IP来源", minWidth: 120 },
    {
      prop: "source",
      label: "来源",
      type: "tag",
      width: 100,
      align: "center" as const,
      tagMap: unref(commentSourceTagMap),
    },
    {
      prop: "status",
      label: "审核状态",
      type: "tag",
      width: 100,
      align: "center" as const,
      tagMap: COMMENT_STATUS_TAG_MAP,
    },
    { prop: "likeCount", label: "点赞", width: 80, align: "center" as const },
    { prop: "createTime", label: "创建时间", minWidth: 170 },
  ]);

  const formFields = computed(() => [
    { prop: "content", label: "评论内容", type: "textarea", required: true, span: 24, rows: 5 },
    {
      prop: "status",
      label: "审核状态",
      type: "select",
      options: COMMENT_STATUS_OPTIONS,
      required: true,
    },
    { prop: "source", label: "来源", type: "select", options: unref(commentSourceOptions) },
    { prop: "ipAddress", label: "IP地址", type: "input" },
  ]);

  const initialForm = {
    status: 0,
  };

  async function updateStatus(row: Record<string, any>, status: number) {
    // 仅传 id + status，遵循最小更新原则
    await CommentAPI.update(row.id, { id: row.id, status });
    ElMessage.success("评论状态更新成功");
  }

  const rowActions = [
    {
      key: "approve",
      label: "通过",
      type: "success" as const,
      permission: "blog:comment:edit",
      confirmText: "确认将评论审核为通过？",
      onClick: (row: Record<string, any>) => updateStatus(row, 1),
    },
    {
      key: "reject",
      label: "拒绝",
      type: "warning" as const,
      permission: "blog:comment:edit",
      confirmText: "确认将评论审核为拒绝？",
      onClick: (row: Record<string, any>) => updateStatus(row, 2),
    },
  ];

  return {
    queryFields,
    columns,
    formFields,
    initialForm,
    rowActions,
  };
}
