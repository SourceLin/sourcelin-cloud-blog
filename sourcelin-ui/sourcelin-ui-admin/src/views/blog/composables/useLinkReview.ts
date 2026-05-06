import LinkAPI from "@/api/blog/link";
import { LINK_STATUS_OPTIONS, LINK_STATUS_TAG_MAP } from "../shared/constants";

export function useLinkReview() {
  const queryFields = [
    { prop: "keywords", label: "关键字", type: "input", placeholder: "友链名称/描述" },
    { prop: "status", label: "状态", type: "select", options: LINK_STATUS_OPTIONS },
  ];

  const columns = [
    { prop: "avatar", label: "头像", type: "image", width: 90, align: "center" as const },
    { prop: "name", label: "友链名称", minWidth: 120 },
    { prop: "url", label: "链接地址", minWidth: 220 },
    {
      prop: "status",
      label: "状态",
      type: "tag",
      width: 90,
      align: "center" as const,
      tagMap: LINK_STATUS_TAG_MAP,
    },
    { prop: "orderNum", label: "排序", width: 80, align: "center" as const },
    { prop: "category", label: "分类", width: 80, align: "center" as const },
    { prop: "createTime", label: "创建时间", minWidth: 170 },
  ];

  const formFields = [
    { prop: "name", label: "名称", type: "input", required: true },
    { prop: "email", label: "邮箱", type: "input" },
    { prop: "url", label: "链接", type: "input", required: true, span: 24 },
    { prop: "avatar", label: "头像", type: "image", span: 24 },
    { prop: "cover", label: "封面", type: "input", span: 24 },
    { prop: "description", label: "描述", type: "textarea", rows: 3, span: 24 },
    { prop: "label", label: "标签", type: "input" },
    {
      prop: "category",
      label: "分类",
      type: "select",
      options: [
        { label: "推荐", value: 1 },
        { label: "精选", value: 2 },
        { label: "更多", value: 3 },
      ],
    },
    { prop: "orderNum", label: "排序", type: "number", min: 0, max: 9999 },
    { prop: "status", label: "状态", type: "select", options: LINK_STATUS_OPTIONS },
    { prop: "reason", label: "下架原因", type: "textarea", rows: 3, span: 24 },
  ];

  const initialForm = {
    status: 0,
    category: 0,
    orderNum: 1,
  };

  async function updateStatus(row: Record<string, any>, status: number, reason = "") {
    await LinkAPI.changeStatus({
      id: row.id,
      status,
      reason,
    });
    ElMessage.success("友链状态更新成功");
  }

  const rowActions = [
    {
      key: "approve",
      label: "审核通过",
      type: "success" as const,
      permission: "blog:link:examine",
      confirmText: "确认审核通过并上架该友链吗？",
      onClick: (row: Record<string, any>) => updateStatus(row, 1),
    },
    {
      key: "offline",
      label: "下架",
      type: "warning" as const,
      permission: "blog:link:examine",
      confirmText: "确认下架该友链吗？",
      onClick: (row: Record<string, any>) => updateStatus(row, 2, "后台手动下架"),
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
