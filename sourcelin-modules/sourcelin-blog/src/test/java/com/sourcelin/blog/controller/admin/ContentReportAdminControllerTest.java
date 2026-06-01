package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.domain.ContentReport;
import com.sourcelin.blog.service.IContentReportService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentReportAdminControllerTest
{
    @InjectMocks
    private ContentReportAdminController controller;

    @Mock
    private IContentReportService contentReportService;

    @Test
    void shouldReturnReportDetail()
    {
        ContentReport report = new ContentReport();
        report.setId(10L);
        report.setStatus("PENDING");
        when(contentReportService.selectContentReportById(10L)).thenReturn(report);

        ContentReport result = controller.getInfo(10L);

        assertEquals(Long.valueOf(10L), result.getId());
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    void shouldRejectMissingReportDetail()
    {
        when(contentReportService.selectContentReportById(99L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.getInfo(99L));

        assertEquals(ResultCode.NOT_FOUND, ex.getResultCode());
        assertEquals("举报记录不存在", ex.getMessage());
    }

    @Test
    void shouldHandleReportStatus()
    {
        ContentReportAdminController.ReportHandleBody body = new ContentReportAdminController.ReportHandleBody();
        body.setStatus("RESOLVED");
        body.setRemark("已处理违规内容");
        when(contentReportService.handleContentReport(10L, "RESOLVED", "已处理违规内容")).thenReturn(1);

        Void result = controller.handle(10L, body);

        assertNull(result);
        verify(contentReportService).handleContentReport(10L, "RESOLVED", "已处理违规内容");
    }

    @Test
    void shouldRejectEmptyHandleStatus()
    {
        ContentReportAdminController.ReportHandleBody body = new ContentReportAdminController.ReportHandleBody();
        body.setStatus("");

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.handle(10L, body));

        assertEquals(ResultCode.VALIDATION_ERROR, ex.getResultCode());
        assertEquals("处理状态不能为空", ex.getMessage());
    }
}
