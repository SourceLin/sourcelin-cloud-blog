import type { BaseQueryParams, PageResult } from "./common";

export interface BlogBaseEntity {
  id?: string | number;
  createTime?: string;
  updateTime?: string;
  [key: string]: any;
}

export interface BlogQueryParams extends Partial<BaseQueryParams> {
  page?: number;
  pageSize?: number;
  keywords?: string;
  status?: string | number;
}

export type BlogPageResult<T = BlogBaseEntity> = PageResult<T>;

export interface ArticleItem extends BlogBaseEntity {
  id: number;
  userId?: number;
  categoryId?: number;
  title?: string;
  avatar?: string;
  avatarFileId?: number;
  summary?: string;
  content?: string;
  readAuth?: number;
  status?: number;
  isComment?: number;
  isRecommend?: number;
  isTop?: number;
  isOriginal?: number;
  originalUrl?: string;
  viewCount?: number;
  likeCount?: number;
  collectCount?: number;
  commentCount?: number;
  deleted?: number;
  tagId?: string;
}

export interface CategoryItem extends BlogBaseEntity {
  id: number;
  name?: string;
  summary?: string;
  icon?: string;
  orderNum?: number;
  clickCount?: number;
  articleCount?: number;
}

export interface TagItem extends BlogBaseEntity {
  id: number;
  name?: string;
  summary?: string;
  icon?: string;
  orderNum?: number;
  clickCount?: number;
}

export interface CommentItem extends BlogBaseEntity {
  id: number;
  userId?: number;
  parentUserId?: number;
  articleId?: number;
  parentCommentId?: number;
  content?: string;
  commentInfo?: string;
  floorCommentId?: number;
  likeCount?: number;
  status?: number;
  source?: string;
  browser?: string;
  browserVersion?: string;
  system?: string;
  systemVersion?: string;
  ipAddress?: string;
  ipSource?: string;
  deleted?: number;
}

export interface ContentReportItem extends BlogBaseEntity {
  id: number;
  userId?: number;
  targetType?: string;
  targetId?: number;
  reason?: string;
  detail?: string;
  pagePath?: string;
  status?: "PENDING" | "PROCESSING" | "RESOLVED" | "REJECTED" | string;
  clientIp?: string;
  userAgent?: string;
  remark?: string;
}

export interface ModerationItem {
  version?: number;
  loadedVersion?: number;
  autoPassOnClean?: boolean;
  aiEnabled?: boolean;
  allow?: string[];
  block?: string[];
  suspect?: string[];
}

export interface SayItem extends BlogBaseEntity {
  id: number;
  userId?: number;
  userNickname?: string;
  content?: string;
  images?: string;
  imageFileIds?: string;
  likeCount?: number;
  collectCount?: number;
  commentCount?: number;
  status?: number;
  deleted?: number;
}

export interface TreeholeItem extends BlogBaseEntity {
  id: number;
  userId?: number;
  userNickname?: string;
  content?: string;
  images?: string;
  likeCount?: number;
  collectCount?: number;
  commentCount?: number;
  status?: number;
  deleted?: number;
  ipAddress?: string;
  browser?: string;
  system?: string;
}

export interface CollectItem extends BlogBaseEntity {
  id: number;
  userId?: number;
  targetType?: "article" | "say" | "treehole";
  targetId?: number;
  deleted?: number;
  article?: {
    id?: number;
    title?: string;
    avatar?: string;
    summary?: string;
    viewCount?: number;
    likeCount?: number;
    createTime?: string;
  };
  say?: {
    id?: number;
    content?: string;
    images?: string;
    imageFileIds?: string;
    likeCount?: number;
    commentCount?: number;
    createTime?: string;
  };
  treehole?: {
    id?: number;
    content?: string;
    likeCount?: number;
    collectCount?: number;
    commentCount?: number;
    createTime?: string;
  };
}

export type InteractionActionType = "LIKE" | "COLLECT";

export interface LikeRecordItem extends BlogBaseEntity {
  actionType?: "LIKE";
  userId?: number;
  targetType?: "article" | "say" | "treehole";
  targetId?: number;
  active?: boolean;
}

export interface CollectRecordItem extends BlogBaseEntity {
  actionType?: "COLLECT";
  userId?: number;
  targetType?: "article" | "say" | "treehole";
  targetId?: number;
  active?: boolean;
}

export interface InteractionRecordItem extends BlogBaseEntity {
  recordKey?: string;
  actionType?: InteractionActionType;
  userId?: number;
  targetType?: "article" | "say" | "treehole";
  targetId?: number;
  active?: boolean;
}

export interface InteractionOverview {
  likeTotal?: number;
  collectTotal?: number;
  todayLikeUsers?: number;
  todayCollectUsers?: number;
  todayActionUsers?: number;
}

export interface FollowItem extends BlogBaseEntity {
  id: number;
  userId?: number;
  followUserId?: number;
  deleted?: number;
  targetUserId?: number;
}

export interface LinkItem extends BlogBaseEntity {
  id: number;
  name?: string;
  avatar?: string;
  avatarFileId?: number;
  url?: string;
  description?: string;
  cover?: string;
  email?: string;
  orderNum?: number;
  label?: string;
  category?: number;
  status?: number;
  reason?: string;
}

export interface NavigationItem extends BlogBaseEntity {
  id: number;
  name?: string;
  url?: string;
  description?: string;
  icon?: string;
  cover?: string;
  category?: string;
  source?: string;
  isRecommend?: number;
  status?: number;
  orderNum?: number;
  clickCount?: number;
}

export interface NavbarItem extends BlogBaseEntity {
  id: number;
  name?: string;
  summary?: string;
  icon?: string;
  parentId?: number;
  orderNum?: number;
  path?: string;
  component?: string;
  isFrame?: string;
  type?: string;
  visible?: string;
}

export interface AnnouncementItem extends BlogBaseEntity {
  id: number;
  title?: string;
  content?: string;
  scopeType?: string;
  scopePayload?: string;
  publishStatus?: string;
  publishTime?: string;
  expireTime?: string;
  deleted?: number;
  isRead?: number;
}

export interface BlogUserItem extends BlogBaseEntity {
  id: number;
  username?: string;
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  sex?: number;
  introduction?: string;
  userType?: number;
  loginType?: string;
  userStatus?: number;
  praise?: number;
  follow?: number;
  loginIp?: string;
  loginDate?: string;
  remark?: string;
}

export interface WebConfigForm extends BlogBaseEntity {
  id?: number;
  logo?: string;
  touristAvatar?: string;
  name?: string;
  summary?: string;
  keyword?: string;
  recordNum?: string;
  webUrl?: string;
  notice?: string;
  aliPay?: string;
  weixinPay?: string;
  github?: string;
  gitee?: string;
  qqNumber?: string;
  qqGroup?: string;
  email?: string;
  wechat?: string;
  showList?: string;
  loginTypeList?: string;
  openComment?: string;
  openAdmiration?: string;
  author?: string;
  authorInfo?: string;
  authorAvatar?: string;
  aboutMe?: string;
}

export interface BlogStatsSummary {
  articleCount?: number;
  commentCount?: number;
  userCount?: number;
  viewCount?: number;
  articleReviewCount?: number;
  articlePublishedCount?: number;
  articleDraftCount?: number;
  articleOtherStatusCount?: number;
  commentPendingCount?: number;
  commentApprovedCount?: number;
  commentRejectedCount?: number;
  commentOtherStatusCount?: number;
  friendLinkPendingCount?: number;
  treeholeCount?: number;
  treeholeCommentCount?: number;
  treeholeNormalCount?: number;
  treeholePinnedCount?: number;
  treeholeOtherStatusCount?: number;
}

export interface BlogStatsTrend {
  dates?: string[];
  articleCounts?: number[];
  commentCounts?: number[];
  userCounts?: number[];
  treeholeCounts?: number[];
}

export interface KeywordSaveBody {
  version?: number;
  allow?: string[];
  block?: string[];
  suspect?: string[];
}

export interface MobileCapability {
  id?: number;
  client: "mini" | "h5" | "app" | string;
  reviewSafeMode: boolean;

  // 基础与阅读
  articleReadEnabled: boolean;
  searchEnabled: boolean;
  profileEnabled: boolean;
  favoriteEnabled: boolean;
  readingHistoryEnabled: boolean;

  // 设置与服务
  settingsEnabled: boolean;
  policyEnabled: boolean;
  aboutEnabled: boolean;
  friendLinkEnabled: boolean;
  navigationEnabled: boolean;

  // 创作
  articlePublishEnabled: boolean;
  articlePublishRole: string;
  userUploadEnabled: boolean;

  // 社交互动
  commentEnabled: boolean;
  communityEnabled: boolean;
  sayPublishEnabled: boolean;
  treeholeEnabled: boolean;
  interactionCenterEnabled: boolean;
  followEnabled: boolean;
  messageCenterEnabled: boolean;
  userHomeEnabled: boolean;

  createTime?: string;
  updateTime?: string;
}
