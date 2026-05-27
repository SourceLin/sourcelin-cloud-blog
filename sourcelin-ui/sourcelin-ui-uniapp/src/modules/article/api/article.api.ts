import { http } from '@/utils/request';
import type { PageResult } from '@/shared/types/api';
import type {
  ArticleDetail,
  ArticleSummary,
  ArticlePageQuery,
  CategoryItem,
  FrontHome,
  HomePageQuery,
  TagItem
} from '../types/article';
import type { ListResult } from '@/shared/types/api';

export function fetchHome(query: HomePageQuery = {}): Promise<FrontHome> {
  return http.get<FrontHome>('/front/home', {
    page: query.page ?? 1,
    pageSize: query.pageSize ?? 10,
    categoryId: query.categoryId
  });
}

export function fetchArticlePage(query: ArticlePageQuery = {}): Promise<PageResult<ArticleSummary>> {
  return http.get<PageResult<ArticleSummary>>('/front/articles', {
    page: query.page ?? 1,
    pageSize: query.pageSize ?? 10,
    categoryId: query.categoryId,
    orderBy: query.orderBy
  });
}

export function fetchArticleDetail(id: number | string): Promise<ArticleDetail> {
  return http.get<ArticleDetail>(`/front/articles/${id}`);
}

export function createArticle(payload: {
  title: string;
  summary?: string;
  content: string;
  categoryId?: number;
  tagIds?: number[];
  avatar?: string;
  avatarFileId?: number;
  readAuth?: number;
  status?: number;
  isComment?: number;
  isOriginal?: number;
  originalUrl?: string;
}): Promise<number> {
  return http.post<number>('/front/articles', payload);
}

export function updateArticle(
  id: number | string,
  payload: {
    title: string;
    summary?: string;
    content: string;
    categoryId?: number;
    tagIds?: number[];
    avatar?: string;
    avatarFileId?: number;
    readAuth?: number;
    status?: number;
    isComment?: number;
    isOriginal?: number;
    originalUrl?: string;
  }
): Promise<void> {
  return http.put<void>(`/front/articles/${id}`, payload);
}

export function deleteArticle(id: number | string): Promise<void> {
  return http.delete<void>(`/front/articles/${id}`);
}

export function searchArticles(
  keyword: string,
  page = 1,
  pageSize = 10
): Promise<PageResult<ArticleSummary>> {
  return http.get<PageResult<ArticleSummary>>('/front/articles/search', {
    keyword,
    page,
    pageSize
  });
}

export function searchCategories(
  keyword: string,
  page = 1,
  pageSize = 6
): Promise<PageResult<CategoryItem>> {
  return http.get<PageResult<CategoryItem>>('/front/categories/search', {
    keyword,
    page,
    pageSize
  });
}

export function searchTags(
  keyword: string,
  page = 1,
  pageSize = 8
): Promise<PageResult<TagItem>> {
  return http.get<PageResult<TagItem>>('/front/tags/search', {
    keyword,
    page,
    pageSize
  });
}

export async function fetchTagList(): Promise<TagItem[]> {
  const result = await http.get<ListResult<TagItem>>('/front/tags');
  return result.items || [];
}

export async function fetchSearchHotKeywords(): Promise<string[]> {
  const result = await http.get<ListResult<string>>('/front/search/hot');
  return result.items || [];
}

export async function fetchSearchSuggestions(keyword: string): Promise<string[]> {
  const result = await http.get<ListResult<string>>('/front/search/suggestions', { keyword });
  return result.items || [];
}

export function fetchTagArticles(
  tagId: number | string,
  page = 1,
  pageSize = 10
): Promise<PageResult<ArticleSummary>> {
  return http.get<PageResult<ArticleSummary>>(`/front/tags/articles/${tagId}`, {
    page,
    pageSize
  });
}

export function reportArticleView(id: number | string): Promise<void> {
  return http.post<void>(`/front/articles/view/${id}`, undefined, { skipErrorToast: true });
}

export function fetchHotArticles(page = 1, pageSize = 5): Promise<PageResult<ArticleSummary>> {
  return http.get<PageResult<ArticleSummary>>('/front/hot/articles', { page, pageSize });
}

export async function fetchCategoryList(): Promise<CategoryItem[]> {
  const result = await http.get<ListResult<CategoryItem>>('/front/categories');
  return result.items || [];
}
