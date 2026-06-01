export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
  requestId: string;
  timestamp: string | number;
}

export interface PageResult<T = unknown> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface ListResult<T = unknown> {
  items: T[];
}
