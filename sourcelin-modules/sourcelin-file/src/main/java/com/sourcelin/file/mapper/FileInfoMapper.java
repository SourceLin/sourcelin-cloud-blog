package com.sourcelin.file.mapper;

import com.sourcelin.file.domain.FileInfo;

public interface FileInfoMapper
{
    int insertFileInfo(FileInfo fileInfo);

    FileInfo selectFileInfoById(Long fileId);

    int updateFileInfo(FileInfo fileInfo);
}
