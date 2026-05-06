package com.sourcelin.file.api.service;

import com.sourcelin.file.api.domain.FileConfirmRequest;
import com.sourcelin.file.api.domain.SysFile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import com.sourcelin.common.core.constant.ServiceNameConstants;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.file.api.factory.RemoteFileFallbackFactory;

/**
 * 文件服务
 * 
 * @author sourcelin
 */
@FeignClient(contextId = "remoteFileService", value = ServiceNameConstants.FILE_SERVICE, fallbackFactory = RemoteFileFallbackFactory.class)
public interface RemoteFileService
{
    /**
     * 上传文件
     *
     * @param file 文件信息
     * @return 结果
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<SysFile> upload(@RequestPart(value = "file") MultipartFile file);

    /**
     * 确认文件
     *
     * @param fileId 文件ID
     * @param request 确认请求
     * @return 结果
     */
    @PostMapping("/confirm/{fileId}")
    public ApiResponse<Void> confirmFile(@PathVariable("fileId") Long fileId, @RequestBody FileConfirmRequest request);

    /**
     * 获取文件元数据
     *
     * @param fileId 文件ID
     * @return 文件元数据
     */
    @GetMapping("/{fileId}")
    public ApiResponse<SysFile> getFileMetadata(@PathVariable("fileId") Long fileId);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @return 结果
     */
    @DeleteMapping("/{fileId}")
    public ApiResponse<Void> deleteFile(@PathVariable("fileId") Long fileId);
}
