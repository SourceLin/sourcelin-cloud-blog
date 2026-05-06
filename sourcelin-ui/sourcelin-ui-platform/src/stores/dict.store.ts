import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getDictItemsByType, getDicts, type RemoteDictItem } from '@/modules/dict/api/dict.service'
import { FALLBACK_DICTS, type DictOption } from '@/shared/constants/fallback-dicts'

const DICT_CACHE_KEY = 'platform:dict-cache'
const DICT_CACHE_TTL_MS = 10 * 60 * 1000

interface DictCachePayload {
  cachedAt: number
  dictMap: Record<string, DictOption[]>
}

function normalizeRemoteItems(items: RemoteDictItem[]): DictOption[] {
  return items
    .filter((item) => item?.enabled !== false)
    .sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
    .map((item) => ({
      label: item.label || '',
      value: String(item.value ?? ''),
      tagType: resolveTagTypeFromRemote(item.tagType)
    }))
    .filter((item) => item.label && item.value)
}

function resolveTagTypeFromRemote(tagType?: string): DictOption['tagType'] {
  const v = (tagType || '').trim()
  if (v === 'success' || v === 'warning' || v === 'error' || v === 'info' || v === 'default') {
    return v
  }
  return 'default'
}

export const useDictStore = defineStore('platform-dict', () => {
  const dictMap = ref<Record<string, DictOption[]>>({})
  const loadingTypes = ref<Set<string>>(new Set())
  const cachedAt = ref<number>(0)
  const missingDictTypes = ref<Set<string>>(new Set())
  const lastMissingReportedSize = ref<number>(0)

  function recordMissing(dictType: string) {
    const v = String(dictType || '').trim()
    if (!v) return
    missingDictTypes.value.add(v)
  }

  function reportMissingIfNeeded() {
    const size = missingDictTypes.value.size
    if (size <= 0) return
    if (size === lastMissingReportedSize.value) return
    lastMissingReportedSize.value = size
    const list = Array.from(missingDictTypes.value).sort()
    console.warn(`[dict] missing dictTypes, fallback will be used: ${list.join(', ')}`)
  }

  function isCacheExpired() {
    if (!cachedAt.value) return true
    return Date.now() - cachedAt.value > DICT_CACHE_TTL_MS
  }

  function hydrateFromCache() {
    const cached = localStorage.getItem(DICT_CACHE_KEY)
    if (!cached) return
    try {
      const payload = JSON.parse(cached) as DictCachePayload
      if (!payload || typeof payload !== 'object' || !payload.dictMap) {
        throw new Error('invalid dict cache payload')
      }
      dictMap.value = payload.dictMap ?? {}
      cachedAt.value = Number(payload.cachedAt ?? 0)
    } catch {
      dictMap.value = {}
      cachedAt.value = 0
    }
  }

  function persistToCache() {
    const payload: DictCachePayload = {
      cachedAt: Date.now(),
      dictMap: dictMap.value
    }
    cachedAt.value = payload.cachedAt
    localStorage.setItem(DICT_CACHE_KEY, JSON.stringify(payload))
  }

  function getDictItems(dictType: string): DictOption[] {
    const cached = dictMap.value[dictType]
    if (Array.isArray(cached) && cached.length > 0) {
      return cached
    }
    return FALLBACK_DICTS[dictType] ?? []
  }

  async function ensureDict(dictType: string) {
    if (loadingTypes.value.has(dictType)) {
      return
    }
    const cached = dictMap.value[dictType]
    if (Array.isArray(cached) && cached.length > 0 && !isCacheExpired()) {
      return
    }
    loadingTypes.value.add(dictType)
    try {
      const rows = await getDictItemsByType(dictType)
      const normalized = normalizeRemoteItems(Array.isArray(rows) ? rows : [])
      if (normalized.length > 0) {
        dictMap.value[dictType] = normalized
        persistToCache()
      } else {
        recordMissing(dictType)
        console.warn(`[dict] remote dict empty, fallback used: ${dictType}`)
      }
    } catch {
      recordMissing(dictType)
      console.warn(`[dict] remote dict load failed, fallback used: ${dictType}`)
    } finally {
      loadingTypes.value.delete(dictType)
    }
  }

  async function ensureDicts(dictTypes: string[]) {
    const uniq = [...new Set(dictTypes.filter(Boolean))]
    const shouldLoad = uniq.filter((dictType) => {
      const cached = dictMap.value[dictType]
      return !(Array.isArray(cached) && cached.length > 0 && !isCacheExpired())
    })
    if (shouldLoad.length === 0) return
    shouldLoad.forEach((dictType) => loadingTypes.value.add(dictType))
    try {
      const rowsMap = await getDicts(shouldLoad)
      shouldLoad.forEach((dictType) => {
        const normalized = normalizeRemoteItems(Array.isArray(rowsMap[dictType]) ? rowsMap[dictType] : [])
        if (normalized.length > 0) {
          dictMap.value[dictType] = normalized
        } else {
          recordMissing(dictType)
          console.warn(`[dict] remote dict empty, fallback used: ${dictType}`)
        }
      })
      persistToCache()
    } catch {
      shouldLoad.forEach((dictType) => recordMissing(dictType))
      shouldLoad.forEach((dictType) => {
        console.warn(`[dict] remote dict batch load failed, fallback used: ${dictType}`)
      })
    } finally {
      shouldLoad.forEach((dictType) => loadingTypes.value.delete(dictType))
      reportMissingIfNeeded()
    }
  }

  function resolveLabel(dictType: string, value: string | number | undefined): string {
    if (value == null) return ''
    const target = String(value)
    const hit = getDictItems(dictType).find((item) => item.value === target)
    return hit?.label ?? ''
  }

  function resolveTagType(dictType: string, value: string | number | undefined): DictOption['tagType'] {
    if (value == null) return 'default'
    const target = String(value)
    const hit = getDictItems(dictType).find((item) => item.value === target)
    return hit?.tagType ?? 'default'
  }

  hydrateFromCache()

  return {
    dictMap,
    cachedAt,
    ensureDict,
    ensureDicts,
    getDictItems,
    resolveLabel,
    resolveTagType,
    hydrateFromCache,
    isCacheExpired,
    missingDictTypes
  }
})
