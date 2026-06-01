export type InteractionTargetType = 'article' | 'say' | 'treehole';

export interface CollectTargetSummary {
  id?: number;
  title?: string;
  content?: string;
  summary?: string;
  coverImage?: string;
  userNickname?: string;
  authorName?: string;
  categoryName?: string;
  createTime?: string;
}

export interface CollectItem<T = CollectTargetSummary> {
  id: number;
  userId?: number;
  targetType: InteractionTargetType;
  targetId: number;
  deleted?: number;
  createTime?: string;
  article?: T;
  say?: T;
  treehole?: T;
}

export interface FollowUser {
  id: number;
  username?: string;
  nickname?: string;
  avatar?: string;
  introduction?: string;
  articleCount?: number;
  followerCount?: number;
}

export interface FollowItem {
  id: number;
  userId?: number;
  followUserId?: number;
  targetUserId?: number;
  deleted?: number;
  createTime?: string;
  targetUser?: FollowUser;
  user?: FollowUser;
}

export interface FollowAction {
  id: number;
  followed: boolean;
}

/** 与收藏列表结构一致，用于「我的点赞」 */
export type LikeItem<T = CollectTargetSummary> = CollectItem<T>;

export type MyCommentSource = InteractionTargetType | 'all';

export interface MyCommentItem {
  id: number;
  userId?: number;
  content: string;
  status?: number;
  source?: string;
  targetType?: InteractionTargetType;
  targetId?: number;
  targetTitle?: string;
  parentCommentId?: number;
  parentNickname?: string;
  createTime?: string;
}

export type InteractionTab = 'collect' | 'like' | 'comment';
