import { deleteUploadedFile } from '@/shared/api/file.api'

/** 列表项 id 为纯数字时视为服务端已持久化的 fileId */
export function isPersistedFileUploadId(id: string): boolean {
  return /^\d+$/.test(id)
}

/**
 * 移除已上传项前调用：对已落地且 finished 的项执行删除。
 * 失败返回 false（由调用方阻止 UI 移除）；错误提示由 http 拦截器处理。
 */
export async function tryDeletePersistedFile(file: {
  id: string
  status: string
}): Promise<boolean> {
  if (file.status !== 'finished' || !isPersistedFileUploadId(file.id)) {
    return true
  }
  try {
    await deleteUploadedFile(file.id)
    return true
  } catch (error) {
    console.error('删除已上传文件失败', error)
    return false
  }
}
