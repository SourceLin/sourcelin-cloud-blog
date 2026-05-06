package com.sourcelin.file.controller;

import com.sourcelin.file.domain.FileInfo;
import com.sourcelin.file.service.IFileCenterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileCenterControllerDownloadHeaderTest
{
    @Mock
    private IFileCenterService fileCenterService;

    @Test
    void shouldForceAttachmentForHtmlContent() throws Exception
    {
        Path tempFile = Files.createTempFile("file-center-download", ".html");
        Files.write(tempFile, "<h1>x</h1>".getBytes(StandardCharsets.UTF_8));
        try
        {
            FileCenterController controller = new FileCenterController();
            ReflectionTestUtils.setField(controller, "fileCenterService", fileCenterService);

            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(1L);
            fileInfo.setOriginalName("payload.html");
            fileInfo.setFileName("payload.html");
            fileInfo.setContentType("text/html;charset=UTF-8");

            when(fileCenterService.getFileInfo(1L)).thenReturn(fileInfo);
            when(fileCenterService.resolvePhysicalFileForDownload(fileInfo)).thenReturn(tempFile.toFile());

            MockHttpServletResponse response = new MockHttpServletResponse();
            controller.download(1L, response);

            assertThat(response.getHeader("Content-Disposition")).startsWith("attachment;");
            assertThat(response.getContentType()).isEqualTo("application/octet-stream");
            assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
        }
        finally
        {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void shouldKeepInlineForMp4Preview() throws Exception
    {
        Path tempFile = Files.createTempFile("file-center-download", ".mp4");
        Files.write(tempFile, new byte[] {0, 1, 2, 3});
        try
        {
            FileCenterController controller = new FileCenterController();
            ReflectionTestUtils.setField(controller, "fileCenterService", fileCenterService);

            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileId(2L);
            fileInfo.setOriginalName("clip.mp4");
            fileInfo.setFileName("clip.mp4");
            fileInfo.setContentType("video/mp4");

            when(fileCenterService.getFileInfo(2L)).thenReturn(fileInfo);
            when(fileCenterService.resolvePhysicalFileForDownload(fileInfo)).thenReturn(tempFile.toFile());

            MockHttpServletResponse response = new MockHttpServletResponse();
            controller.download(2L, response);

            assertThat(response.getHeader("Content-Disposition")).startsWith("inline;");
            assertThat(response.getContentType()).isEqualTo("video/mp4");
        }
        finally
        {
            Files.deleteIfExists(tempFile);
        }
    }
}
