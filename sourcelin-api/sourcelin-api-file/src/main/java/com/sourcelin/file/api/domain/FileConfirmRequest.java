package com.sourcelin.file.api.domain;

import lombok.Data;

@Data
public class FileConfirmRequest
{
    private Long fileId;

    private String bizType;

    private String bizId;
}
