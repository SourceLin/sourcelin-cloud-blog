import { http } from '@/utils/request';
import type { PageResult } from '@/shared/types/api';
import type {
  ArticleDetail,
  ArticlePageQuery,
  ArticleSummary,
  CategoryItem,
  FrontHome,
  HomePageQuery
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
