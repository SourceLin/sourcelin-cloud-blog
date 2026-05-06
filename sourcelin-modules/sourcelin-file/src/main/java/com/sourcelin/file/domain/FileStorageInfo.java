package com.sourcelin.file.domain;

import lombok.Data;

@Data
public class FileStorageInfo
{
    private String storageType;

    private String bucketName;

    private String storagePath;

    private String accessUrl;

    private String fileName;
}
