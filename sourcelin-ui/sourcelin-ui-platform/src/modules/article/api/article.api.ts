import { requestData } from '@/shared/api/http'
import { uploadFile } from '@/shared/api/file.api'
import type { IdResult, PageResult } from '@/shared/types/api'
import type { CategoryOption } from '@/modules/article/api/category.api'
import type { TagOption } from '@/modules/article/api/tag.api'

export interface ArticleListItem {
  id: number
  title?: string
  summary?: string
  avatar?: string
  createTime?: string
  viewCount?: number
  commentCount?: number
  isTop?: number
  isRecommend?: number
  tagNames?: string
  author?: string
  categoryId?: number
  categoryName?: string
}

export interface ArticlePageQuery {
  page?: number
  pageSize?: number
  categoryId?: number
  isRecommend?: number
  isTop?: number
  orderBy?: string
}

/** 首页站点信息 VO — 对齐后端 /front/config/siteInfo 响应 */
export interface HomeSiteInfoVO {
  webName?: string
  siteName?: string
  name?: string
  logo?: string
  footer?: string
  description?: string
  summary?: string
  notice?: string
  backgroundImage?: string
  author?: string
  github?: string
  gitee?: string
  qqNumber?: string
  email?: string
  recordNum?: string | number
  showList?: number[]
  notices?: string[]
}

/** 文章创建/更新 DTO — 对齐后端 ArticleInsertDTO */
export interface ArticleCreateUpdateDTO {
  id?: number
  title?: string
  summary?: string
  content?: string
  categoryId?: number
  tagIds?: number[]
  isOriginal?: number
  originalUrl?: string
  readAuth?: number
  isComment?: number
  isRecommend?: number
  isTop?: number
  avatar?: string
  status?: number
}

export interface HomeData {
  siteInfo: HomeSiteInfoVO
  latest: PageResult<ArticleListItem>
  recommend: ArticleListItem[]
  carousel: ArticleListItem[]
  categories: CategoryOption[]
  tags: TagOption[]
}

export interface ArticleDetailUser {
  id?: number
  nickname?: string
  avatar?: string
  introduction?: string
  articleCount?: number
  followerCount?: number
}

export interface ArticleDetailTag {
  id?: number
  name?: string
  summary?: string
  icon?: string
  orderNum?: number
  clickCount?: number
}

export interface ArticleDetail {
  id?: number
  userId?: number
  categoryId?: number
  title?: string
  summary?: string
  content?: string
  avatar?: string
  createTime?: string
  viewCount?: number
  likeCount?: number
  collectCount?: number
  readAuth?: number
  status?: number
  isComment?: number
  isRecommend?: number
  isTop?: number
  isOriginal?: number
  originalUrl?: string
  user?: ArticleDetailUser
  tags?: ArticleDetailTag[]
  commentCount?: number
  readFull?: boolean
  needLogin?: boolean
  unlockHint?: string
  isCollected?: boolean
  collectId?: number
  isFollowed?: boolean
  followId?: number
  isLike?: boolean
}

export function getHome(params: ArticlePageQuery) {
  return requestData<HomeData>({
    url: '/front/home',
    method: 'get',
    params
  })
}

export function getArticles(params: ArticlePageQuery) {
  return requestData<PageResult<ArticleListItem>>({
    url: '/front/articles',
    method: 'get',
    params
  })
}

export function getArticleDetail(id: number) {
  return requestData<ArticleDetail>({
    url: `/front/articles/${id}`,
    method: 'get'
  })
}

export function getRecommendArticles(params: ArticlePageQuery) {
  return requestData<PageResult<ArticleListItem>>({
    url: '/front/articles',
    method: 'get',
    params: { ...params, isRecommend: 1 }
  })
}

export function getTopArticles(params: ArticlePageQuery) {
  return requestData<PageResult<ArticleListItem>>({
    url: '/front/articles',
    method: 'get',
    params: { ...params, isTop: 1 }
  })
}

export function getHotArticles(params: ArticlePageQuery) {
  return requestData<PageResult<ArticleListItem>>({
    url: '/front/articles',
    method: 'get',
    params: { ...params, orderBy: 'view_count' }
  })
}

export function getArchiveArticles(params: ArticlePageQuery) {
  return requestData<PageResult<ArticleListItem>>({
    url: '/front/articles',
    method: 'get',
    params
  })
}

export function viewArticle(articleId: number) {
  return requestData<void>({
    url: `/front/articles/view/${articleId}`,
    method: 'post'
  })
}

export function getUserArticles(params: ArticlePageQuery) {
  return requestData<PageResult<ArticleListItem>>({
    url: '/front/articles',
    method: 'get',
    params
  })
}

export function createArticle(data: ArticleCreateUpdateDTO) {
  return requestData<IdResult | number>({
    url: '/front/articles',
    method: 'post',
    data: JSON.stringify(data),
    headers: { 'Content-Type': 'application/json' }
  })
}

export function updateArticle(id: number, data: ArticleCreateUpdateDTO) {
  return requestData<void>({
    url: `/front/articles/${id}`,
    method: 'put',
    data: JSON.stringify(data),
    headers: { 'Content-Type': 'application/json' }
  })
}

export function deleteArticle(id: number) {
  return requestData<void>({
    url: `/front/articles/${id}`,
    method: 'delete'
  })
}

export function uploadArticleFile(data: FormData) {
  return uploadFile(data)
}
