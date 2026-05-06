package com.sourcelin.file.service;

import com.sourcelin.file.api.domain.FileConfirmRequest;
import com.sourcelin.file.api.domain.SysFile;
import com.sourcelin.file.domain.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface IFileCenterService
{
    SysFile upload(MultipartFile file) throws Exception;

    SysFile getFileMetadata(Long fileId);

    FileInfo getFileInfo(Long fileId);

    void confirmFile(Long fileId, FileConfirmRequest request);

    void deleteFile(Long fileId) throws Exception;

    /**
     * 解析本地磁盘文件供下载/预览。兼容 storage_path 误存为完整 URL 的历史数据。
     */
    File resolvePhysicalFileForDownload(FileInfo fileInfo);
}
