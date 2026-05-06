import type { LocationQuery, LocationQueryRaw } from 'vue-router'

export function parseDiscoverCategoryId(raw: unknown): number | null {
  const value = Array.isArray(raw) ? raw[0] : raw
  const id = Number(value)
  return Number.isFinite(id) && id > 0 ? id : null
}

export function buildDiscoverQuery(query: LocationQuery, categoryId: number | null): LocationQueryRaw {
  const nextQuery: LocationQueryRaw = { ...query }
  if (categoryId == null) {
    delete nextQuery.dc
  } else {
    nextQuery.dc = String(categoryId)
  }

  return nextQuery
}

export function resolveDiscoverCategoryId(
  categoryId: number | null,
  categories: Array<{ id: number }>
): number | null {
  if (categoryId == null) {
    return null
  }

  return categories.some((item) => item.id === categoryId) ? categoryId : null
}
