package com.sourcelin.blog.controller.internal;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.api.model.LoginUser;
import com.sourcelin.blog.service.IUserService;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.domain.R;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoteUserControllerTest {

    @InjectMocks
    private RemoteUserController controller;

    @Mock
    private IUserService userService;

    @Test
    void shouldReturnUserByOpenId() {
        User user = new User();
        user.setId(9L);
        user.setUsername("tester");
        user.setOpenId("wx-open-id");

        when(userService.selectUserByOpenId("wx-open-id")).thenReturn(user);

        R<LoginUser> result = controller.getUserInfoByOpenId("wx-open-id", SecurityConstants.INNER);

        assertEquals(R.SUCCESS, result.getCode());
        assertNotNull(result.getData());
        assertEquals("tester", result.getData().getUser().getUsername());
        assertEquals("wx-open-id", result.getData().getUser().getOpenId());
    }

    @Test
    void shouldRejectOpenIdQueryFromNonInnerSource() {
        R<LoginUser> result = controller.getUserInfoByOpenId("wx-open-id", "outer");

        assertEquals(R.FAIL, result.getCode());
        assertNull(result.getData());
        assertEquals("非法访问来源", result.getMsg());
    }
}
