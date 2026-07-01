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
import com.sourcelin.system.api.service.RemoteSysDictDataService;
import com.sourcelin.system.api.domain.SysDictData;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.blog.vo.FrontCurrentUserInfoVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Mock
    private RemoteSysDictDataService remoteSysDictDataService;

    @Test
    void shouldUpdateProfileFromTypedBody()
    {
        User currentUser = new User();
        currentUser.setId(7L);
        currentUser.setEmail("demo@example.com");

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

    @Test
    void shouldGetUserInfoForNormalUser()
    {
        User user = new User();
        user.setId(7L);
        user.setUsername("normalUser");
        user.setNickname("普通用户");
        user.setUserType(1);

        List<SysDictData> dictDataList = new ArrayList<>();
        SysDictData normalDict = new SysDictData();
        normalDict.setDictLabel("普通用户");
        normalDict.setDictValue("1");
        normalDict.setStatus("0");
        SysDictData bloggerDict = new SysDictData();
        bloggerDict.setDictLabel("博主");
        bloggerDict.setDictValue("2");
        bloggerDict.setStatus("0");
        dictDataList.add(normalDict);
        dictDataList.add(bloggerDict);

        when(blogLoginAccessor.isLogin()).thenReturn(true);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(7L);
        when(userService.selectUserById(7L)).thenReturn(user);
        when(articleService.countArticleByUserId(7L)).thenReturn(5);
        when(followService.countFansByTargetUserId(7L)).thenReturn(10);
        when(remoteSysDictDataService.listByType("blog_user_type", "front-platform"))
            .thenReturn(ApiResponse.success(dictDataList));

        FrontCurrentUserInfoVO info = controller.getCurrentUserInfo();

        assertEquals(7L, info.getId());
        assertEquals("normalUser", info.getUserName());
        assertEquals("普通用户", info.getNickName());
        assertEquals(Integer.valueOf(1), info.getUserType());
        assertEquals("普通用户", info.getUserTypeLabel());
        assertEquals(0, info.getRoles().size());
        assertEquals(0, info.getPermissions().size());
    }

    @Test
    void shouldGetUserInfoForBloggerWithDictSuccess()
    {
        User user = new User();
        user.setId(8L);
        user.setUsername("bloggerUser");
        user.setNickname("博主昵称");
        user.setUserType(2);

        List<SysDictData> dictDataList = new ArrayList<>();
        SysDictData normalDict = new SysDictData();
        normalDict.setDictLabel("普通用户");
        normalDict.setDictValue("1");
        normalDict.setStatus("0");
        SysDictData bloggerDict = new SysDictData();
        bloggerDict.setDictLabel("博主");
        bloggerDict.setDictValue("2");
        bloggerDict.setStatus("0");
        dictDataList.add(normalDict);
        dictDataList.add(bloggerDict);

        when(blogLoginAccessor.isLogin()).thenReturn(true);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(8L);
        when(userService.selectUserById(8L)).thenReturn(user);
        when(articleService.countArticleByUserId(8L)).thenReturn(15);
        when(followService.countFansByTargetUserId(8L)).thenReturn(20);
        when(remoteSysDictDataService.listByType("blog_user_type", "front-platform"))
            .thenReturn(ApiResponse.success(dictDataList));

        FrontCurrentUserInfoVO info = controller.getCurrentUserInfo();

        assertEquals(8L, info.getId());
        assertEquals(Integer.valueOf(2), info.getUserType());
        assertEquals("博主", info.getUserTypeLabel());
        assertEquals(1, info.getRoles().size());
        assertEquals("blogger", info.getRoles().get(0));
        assertEquals(2, info.getPermissions().size());
        org.junit.jupiter.api.Assertions.assertTrue(info.getPermissions().contains("mini:blogger"));
        org.junit.jupiter.api.Assertions.assertTrue(info.getPermissions().contains("blog:article:write"));
    }

    @Test
    void shouldGetUserInfoForBloggerWithDictFallback()
    {
        User user = new User();
        user.setId(8L);
        user.setUsername("bloggerUser");
        user.setNickname("博主昵称");
        user.setUserType(2);

        when(blogLoginAccessor.isLogin()).thenReturn(true);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(8L);
        when(userService.selectUserById(8L)).thenReturn(user);
        when(articleService.countArticleByUserId(8L)).thenReturn(15);
        when(followService.countFansByTargetUserId(8L)).thenReturn(20);

        // 模拟 Feign 报错以触发熔断降级
        when(remoteSysDictDataService.listByType("blog_user_type", "front-platform"))
            .thenThrow(new RuntimeException("Feign connection timeout"));

        FrontCurrentUserInfoVO info = controller.getCurrentUserInfo();

        assertEquals(8L, info.getId());
        assertEquals(Integer.valueOf(2), info.getUserType());
        // 兜底应该依然正确返回“博主”
        assertEquals("博主", info.getUserTypeLabel());
        assertEquals(1, info.getRoles().size());
        assertEquals("blogger", info.getRoles().get(0));
    }
}
