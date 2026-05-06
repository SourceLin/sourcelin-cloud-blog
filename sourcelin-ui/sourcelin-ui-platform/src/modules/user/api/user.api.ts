import { requestData } from '@/shared/api/http'
import type { PageResult } from '@/shared/types/api'

export interface UserCenterStatsResponse {
  articleCount: number
  collectCount: number
  followCount: number
  fansCount: number
}

export interface UserArticlesQuery {
  page?: number
  pageSize?: number
  status?: number
}

export interface UserRelationQuery {
  page?: number
  pageSize?: number
  state?: 'all' | 'active' | 'inactive'
}

/** 前台用户详情 VO — 对齐后端 FrontUserInfoVO */
export interface FrontUserDetailVO {
  id?: number
  userName?: string
  nickName?: string
  email?: string
  phonenumber?: string
  sex?: string | number
  introduction?: string
  avatar?: string
  avatarFileId?: number | null
  articleCount?: number
  followerCount?: number
}

/** 前台用户资料 VO — 对齐后端 /front/user/profile 响应 */
export interface FrontUserProfileVO {
  nickName?: string
  phonenumber?: string
  email?: string
  sex?: string | number
  introduction?: string
}

/** 前台用户资料更新 DTO */
export interface FrontUserProfileUpdateDTO {
  nickName?: string
  phonenumber?: string
  email?: string
  sex?: string | number
  introduction?: string
}

/** 头像上传响应 VO */
export interface FrontAvatarUploadVO {
  url?: string
  fileId?: number
}

/** 用户文章列表项 VO — 对齐后端 /front/users/articles/{id} 响应 */
export interface FrontUserArticleVO {
  id?: number
  title?: string
  summary?: string
  avatar?: string
  createTime?: string
  viewCount?: number
  likeCount?: number
  commentCount?: number
  status?: number
  categoryId?: number
  categoryName?: string
}

/** 用户关注/粉丝关系列表项 VO */
export interface FrontUserFollowVO {
  id?: number
  userId?: number
  followUserId?: number
  targetUserId?: number
  createTime?: string
  user?: FrontUserDetailVO | null
  targetUser?: FrontUserDetailVO | null
}

export function getUserDetail(userId: number) {
  return requestData<FrontUserDetailVO>({
    url: `/front/users/${userId}`,
    method: 'get'
  })
}

export function getUserProfile() {
  return requestData<FrontUserProfileVO>({
    url: '/front/user/profile',
    method: 'get'
  })
}

export function getUserCenterStats() {
  return requestData<UserCenterStatsResponse>({
    url: '/front/user/stats',
    method: 'get'
  })
}

export function updateUserProfile(data: FrontUserProfileUpdateDTO) {
  return requestData<void>({
    url: '/front/user/profile',
    method: 'put',
    data
  })
}

export function updateUserPwd(oldPassword: string, newPassword: string) {
  return requestData<void>({
    url: '/front/user/updatePwd',
    method: 'put',
    params: { oldPassword, newPassword }
  })
}

export function uploadAvatar(data: FormData) {
  return requestData<FrontAvatarUploadVO>({
    url: '/front/user/avatar',
    method: 'post',
    data
  })
}

export function getUserArticles(userId: number, params: UserArticlesQuery) {
  return requestData<PageResult<FrontUserArticleVO>>({
    url: `/front/users/articles/${userId}`,
    method: 'get',
    params
  })
}

export function getUserFollows(userId: number, params: UserRelationQuery) {
  return requestData<PageResult<FrontUserFollowVO>>({
    url: `/front/users/followings/${userId}`,
    method: 'get',
    params
  })
}

export function getUserFans(userId: number, params: UserRelationQuery) {
  return requestData<PageResult<FrontUserFollowVO>>({
    url: `/front/users/followers/${userId}`,
    method: 'get',
    params
  })
}
