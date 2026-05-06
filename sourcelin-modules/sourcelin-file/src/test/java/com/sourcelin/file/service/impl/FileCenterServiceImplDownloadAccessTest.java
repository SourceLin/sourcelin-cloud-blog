package com.sourcelin.file.service.impl;

import com.sourcelin.common.core.exception.ServiceException;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import com.sourcelin.file.domain.FileInfo;
import com.sourcelin.file.mapper.FileInfoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileCenterServiceImplDownloadAccessTest
{
    @Mock
    private FileInfoMapper fileInfoMapper;
    @Mock
    private AdminLoginAccessor adminLoginAccessor;
    @Mock
    private BlogLoginAccessor blogLoginAccessor;

    private FileCenterServiceImpl service;

    @BeforeEach
    void setUp()
    {
        service = new FileCenterServiceImpl();
        ReflectionTestUtils.setField(service, "fileInfoMapper", fileInfoMapper);
        ReflectionTestUtils.setField(service, "adminLoginAccessor", adminLoginAccessor);
        ReflectionTestUtils.setField(service, "blogLoginAccessor", blogLoginAccessor);
    }

    @Test
    void shouldRejectAnonymousTempFileDownload()
    {
        FileInfo tempFile = buildFile(11L, "TEMP", "PUBLIC", "BLOG", 1001L);
        when(fileInfoMapper.selectFileInfoById(11L)).thenReturn(tempFile);
        when(adminLoginAccessor.isLogin()).thenReturn(false);
        when(blogLoginAccessor.isLogin()).thenReturn(false);

        assertThrows(ServiceException.class, () -> service.getFileInfo(11L));
    }

    @Test
    void shouldAllowAnonymousActivePublicFileDownload()
    {
        FileInfo activePublic = buildFile(12L, "ACTIVE", "PUBLIC", "BLOG", 1002L);
        when(fileInfoMapper.selectFileInfoById(12L)).thenReturn(activePublic);
        when(adminLoginAccessor.isLogin()).thenReturn(false);

        assertDoesNotThrow(() -> service.getFileInfo(12L));
    }

    @Test
    void shouldAllowBlogOwnerDownloadOwnTempFile()
    {
        FileInfo ownTempFile = buildFile(13L, "TEMP", "PUBLIC", "BLOG", 1003L);
        when(fileInfoMapper.selectFileInfoById(13L)).thenReturn(ownTempFile);
        when(adminLoginAccessor.isLogin()).thenReturn(false);
        when(blogLoginAccessor.isLogin()).thenReturn(true);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(1003L);

        assertDoesNotThrow(() -> service.getFileInfo(13L));
    }

    private FileInfo buildFile(Long fileId, String status, String accessScope, String ownerType, Long ownerId)
    {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(fileId);
        fileInfo.setStatus(status);
        fileInfo.setAccessScope(accessScope);
        fileInfo.setOwnerType(ownerType);
        fileInfo.setOwnerId(ownerId);
        return fileInfo;
    }
}
