export interface SayItem {
  id: number;
  userId?: number;
  userNickname?: string;
  content: string;
  images?: string;
  imageFileIds?: string;
  likeCount?: number;
  commentCount?: number;
  collectCount?: number;
  createTime?: string;
  likedByMe?: boolean;
  isCollected?: boolean;
  collectId?: number;
  user?: {
    id?: number;
    nickname?: string;
    avatar?: string;
    avatarFileId?: number;
  };
}

export interface TreeholeItem {
  id: number;
  userId?: number;
  userNickname?: string;
  nickname?: string;
  avatar?: string;
  content: string;
  images?: string;
  likeCount?: number;
  commentCount?: number;
  collectCount?: number;
  createTime?: string;
  likedByMe?: boolean;
  collectedByMe?: boolean;
}
