import type { PageResult } from '@/shared/types/api';

export interface ArticleSummary {
  id: number;
  userId?: number;
  author?: string;
  categoryId?: number;
  categoryName?: string;
  tagIds?: string;
  tagNames?: string;
  title: string;
  avatar?: string;
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
  createTime?: string;
  updateTime?: string;
  user?: {
    id?: number;
    username?: string;
    nickname?: string;
    avatar?: string;
    articleCount?: number;
    followerCount?: number;
  };
}

export interface ArticleDetail extends ArticleSummary {
  avatarFileId?: number;
  tags?: TagItem[];
  readFull?: boolean;
  needLogin?: boolean;
  unlockHint?: string;
  isCollected?: boolean;
  collectId?: number;
  isFollowed?: boolean;
  followId?: number;
  isLike?: boolean;
}

export interface CategoryItem {
  id: number;
  name: string;
  avatar?: string;
  description?: string;
  clickCount?: number;
}

export interface TagItem {
  id: number;
  name: string;
  clickCount?: number;
}

export interface FrontSiteInfo {
  title?: string;
  webName?: string;
  siteName?: string;
  author?: string;
  authorInfo?: string;
  bio?: string;
  avatar?: string;
  logo?: string;
  notices?: string[];
}

export interface FrontHome {
  siteInfo?: FrontSiteInfo;
  latest: PageResult<ArticleSummary>;
  recommend: ArticleSummary[];
  carousel: ArticleSummary[];
  categories: CategoryItem[];
  tags: TagItem[];
}

export interface ArticlePageQuery {
  page?: number;
  pageSize?: number;
  categoryId?: number;
  orderBy?: 'view_count' | 'create_time';
}

export interface HomePageQuery {
  page?: number;
  pageSize?: number;
  categoryId?: number;
}
