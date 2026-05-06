import { computed } from 'vue'

export const FILE_API_BASE = import.meta.env.VITE_FILE_BASE_API || '/file'

/** 仅 fileId 时走二进制下载，/file/{id} 为元数据 JSON，不能作为 img src */
export function fileIdToDownloadUrl(fileId: string | number): string {
  const id = String(fileId).trim()
  if (!id) return ''
  return `${FILE_API_BASE}/download/${id}`
}

/** 业务库中应存数字 fileId；非数字（如历史错误数据）不能拼 download */
function isNumericFileId(token: string): boolean {
  return /^\d+$/.test(token.trim())
}

/**
 * 将上传接口返回的地址转为当前站点可展示的 URL（优先 /statics 并走网关 /file 代理）。
 * GET /file/{id} 返回 JSON，不可直接用于图像展示。
 */
export function resolveUploadedFileDisplayUrl(data: {
  url?: string | null
  fileId?: number | null
}): string {
  const raw = data.url?.trim()
  if (raw) {
    if (/^https?:\/\//i.test(raw)) {
      try {
        const parsed = new URL(raw)
        if (parsed.pathname.startsWith('/statics')) {
          return `${FILE_API_BASE}${parsed.pathname}${parsed.search}`
        }
      } catch {
        // ignore
      }
      return raw
    }
    if (raw.startsWith('/statics')) {
      return `${FILE_API_BASE}${raw}`
    }
    return raw
  }
  if (data.fileId != null) {
    return fileIdToDownloadUrl(data.fileId)
  }
  return ''
}

/** 列表里 `images` 字段存的绝对地址等，统一成当前页可用的展示 URL */
function normalizeStoredImageUrl(raw: string): string {
  const t = raw.trim()
  if (!t) return ''
  const viaUpload = resolveUploadedFileDisplayUrl({ url: t })
  return viaUpload || t
}

export interface FileRecord {
  avatar?: string | null
  avatarFileId?: number | null
  images?: string | null
  imageFileIds?: string | null
}

export function useFileUrl() {
  /** 与 {@link resolveUploadedFileDisplayUrl} 相同策略：优先 url，与正文插图一致 */
  const resolveAvatarUrl = (record: FileRecord | null | undefined): string => {
    if (!record) return ''
    return resolveUploadedFileDisplayUrl({
      url: record.avatar,
      fileId: record.avatarFileId ?? undefined
    })
  }

  const resolveCoverUrl = (record: FileRecord | null | undefined): string => {
    if (!record) return ''
    return resolveUploadedFileDisplayUrl({
      url: record.avatar,
      fileId: record.avatarFileId ?? undefined
    })
  }

  const resolveImageUrls = (record: FileRecord | null | undefined): string[] => {
    if (!record) return []

    const fromIds: string[] = []
    if (record.imageFileIds) {
      const tokens = record.imageFileIds.split(',').map((id) => id.trim()).filter(Boolean)
      for (const token of tokens) {
        if (isNumericFileId(token)) {
          fromIds.push(fileIdToDownloadUrl(token))
        }
      }
    }

    if (fromIds.length > 0) {
      return fromIds
    }

    if (record.images) {
      return record.images
        .split(',')
        .map((u) => normalizeStoredImageUrl(u))
        .filter(Boolean)
    }

    return []
  }

  const resolveImageUrl = (record: FileRecord | null | undefined, index: number): string => {
    const urls = resolveImageUrls(record)
    return urls[index] || ''
  }

  return {
    resolveAvatarUrl,
    resolveCoverUrl,
    resolveImageUrls,
    resolveImageUrl,
    FILE_API_BASE
  }
}
