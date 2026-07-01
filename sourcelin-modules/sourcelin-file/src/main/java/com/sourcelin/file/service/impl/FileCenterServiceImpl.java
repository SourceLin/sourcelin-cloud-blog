package com.sourcelin.file.service.impl;

import com.sourcelin.common.core.exception.ServiceException;
import com.sourcelin.common.core.utils.DateUtils;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import com.sourcelin.file.api.domain.FileConfirmRequest;
import com.sourcelin.file.api.domain.SysFile;
import com.sourcelin.file.domain.FileInfo;
import com.sourcelin.file.mapper.FileInfoMapper;
import com.sourcelin.file.service.IFileCenterService;
import com.sourcelin.file.service.ISysFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Primary
public class FileCenterServiceImpl implements IFileCenterService
{
    @Autowired
    private ISysFileService sysFileService;

    @Autowired
    private FileInfoMapper fileInfoMapper;

    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    @Value("${file.path}")
    private String localFilePath;

    @Value("${file.prefix:/statics}")
    private String filePublicPrefix;

    @Override
    public SysFile upload(MultipartFile file) throws Exception
    {
        String url = sysFileService.uploadFile(file);

        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(extractFileName(url));
        fileInfo.setOriginalName(file.getOriginalFilename());
        fileInfo.setFileExt(extractFileExt(file.getOriginalFilename()));
        fileInfo.setContentType(file.getContentType());
        fileInfo.setFileSize(file.getSize());
        fileInfo.setStorageType(getStorageType());
        fileInfo.setStoragePath(extractStoragePath(url));
        fileInfo.setAccessUrl(url);
        fileInfo.setAccessScope("PUBLIC");
        fileInfo.setStatus("TEMP");
        fileInfo.setRefCount(0);
        fillFileOwner(fileInfo);
        fileInfo.setCreateTime(DateUtils.getNowDate());
        fileInfo.setUpdateTime(DateUtils.getNowDate());

        fileInfoMapper.insertFileInfo(fileInfo);

        SysFile sysFile = new SysFile();
        sysFile.setName(fileInfo.getFileName());
        sysFile.setOriginalName(fileInfo.getOriginalName());
        sysFile.setUrl(url);
        sysFile.setFileId(fileInfo.getFileId());
        sysFile.setSize(fileInfo.getFileSize());
        sysFile.setContentType(fileInfo.getContentType());

        return sysFile;
    }

    @Override
    public SysFile getFileMetadata(Long fileId)
    {
        FileInfo fileInfo = fileInfoMapper.selectFileInfoById(fileId);
        if (fileInfo == null)
        {
            throw new ServiceException("File not found: " + fileId);
        }
        checkBlogOwnerPermission(fileInfo, "查看");

        SysFile sysFile = new SysFile();
        sysFile.setName(fileInfo.getFileName());
        sysFile.setOriginalName(fileInfo.getOriginalName());
        sysFile.setUrl(fileInfo.getAccessUrl());
        sysFile.setFileId(fileInfo.getFileId());
        sysFile.setSize(fileInfo.getFileSize());
        sysFile.setContentType(fileInfo.getContentType());

        return sysFile;
    }

    @Override
    public FileInfo getFileInfo(Long fileId)
    {
        FileInfo fileInfo = fileInfoMapper.selectFileInfoById(fileId);
        if (fileInfo == null)
        {
            throw new ServiceException("File not found: " + fileId);
        }
        checkDownloadPermission(fileInfo);
        return fileInfo;
    }

    @Override
    public void confirmFile(Long fileId, FileConfirmRequest request)
    {
        FileInfo fileInfo = fileInfoMapper.selectFileInfoById(fileId);
        if (fileInfo == null)
        {
            throw new ServiceException("File not found: " + fileId);
        }
        checkBlogOwnerPermission(fileInfo, "确认");

        if (!"TEMP".equals(fileInfo.getStatus()))
        {
            return;
        }

        fileInfo.setStatus("ACTIVE");
        fileInfo.setBizType(request.getBizType());
        fileInfo.setBizId(request.getBizId());
        fileInfo.setRefCount(1);
        fileInfo.setUpdateTime(DateUtils.getNowDate());

        fileInfoMapper.updateFileInfo(fileInfo);
    }

    @Override
    public void deleteFile(Long fileId) throws Exception
    {
        FileInfo fileInfo = fileInfoMapper.selectFileInfoById(fileId);
        if (fileInfo == null)
        {
            return;
        }
        checkBlogOwnerPermission(fileInfo, "删除");

        fileInfo.setStatus("PENDING_DELETE");
        fileInfo.setDeleteTime(DateUtils.getNowDate());
        fileInfo.setUpdateTime(DateUtils.getNowDate());

        fileInfoMapper.updateFileInfo(fileInfo);
    }

    @Override
    public File resolvePhysicalFileForDownload(FileInfo fileInfo)
    {
        if (fileInfo == null)
        {
            return null;
        }
        Path base = Paths.get(localFilePath).toAbsolutePath().normalize();

        String stored = fileInfo.getStoragePath();
        if (stored != null && !stored.trim().isEmpty())
        {
            String t = stored.trim();
            if (!t.startsWith("http://") && !t.startsWith("https://"))
            {
                File direct = new File(t);
                if (direct.isFile() && direct.exists())
                {
                    return direct;
                }
                Path underBase = base.resolve(t.replaceFirst("^[/\\\\]+", "")).normalize();
                if (underBase.startsWith(base) && Files.isRegularFile(underBase))
                {
                    return underBase.toFile();
                }
            }
        }

        Path fromUrl = toAbsolutePathFromAccessUrl(fileInfo.getAccessUrl(), base);
        if (fromUrl == null || !fromUrl.startsWith(base) || !Files.isRegularFile(fromUrl))
        {
            return null;
        }
        return fromUrl.toFile();
    }

    private String getStorageType()
    {
        return "local";
    }

    private void fillFileOwner(FileInfo fileInfo)
    {
        Long adminUserId = adminLoginAccessor.getCurrentUserId();
        if (adminUserId != null)
        {
            fileInfo.setOwnerType("ADMIN");
            fileInfo.setOwnerId(adminUserId);
            fileInfo.setCreateBy(String.valueOf(adminUserId));
            return;
        }

        Long blogUserId = blogLoginAccessor.getCurrentUserId();
        if (blogUserId != null)
        {
            fileInfo.setOwnerType("BLOG");
            fileInfo.setOwnerId(blogUserId);
            fileInfo.setCreateBy(String.valueOf(blogUserId));
        }
    }

    /**
     * 下载权限规则：
     * 1. admin 登录可访问任意文件
     * 2. 匿名仅允许 ACTIVE + PUBLIC 文件
     * 3. blog 登录可访问 ACTIVE + PUBLIC，以及本人上传文件
     */
    private void checkDownloadPermission(FileInfo fileInfo)
    {
        if (adminLoginAccessor.isLogin())
        {
            return;
        }
        if (isActivePublicFile(fileInfo))
        {
            return;
        }
        if (blogLoginAccessor.isLogin() && isBlogOwner(fileInfo, blogLoginAccessor.getCurrentUserId()))
        {
            return;
        }
        throw new ServiceException("无权限下载该文件");
    }

    /**
     * 双域下 blog 仅能操作自己上传的文件；admin 与内部调用保持兼容。
     */
    private void checkBlogOwnerPermission(FileInfo fileInfo, String action)
    {
        if (adminLoginAccessor.isLogin())
        {
            return;
        }
        if (!blogLoginAccessor.isLogin())
        {
            // 兼容服务间调用（Feign）场景：无登录域时按内部调用放行
            return;
        }

        Long currentUserId = blogLoginAccessor.getCurrentUserId();
        boolean sameOwner = currentUserId != null
            && fileInfo.getOwnerId() != null
            && currentUserId.equals(fileInfo.getOwnerId());
        if (!sameOwner || !"BLOG".equalsIgnoreCase(fileInfo.getOwnerType()))
        {
            throw new ServiceException("无权限" + action + "该文件");
        }
    }

    private boolean isActivePublicFile(FileInfo fileInfo)
    {
        return fileInfo != null
            && "ACTIVE".equalsIgnoreCase(fileInfo.getStatus())
            && "PUBLIC".equalsIgnoreCase(fileInfo.getAccessScope());
    }

    private boolean isBlogOwner(FileInfo fileInfo, Long userId)
    {
        return fileInfo != null
            && userId != null
            && fileInfo.getOwnerId() != null
            && userId.equals(fileInfo.getOwnerId())
            && "BLOG".equalsIgnoreCase(fileInfo.getOwnerType());
    }

    private String extractFileName(String url)
    {
        if (url == null || url.isEmpty())
        {
            return null;
        }
        int lastSlash = url.lastIndexOf('/');
        return lastSlash >= 0 ? url.substring(lastSlash + 1) : url;
    }

    private String extractFileExt(String originalName)
    {
        if (originalName == null || originalName.isEmpty())
        {
            return null;
        }
        int lastDot = originalName.lastIndexOf('.');
        return lastDot >= 0 ? originalName.substring(lastDot + 1) : null;
    }

    /**
     * 写入库中的本地绝对路径，供下载流读取；不可再存完整 http(s) URL。
     */
    private String extractStoragePath(String url)
    {
        Path base = Paths.get(localFilePath).toAbsolutePath().normalize();
        Path resolved = toAbsolutePathFromAccessUrl(url, base);
        return resolved != null ? resolved.toString() : url;
    }

    /**
     * 从对外访问 URL（含域名）解析到 {@code file.path} 下的绝对路径。
     */
    private Path toAbsolutePathFromAccessUrl(String url, Path base)
    {
        if (url == null || url.isEmpty())
        {
            return null;
        }
        int profileIdx = url.indexOf("/profile");
        if (profileIdx >= 0)
        {
            String rel = url.substring(profileIdx + "/profile".length());
            while (rel.startsWith("/"))
            {
                rel = rel.substring(1);
            }
            if (rel.isEmpty() || rel.contains(".."))
            {
                return null;
            }
            Path p = base;
            for (String part : rel.split("/"))
            {
                if (!part.isEmpty())
                {
                    p = p.resolve(part);
                }
            }
            return p.normalize();
        }

        String prefix = filePublicPrefix.startsWith("/") ? filePublicPrefix : "/" + filePublicPrefix;
        String marker = prefix + "/";
        int pos = url.indexOf(marker);
        if (pos < 0)
        {
            return null;
        }
        String relative = url.substring(pos + marker.length());
        if (relative.isEmpty() || relative.contains(".."))
        {
            return null;
        }
        Path p = base;
        for (String part : relative.split("/"))
        {
            if (!part.isEmpty())
            {
                p = p.resolve(part);
            }
        }
        Path out = p.normalize();
        return out.startsWith(base) ? out : null;
    }

    @Override
    public java.util.List<FileInfo> selectFileInfoList(FileInfo fileInfo)
    {
        return fileInfoMapper.selectFileInfoList(fileInfo);
    }
}
