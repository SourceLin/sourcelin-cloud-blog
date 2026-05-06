import { requestData } from '@/shared/api/request'
import type { ListResult } from '@/shared/types/api'

export interface RemoteDictItem {
  value?: string
  label?: string
  tagType?: string
  sort?: number
  enabled?: boolean
}

interface FrontDictBundle {
  dictType: string
  items: RemoteDictItem[]
}

interface FrontDictQuery {
  types: string
}

export async function getDicts(dictTypes: string[]) {
  const uniq = [...new Set(dictTypes.filter(Boolean))]
  if (uniq.length === 0) {
    return {} as Record<string, RemoteDictItem[]>
  }
  const payload = await requestData<ListResult<FrontDictBundle>>({
    url: '/front/dicts',
    method: 'get',
    params: {
      types: uniq.join(',')
    } as FrontDictQuery,
    skipErrorMessage: true
  })
  if (!payload || !Array.isArray(payload.items)) {
    throw new Error('[dict] invalid payload from /front/dicts: missing items')
  }

  const rows = payload.items
  return rows.reduce<Record<string, RemoteDictItem[]>>((map, row) => {
    if (!row?.dictType) return map
    if (!Array.isArray(row.items)) {
      throw new Error(`[dict] invalid /front/dicts payload for dictType=${row.dictType}`)
    }
    map[row.dictType] = row.items
    return map
  }, {})
}

export async function getDictItemsByType(dictType: string) {
  const rowsMap = await getDicts([dictType])
  const hit = rowsMap[dictType]
  if (!hit) {
    throw new Error(`[dict] missing dictType in /front/dicts response: ${dictType}`)
  }
  return hit
}
