import request from "@/utils/request";
import type { FileInfo, FileInfoDetail, FileInfoPageQuery } from "@/types/api";

const FILE_BASE_URL = "/file";

const FileAPI = {
  /** 分页查询文件列表 */
  list(params: FileInfoPageQuery) {
    return request<unknown, PageResult<FileInfoDetail>>({
      url: `${FILE_BASE_URL}/list`,
      method: "get",
      params,
    });
  },

  /** 上传文件 （传入 FormData，上传进度回调） */
  upload(formData: FormData, onProgress?: (percent: number) => void) {
    return request<unknown, FileInfo>({
      url: `${FILE_BASE_URL}/upload`,
      method: "post",
      data: formData,
      headers: { "Content-Type": "multipart/form-data" },
      onUploadProgress: (progressEvent) => {
        if (progressEvent.total) {
          const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          onProgress?.(percent);
        }
      },
    });
  },

  /** 上传文件（传入 File） */
  uploadFile(file: File) {
    const formData = new FormData();
    formData.append("file", file);
    return request<unknown, FileInfo>({
      url: `${FILE_BASE_URL}/upload`,
      method: "post",
      data: formData,
      headers: { "Content-Type": "multipart/form-data" },
    });
  },

  /** 删除文件 */
  delete(filePath?: string | number) {
    const fileId = resolveFileId(filePath);
    if (fileId == null) {
      return Promise.reject(new Error("删除文件失败：未解析到 fileId"));
    }

    return request({
      url: `${FILE_BASE_URL}/${fileId}`,
      method: "delete",
    });
  },

  /** 下载文件 */
  download(url: string, fileName?: string) {
    return request({
      url,
      method: "get",
      responseType: "blob",
    }).then((res) => {
      const blob = new Blob([res.data]);
      const a = document.createElement("a");
      const urlObject = window.URL.createObjectURL(blob);
      a.href = urlObject;
      a.download = fileName || "下载文件";
      a.click();
      window.URL.revokeObjectURL(urlObject);
    });
  },
};

function resolveFileId(filePath?: string | number): number | null {
  if (typeof filePath === "number" && Number.isFinite(filePath)) {
    return filePath;
  }
  if (typeof filePath !== "string") {
    return null;
  }

  const text = filePath.trim();
  if (!text) {
    return null;
  }

  if (/^\d+$/.test(text)) {
    return Number(text);
  }

  // 兼容 /file/12、/file/download/12、/file/12/download（含 query/hash）
  const matched = text.match(/\/file\/(?:download\/)?(\d+)(?:\/download)?(?:[?#].*)?$/i);
  if (!matched) {
    return null;
  }
  return Number(matched[1]);
}

export default FileAPI;
