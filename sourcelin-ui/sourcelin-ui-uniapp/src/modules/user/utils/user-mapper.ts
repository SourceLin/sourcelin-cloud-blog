import type { UserInfo } from '@/stores/user';
import type { FrontUserInfo } from '../types/user';

export function mapFrontUserInfo(user: FrontUserInfo): UserInfo {
  return {
    id: user.id,
    username: user.userName || '',
    nickname: user.nickName || user.userName || '',
    avatar: user.avatar || '',
    email: user.email || '',
    phone: user.phonenumber || '',
    introduction: user.introduction || '',
    articleCount: user.articleCount || 0,
    followerCount: user.followerCount || 0,
    roles: user.roles || [],
    permissions: user.permissions || []
  };
}
