export const YES_NO_OPTIONS = [
  { label: "是", value: 1 },
  { label: "否", value: 0 },
];

export const ENABLE_DISABLE_OPTIONS = [
  { label: "启用", value: 0 },
  { label: "停用", value: 1 },
];

export const ARTICLE_STATUS_OPTIONS = [
  { label: "审核中", value: 1 },
  { label: "已发布", value: 2 },
  { label: "草稿", value: 3 },
];

export const COMMENT_STATUS_OPTIONS = [
  { label: "待审核", value: 0 },
  { label: "通过", value: 1 },
  { label: "拒绝", value: 2 },
];

export const COMMENT_SOURCE_OPTIONS = [
  { label: "博客文章", value: "article" },
  { label: "说说", value: "say" },
  { label: "树洞", value: "treehole" },
];

export const LINK_STATUS_OPTIONS = [
  { label: "待审核", value: 0 },
  { label: "已上架", value: 1 },
  { label: "已下架", value: 2 },
];

export const SAY_TREEHOLE_STATUS_OPTIONS = [
  { label: "正常", value: 0 },
  { label: "置顶", value: 1 },
];

export const ARTICLE_STATUS_TAG_MAP = {
  1: { label: "审核中", type: "warning" },
  2: { label: "已发布", type: "success" },
  3: { label: "草稿", type: "info" },
};

export const COMMENT_STATUS_TAG_MAP = {
  0: { label: "待审核", type: "warning" },
  1: { label: "通过", type: "success" },
  2: { label: "拒绝", type: "danger" },
};

export const COMMENT_SOURCE_TAG_MAP = {
  article: { label: "博客文章", type: "info" },
  say: { label: "说说", type: "success" },
  treehole: { label: "树洞", type: "warning" },
};

export const LINK_STATUS_TAG_MAP = {
  0: { label: "待审核", type: "warning" },
  1: { label: "已上架", type: "success" },
  2: { label: "已下架", type: "info" },
};

export const ENABLE_DISABLE_TAG_MAP = {
  0: { label: "启用", type: "success" },
  1: { label: "停用", type: "info" },
};

export const VISIBLE_TAG_MAP = {
  0: { label: "显示", type: "success" },
  1: { label: "隐藏", type: "info" },
};

export const YES_NO_TAG_MAP = {
  0: { label: "否", type: "info" },
  1: { label: "是", type: "success" },
};

export const READ_AUTH_OPTIONS = [
  { label: "公开", value: 1 },
  { label: "登录可读", value: 2 },
  { label: "评论后可读", value: 3 },
  { label: "认证可读", value: 4 },
  { label: "VIP可读", value: 5 },
  { label: "付费可读", value: 6 },
  { label: "点赞后可读", value: 7 },
  { label: "收藏后可读", value: 8 },
  { label: "关注后可读", value: 9 },
];
