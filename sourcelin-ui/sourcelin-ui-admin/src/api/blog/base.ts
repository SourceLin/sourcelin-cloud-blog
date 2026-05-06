import request from "@/utils/request";
import type { BlogBaseEntity, BlogPageResult, BlogQueryParams } from "@/types/api";

function buildPageParams(queryParams: BlogQueryParams = {}): Record<string, unknown> {
  const currentPage = queryParams.page ?? 1;
  const pageSize = queryParams.pageSize ?? 10;
  return {
    ...queryParams,
    page: currentPage,
    pageSize,
  };
}

export function normalizePageResult<T extends BlogBaseEntity>(
  payload: Record<string, unknown>
): BlogPageResult<T> {
  const items = Array.isArray(payload?.items) ? (payload.items as unknown[]) : [];
  const page = Number(payload?.page ?? 1);
  const pageSize = Number(payload?.pageSize ?? items.length);
  const total = Number(payload?.total ?? items.length);
  const totalPages = Number(
    payload?.totalPages ?? (pageSize > 0 ? Math.ceil(total / pageSize) : 0)
  );

  return {
    items: items as T[],
    total,
    page,
    pageSize,
    totalPages,
  };
}

export function createBlogCrudApi<
  T extends BlogBaseEntity = BlogBaseEntity,
  Q extends BlogQueryParams = BlogQueryParams,
>(baseUrl: string) {
  return {
    async getPage(queryParams: Q) {
      const data = await request<unknown, unknown>({
        url: `${baseUrl}/list`,
        method: "get",
        params: buildPageParams(queryParams),
      });
      return normalizePageResult<T>(data as Record<string, unknown>);
    },

    getList(queryParams?: Record<string, unknown>) {
      return request<unknown, T[]>({
        url: `${baseUrl}/list`,
        method: "get",
        params: queryParams,
      });
    },

    getFormData(id: string | number) {
      return request<unknown, T>({
        url: `${baseUrl}/${id}`,
        method: "get",
      });
    },

    create(data: Partial<T>) {
      return request<Partial<T>, void>({
        url: `${baseUrl}`,
        method: "post",
        data,
      });
    },

    update(_id: string | number, data: Partial<T>) {
      return request<Partial<T>, void>({
        url: `${baseUrl}`,
        method: "put",
        data,
      });
    },

    deleteByIds(ids: string | number | Array<string | number>) {
      const idString = Array.isArray(ids) ? ids.join(",") : String(ids);
      return request<void, void>({
        url: `${baseUrl}/${idString}`,
        method: "delete",
      });
    },

    export(queryParams: Q) {
      return request<Q, Blob>({
        url: `${baseUrl}/export`,
        method: "post",
        data: queryParams,
        responseType: "blob",
      });
    },
  };
}
