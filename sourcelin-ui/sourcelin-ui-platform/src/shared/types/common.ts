export interface PageParams {
  page?: number
  pageSize?: number
}

export interface OptionItem<T = string | number> {
  label: string
  value: T
}

export interface KeyValue<T = unknown> {
  [key: string]: T
}
