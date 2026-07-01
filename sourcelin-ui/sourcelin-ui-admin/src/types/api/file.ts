/**
 * File 文件上传类型定义
 */

/** 文件信息 */
export interface FileInfo {
  /** 文件名称 */
  name: string;
  /** 文件URL */
  url: string;
}

/** 完整文件信息实体 */
export interface FileInfoDetail {
  fileId: number;
  fileName: string;
  originalName: string;
  fileExt: string;
  contentType: string;
  fileSize: number;
  storageType: string;
  storagePath: string;
  accessUrl: string;
  accessScope: string;
  status: string;
  refCount: number;
  ownerType: string;
  ownerId: number;
  createTime: string;
  updateTime: string;
}

/** 文件分页查询参数 */
export interface FileInfoPageQuery {
  page: number;
  pageSize: number;
  fileName?: string;
  status?: string;
  ownerType?: string;
}
