package com.sourcelin.blog.support;

import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.ServiceException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.file.api.domain.FileConfirmRequest;
import com.sourcelin.file.api.service.RemoteFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BlogFileCenterSupport
{
    @Autowired
    private RemoteFileService remoteFileService;

    public void confirmFile(Long fileId, String bizType, String bizId)
    {
        if (fileId == null || fileId <= 0)
        {
            return;
        }
        FileConfirmRequest request = new FileConfirmRequest();
        request.setFileId(fileId);
        request.setBizType(bizType);
        request.setBizId(bizId);
        ApiResponse<Void> result = remoteFileService.confirmFile(fileId, request);
        if (result == null
            || result.getCode() == null
            || !ResultCode.SUCCESS.getCode().equals(result.getCode()))
        {
            throw new ServiceException("confirm file failed: " + fileId
                + (result == null || StringUtils.isEmpty(result.getMessage()) ? "" : ", " + result.getMessage()));
        }
    }

    public void confirmFiles(String fileIds, String bizType, String bizId)
    {
        if (StringUtils.isEmpty(fileIds))
        {
            return;
        }
        String[] values = fileIds.split(",");
        for (String value : values)
        {
            if (StringUtils.isEmpty(value))
            {
                continue;
            }
            confirmFile(Long.valueOf(value.trim()), bizType, bizId);
        }
    }
}
