import { env } from '@/config/env';
import { upload } from '@/utils/request';

export interface UploadedFileData {
  fileId?: number;
  url?: string;
  originalName?: string;
  size?: number;
  contentType?: string;
}

export function uploadPublicFile(filePath: string): Promise<UploadedFileData> {
  return upload<UploadedFileData>({
    url: `${env.baseURL}/file/upload`,
    filePath
  });
}
