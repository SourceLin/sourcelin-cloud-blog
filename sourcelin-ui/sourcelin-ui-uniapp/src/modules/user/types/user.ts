export interface FrontUserInfo {
  id: number;
  userName?: string;
  nickName?: string;
  email?: string;
  phonenumber?: string;
  sex?: number;
  introduction?: string;
  avatar?: string;
  avatarFileId?: number | string;
  articleCount?: number;
  followerCount?: number;
  roles?: string[];
  permissions?: string[];
}
