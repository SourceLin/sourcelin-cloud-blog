export type CommentSource = 'article' | 'say' | 'treehole';

export interface CommentItem {
  id: number;
  userId?: number;
  userNickname?: string;
  nickname?: string;
  avatar?: string;
  articleId: number;
  parentCommentId?: number;
  parentUserId?: number;
  floorCommentId?: number;
  parentNickname?: string;
  content: string;
  source?: CommentSource;
  likeCount?: number;
  status?: number;
  createTime?: string;
}

export interface CommentCreatePayload {
  articleId: number;
  content: string;
  source: CommentSource;
  parentCommentId?: number;
  parentUserId?: number;
  floorCommentId?: number;
}
