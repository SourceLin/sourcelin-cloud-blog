import type { ArticleItem, CategoryItem, TagItem } from "@/types/api";
import ArticleAPI from "@/api/blog/article";
import CategoryAPI from "@/api/blog/category";
import TagAPI from "@/api/blog/tag";
import {
  ARTICLE_STATUS_OPTIONS,
  ARTICLE_STATUS_TAG_MAP,
  YES_NO_OPTIONS,
  YES_NO_TAG_MAP,
  READ_AUTH_OPTIONS,
} from "../shared/constants";

type SelectOption = { label: string; value: string | number };

export function useArticleTable() {
  const categoryOptions = ref<SelectOption[]>([]);
  const tagOptions = ref<SelectOption[]>([]);

  const queryFields = computed(() => [
    { prop: "keywords", label: "关键字", type: "input", placeholder: "标题/摘要" },
    { prop: "categoryId", label: "分类", type: "select", options: categoryOptions.value },
    { prop: "status", label: "状态", type: "select", options: ARTICLE_STATUS_OPTIONS },
  ]);

  const columns = [
    { prop: "title", label: "标题", minWidth: 220 },
    {
      prop: "status",
      label: "状态",
      type: "tag",
      width: 90,
      align: "center" as const,
      tagMap: ARTICLE_STATUS_TAG_MAP,
    },
    {
      prop: "isTop",
      label: "置顶",
      type: "tag",
      width: 80,
      align: "center" as const,
      tagMap: YES_NO_TAG_MAP,
    },
    {
      prop: "isRecommend",
      label: "推荐",
      type: "tag",
      width: 80,
      align: "center" as const,
      tagMap: YES_NO_TAG_MAP,
    },
    { prop: "viewCount", label: "阅读量", width: 90, align: "center" as const },
    { prop: "createTime", label: "创建时间", minWidth: 170 },
  ];

  const formFields = computed(() => [
    { prop: "title", label: "标题", type: "input", required: true, span: 24 },
    {
      prop: "categoryId",
      label: "分类",
      type: "select",
      options: categoryOptions.value,
      required: true,
    },
    {
      prop: "tagIds",
      label: "标签",
      type: "select",
      options: tagOptions.value,
      multiple: true,
      span: 12,
    },
    {
      prop: "status",
      label: "状态",
      type: "select",
      options: ARTICLE_STATUS_OPTIONS,
      required: true,
    },
    { prop: "avatar", label: "封面", type: "image", span: 24 },
    { prop: "summary", label: "摘要", type: "textarea", span: 24, rows: 3 },
    { prop: "content", label: "正文", type: "editor", span: 24, required: true },
    { prop: "readAuth", label: "阅读权限", type: "select", options: READ_AUTH_OPTIONS },
    { prop: "isComment", label: "允许评论", type: "select", options: YES_NO_OPTIONS },
    { prop: "isRecommend", label: "推荐", type: "select", options: YES_NO_OPTIONS },
    { prop: "isTop", label: "置顶", type: "select", options: YES_NO_OPTIONS },
    { prop: "isOriginal", label: "原创", type: "select", options: YES_NO_OPTIONS },
    { prop: "originalUrl", label: "原文链接", type: "input", span: 24 },
  ]);

  const initialForm = {
    status: 3,
    readAuth: 1,
    isComment: 1,
    isRecommend: 0,
    isTop: 0,
    isOriginal: 1,
    tagIds: [] as Array<string | number>,
  };

  function deserializeForm(row: Record<string, any>) {
    const current = row as ArticleItem;
    const ids = current.tagId
      ? String(current.tagId)
          .split(",")
          .map((item) => item.trim())
          .filter(Boolean)
      : [];
    return {
      ...current,
      tagIds: ids,
    };
  }

  function serializeForm(form: Record<string, any>) {
    const payload = { ...form };
    payload.tagId = Array.isArray(payload.tagIds) ? payload.tagIds.join(",") : "";
    delete payload.tagIds;
    return payload;
  }

  async function updateStatus(row: Record<string, any>, status: number) {
    if (Number(row.status) === status) {
      ElMessage.info("文章状态未变化");
      return;
    }
    // 仅传 id + status，避免传整行数据与 ArticleInsertDTO 字段不匹配导致 500
    await ArticleAPI.update(row.id, { id: row.id, status });
    ElMessage.success("文章审核状态更新成功");
  }

  const rowActions = [
    {
      key: "approve",
      label: "审核通过",
      type: "success" as const,
      permission: "blog:article:edit",
      confirmText: "确认审核通过并发布该文章吗？",
      onClick: (row: Record<string, any>) => updateStatus(row, 2),
    },
    {
      key: "reject",
      label: "驳回草稿",
      type: "warning" as const,
      permission: "blog:article:edit",
      confirmText: "确认将该文章驳回为草稿吗？",
      onClick: (row: Record<string, any>) => updateStatus(row, 3),
    },
  ];

  async function loadOptions() {
    const [categories, tags] = await Promise.all([CategoryAPI.getList(), TagAPI.getList()]);
    categoryOptions.value = (categories || []).map((item: CategoryItem) => ({
      label: item.name || "",
      value: item.id,
    }));
    tagOptions.value = (tags || []).map((item: TagItem) => ({
      label: item.name || "",
      value: item.id,
    }));
  }

  onMounted(() => {
    loadOptions();
  });

  return {
    queryFields,
    columns,
    formFields,
    initialForm,
    serializeForm,
    deserializeForm,
    rowActions,
  };
}
