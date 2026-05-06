package com.sourcelin.file.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.sourcelin.common.core.utils.DateUtils;
import com.sourcelin.common.core.utils.file.FileUtils;
import com.sourcelin.file.api.domain.FileConfirmRequest;
import com.sourcelin.file.domain.FileInfo;
import com.sourcelin.file.service.IFileCenterService;
import com.sourcelin.file.service.ISysFileService;
import com.sourcelin.file.api.domain.SysFile;

/**
 * 文件请求处理
 * 
 * @author sourcelin
 */
@RestController
public class FileCenterController
{
    private static final Logger log = LoggerFactory.getLogger(FileCenterController.class);
    private static final String OCTET_STREAM = "application/octet-stream";
    private static final Set<String> INLINE_IMAGE_TYPES = new HashSet<>(Arrays.asList(
        "image/png", "image/jpeg", "image/jpg", "image/gif", "image/bmp", "image/webp"
    ));
    private static final Set<String> INLINE_IMAGE_EXTS = new HashSet<>(Arrays.asList(
        "png", "jpg", "jpeg", "gif", "bmp", "webp"
    ));
    private static final Set<String> INLINE_VIDEO_TYPES = new HashSet<>(Arrays.asList(
        "video/mp4", "video/webm", "video/ogg"
    ));
    private static final Set<String> INLINE_VIDEO_EXTS = new HashSet<>(Arrays.asList(
        "mp4", "webm", "ogg", "ogv"
    ));

    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private IFileCenterService fileCenterService;

    /**
     * 文件上传请求
     */
    @PostMapping("upload")
    public SysFile upload(MultipartFile file)
    {
        try
        {
            return fileCenterService.upload(file);
        }
        catch (Exception e)
        {
            if (e instanceof BusinessException)
            {
                throw (BusinessException) e;
            }
            log.error("上传文件失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "上传文件失败");
        }
    }

    /**
     * 确认文件
     */
    @PostMapping("confirm/{fileId}")
    public Void confirm(@PathVariable("fileId") Long fileId, @RequestBody FileConfirmRequest request)
    {
        try
        {
            if (request.getFileId() != null && !fileId.equals(request.getFileId()))
            {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "路径 fileId 与请求体 fileId 不一致");
            }
            request.setFileId(fileId);
            fileCenterService.confirmFile(fileId, request);
            return null;
        }
        catch (Exception e)
        {
            if (e instanceof BusinessException)
            {
                throw (BusinessException) e;
            }
            log.error("确认文件失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "确认文件失败");
        }
    }

    /**
     * 获取文件元数据
     */
    @GetMapping("{fileId}")
    public SysFile getFileMetadata(@PathVariable("fileId") Long fileId)
    {
        try
        {
            return fileCenterService.getFileMetadata(fileId);
        }
        catch (Exception e)
        {
            if (e instanceof BusinessException)
            {
                throw (BusinessException) e;
            }
            log.error("获取文件元数据失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "获取文件元数据失败");
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("{fileId}")
    public Void deleteFile(@PathVariable("fileId") Long fileId)
    {
        try
        {
            fileCenterService.deleteFile(fileId);
            return null;
        }
        catch (Exception e)
        {
            if (e instanceof BusinessException)
            {
                throw (BusinessException) e;
            }
            log.error("删除文件失败", e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "删除文件失败");
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("download/{fileId}")
    public void download(@PathVariable("fileId") Long fileId, HttpServletResponse response)
    {
        FileInputStream fis = null;
        try
        {
            FileInfo fileInfo = fileCenterService.getFileInfo(fileId);
            String fileName = fileInfo.getOriginalName();
            if (fileName == null)
            {
                fileName = fileInfo.getFileName();
            }
            fileInfo.setLastAccessTime(DateUtils.getNowDate());

            File file = fileCenterService.resolvePhysicalFileForDownload(fileInfo);
            if (file == null || !file.exists())
            {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            applySafeDownloadHeaders(fileInfo, response, fileName);

            fis = new FileInputStream(file);
            OutputStream os = response.getOutputStream();
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0)
            {
                os.write(b, 0, length);
            }
            os.flush();
        }
        catch (IOException e)
        {
            log.error("下载文件失败", e);
        }
        catch (ServiceException e)
        {
            int status = isFileNotFound(e) ? HttpServletResponse.SC_NOT_FOUND : HttpServletResponse.SC_FORBIDDEN;
            response.setStatus(status);
            log.warn("下载文件被拒绝 fileId={}, reason={}", fileId, e.getMessage());
        }
        finally
        {
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                    log.error("关闭流失败", e);
                }
            }
        }
    }

    private void applySafeDownloadHeaders(FileInfo fileInfo, HttpServletResponse response, String fileName) throws IOException
    {
        String normalizedContentType = normalizeContentType(fileInfo.getContentType());
        String ext = extractExt(fileName);
        boolean safeInline = isSafeInlineMedia(normalizedContentType, ext);

        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setContentType(safeInline ? normalizedContentType : OCTET_STREAM);
        response.setHeader("Content-Disposition",
            (safeInline ? "inline" : "attachment") + ";filename=" + FileUtils.percentEncode(fileName));
    }

    private boolean isSafeInlineMedia(String contentType, String ext)
    {
        if (contentType == null || ext == null)
        {
            return false;
        }
        if (INLINE_IMAGE_TYPES.contains(contentType))
        {
            return INLINE_IMAGE_EXTS.contains(ext);
        }
        if (INLINE_VIDEO_TYPES.contains(contentType))
        {
            return INLINE_VIDEO_EXTS.contains(ext);
        }
        return false;
    }

    private String normalizeContentType(String contentType)
    {
        if (contentType == null)
        {
            return null;
        }
        String value = contentType.trim().toLowerCase(Locale.ROOT);
        int semi = value.indexOf(';');
        return semi >= 0 ? value.substring(0, semi).trim() : value;
    }

    private String extractExt(String fileName)
    {
        if (fileName == null)
        {
            return null;
        }
        String value = fileName.trim();
        int dot = value.lastIndexOf('.');
        if (dot < 0 || dot == value.length() - 1)
        {
            return null;
        }
        return value.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private boolean isFileNotFound(ServiceException e)
    {
        String msg = e.getMessage();
        return msg != null && msg.startsWith("File not found");
    }
}
