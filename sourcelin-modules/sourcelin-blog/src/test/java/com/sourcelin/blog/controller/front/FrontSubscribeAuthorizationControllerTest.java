package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.SubscribeAuthorization;
import com.sourcelin.blog.dto.FrontSubscribeAuthorizationDTO;
import com.sourcelin.blog.service.ISubscribeAuthorizationService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FrontSubscribeAuthorizationControllerTest
{
    @InjectMocks
    private FrontSubscribeAuthorizationController controller;

    @Mock
    private ISubscribeAuthorizationService subscribeAuthorizationService;

    @Mock
    private BlogLoginAccessor blogLoginAccessor;

    @BeforeEach
    void setUpRequestContext()
    {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setRemoteAddr("127.0.0.1");
        mockRequest.addHeader("User-Agent", "JUnit");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }

    @AfterEach
    void clearRequestContext()
    {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldRejectAnonymousAuthorizationRecord()
    {
        FrontSubscribeAuthorizationDTO body = buildBody();
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.create(body));

        assertEquals(ResultCode.UNAUTHORIZED, ex.getResultCode());
        assertEquals("请先登录", ex.getMessage());
        verify(subscribeAuthorizationService, never()).insertSubscribeAuthorization(any(SubscribeAuthorization.class));
    }

    @Test
    void shouldCreateAuthorizationRecordForLoggedInUser()
    {
        FrontSubscribeAuthorizationDTO body = buildBody();
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(7L);
        when(subscribeAuthorizationService.insertSubscribeAuthorization(any(SubscribeAuthorization.class))).thenReturn(1);

        controller.create(body);

        ArgumentCaptor<SubscribeAuthorization> captor = ArgumentCaptor.forClass(SubscribeAuthorization.class);
        verify(subscribeAuthorizationService).insertSubscribeAuthorization(captor.capture());
        SubscribeAuthorization saved = captor.getValue();
        assertEquals(Long.valueOf(7L), saved.getUserId());
        assertEquals("y24CeVVFAMDOvRrmMzp7TK4spP8QFG5eEeiT0pxXazM", saved.getTemplateId());
        assertEquals("accept", saved.getAuthorizationStatus());
        assertEquals("settings", saved.getScene());
        assertEquals("mp-weixin", saved.getPlatform());
    }

    private FrontSubscribeAuthorizationDTO buildBody()
    {
        FrontSubscribeAuthorizationDTO body = new FrontSubscribeAuthorizationDTO();
        body.setTemplateId("y24CeVVFAMDOvRrmMzp7TK4spP8QFG5eEeiT0pxXazM");
        body.setAuthorizationStatus("accept");
        body.setScene("settings");
        body.setPlatform("mp-weixin");
        return body;
    }
}
