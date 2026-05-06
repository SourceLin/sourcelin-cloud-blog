import { requestData } from '@/shared/api/http'
import { FILE_API_BASE } from '@/shared/composables/useFileUrl'

export interface UploadedFileData {
  url: string
  fileId?: number
  originalName?: string
}

/** multipart 上传到 VITE_FILE_BASE_API（如 /file/upload） */
export function uploadFile(data: FormData) {
  return requestData<UploadedFileData>({
    baseURL: '',
    url: `${FILE_API_BASE}/upload`,
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/** DELETE /file/{fileId} */
export function deleteUploadedFile(fileId: string | number) {
  return requestData<void>({
    baseURL: '',
    url: `${FILE_API_BASE}/${fileId}`,
    method: 'delete'
  })
}
