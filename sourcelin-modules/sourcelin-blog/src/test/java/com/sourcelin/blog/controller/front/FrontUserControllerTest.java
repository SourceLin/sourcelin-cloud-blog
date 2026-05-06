package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.domain.Follow;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.IFollowService;
import com.sourcelin.blog.service.IUserService;
import com.sourcelin.blog.vo.FrontUserInfoVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import com.sourcelin.file.api.service.RemoteFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FrontUserControllerTest
{
    @InjectMocks
    private FrontUserController controller;

    @Mock
    private IUserService userService;

    @Mock
    private IArticleService articleService;

    @Mock
    private IFollowService followService;

    @Mock
    private BlogLoginAccessor blogLoginAccessor;

    @Mock
    private RemoteFileService remoteFileService;

    @Test
    void shouldUpdateProfileFromTypedBody()
    {
        User currentUser = new User();
        currentUser.setId(7L);

        FrontUserController.ProfileUpdateBody body = new FrontUserController.ProfileUpdateBody();
        body.setNickName("  新昵称  ");
        body.setEmail("demo@example.com");
        body.setPhonenumber("13800000000");
        body.setSex(1);
        body.setIntroduction("个人简介");

        when(blogLoginAccessor.getCurrentUserId()).thenReturn(7L);
        when(userService.selectUserById(7L)).thenReturn(currentUser);
        when(userService.updateUser(any(User.class))).thenReturn(1);

        Void result = controller.updateProfile(body);

        assertNull(result);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).updateUser(userCaptor.capture());
        assertEquals("新昵称", userCaptor.getValue().getNickname());
        assertEquals("demo@example.com", userCaptor.getValue().getEmail());
        assertEquals("13800000000", userCaptor.getValue().getPhone());
        assertEquals(Integer.valueOf(1), userCaptor.getValue().getSex());
        assertEquals("个人简介", userCaptor.getValue().getIntroduction());
    }

    @Test
    void shouldReturnTypedFollowerRows()
    {
        User follower = new User();
        follower.setId(12L);
        follower.setUsername("tester");
        follower.setNickname("测试用户");
        follower.setAvatar("/avatar.png");

        Follow row = new Follow();
        row.setId(101L);
        row.setUserId(12L);
        row.setFollowUserId(99L);
        row.setUser(follower);

        when(followService.selectFansListWithUser(any(Follow.class))).thenReturn(Collections.singletonList(row));

        PageResult<FrontUserController.FrontFollowRelationVO> result = controller.userFollowers(99L, 1, 10, "all");

        assertEquals(1, result.getItems().size());
        FrontUserController.FrontFollowRelationVO item = result.getItems().get(0);
        assertEquals(Long.valueOf(101L), item.getId());
        assertEquals(Long.valueOf(12L), item.getUserId());
        assertEquals(Long.valueOf(99L), item.getFollowUserId());
        FrontUserInfoVO user = item.getUser();
        assertEquals(Long.valueOf(12L), user.getId());
        assertEquals("tester", user.getUserName());
        assertEquals("测试用户", user.getNickName());
        assertEquals("/avatar.png", user.getAvatar());
    }

    @Test
    void shouldRejectInvalidFollowerState()
    {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> controller.userFollowers(99L, 1, 10, "blocked"));

        assertEquals(ResultCode.VALIDATION_ERROR, ex.getResultCode());
        assertEquals("筛选参数不合法", ex.getMessage());
    }
}
