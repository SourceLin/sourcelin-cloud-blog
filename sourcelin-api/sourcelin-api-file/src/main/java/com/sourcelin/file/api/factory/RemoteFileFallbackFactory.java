package com.sourcelin.file.api.factory;

import com.sourcelin.file.api.domain.FileConfirmRequest;
import com.sourcelin.file.api.domain.SysFile;
import com.sourcelin.file.api.service.RemoteFileService;
import com.sourcelin.common.core.enums.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.sourcelin.common.core.web.domain.response.ApiResponse;

/**
 * 文件服务降级处理
 * 
 * @author sourcelin
 */
@Component
public class RemoteFileFallbackFactory implements FallbackFactory<RemoteFileService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteFileFallbackFactory.class);

    @Override
    public RemoteFileService create(Throwable throwable)
    {
        log.error("文件服务调用失败:{}", throwable.getMessage());
        return new RemoteFileService()
        {
            @Override
            public ApiResponse<SysFile> upload(MultipartFile file)
            {
                return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "上传文件失败:" + throwable.getMessage());
            }

            @Override
            public ApiResponse<Void> confirmFile(Long fileId, FileConfirmRequest request)
            {
                return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "确认文件失败:" + throwable.getMessage());
            }

            @Override
            public ApiResponse<SysFile> getFileMetadata(Long fileId)
            {
                return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "获取文件元数据失败:" + throwable.getMessage());
            }

            @Override
            public ApiResponse<Void> deleteFile(Long fileId)
            {
                return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "删除文件失败:" + throwable.getMessage());
            }
        };
    }
}
