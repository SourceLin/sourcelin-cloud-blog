package com.sourcelin.file.domain;

import java.util.Date;
import com.sourcelin.common.core.web.domain.BaseEntity;
import lombok.Data;

@Data
public class FileInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long fileId;

    private String fileName;

    private String originalName;

    private String fileExt;

    private String contentType;

    private Long fileSize;

    private String fileHash;

    private String storageType;

    private String bucketName;

    private String storagePath;

    private String accessUrl;

    private String accessScope;

    private String bizType;

    private String bizId;

    private String ownerType;

    private Long ownerId;

    private String status;

    private Integer refCount;

    private Date expireTime;

    private Date lastAccessTime;

    private Date deleteTime;
}
