package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.constant.ArticleConstants;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Follow;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.IFollowService;
import com.sourcelin.blog.service.IUserService;
import com.sourcelin.blog.vo.ArticleVO;
import com.sourcelin.blog.vo.FrontAvatarUploadVO;
import com.sourcelin.blog.vo.FrontCurrentUserInfoVO;
import com.sourcelin.blog.vo.FrontUserInfoVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import com.sourcelin.common.security.stp.StpBlogUtil;
import com.sourcelin.file.api.domain.SysFile;
import com.sourcelin.file.api.service.RemoteFileService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sourcelin.common.security.utils.SecurityUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/front")
public class FrontUserController extends BaseController
{
    @Autowired
    private IUserService userService;
    @Autowired
    private IArticleService articleService;
    @Autowired
    private IFollowService followService;
    @Autowired
    private BlogLoginAccessor blogLoginAccessor;
    @Autowired
    private RemoteFileService remoteFileService;

    @GetMapping("/user/info")
    public FrontCurrentUserInfoVO getCurrentUserInfo()
    {
        try
        {
            if (!StpBlogUtil.stpLogic.isLogin())
            {
                throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
            }

            Long userId = StpBlogUtil.stpLogic.getLoginIdAsLong();
            User user = userService.selectUserById(userId);
            if (user == null)
            {
                throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
            }
            user.setPassword(null);
            user.setArticleCount((long) articleService.countArticleByUserId(userId));
            user.setFollowerCount((long) followService.countFansByTargetUserId(userId));
            FrontCurrentUserInfoVO payload = buildCurrentUserPayload(user);
            payload.setRoles(Collections.<String>emptyList());
            payload.setPermissions(Collections.<String>emptyList());
            return payload;
        }
        catch (Exception e)
        {
            if (e instanceof BusinessException)
            {
                throw (BusinessException) e;
            }
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
    }

    @GetMapping("/user/profile")
    public FrontUserInfoVO profile()
    {
        Long currentUserId = blogLoginAccessor.getCurrentUserId();
        if (currentUserId == null || currentUserId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        User user = userService.selectUserById(currentUserId);
        if (user == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        user.setPassword(null);

        return buildFrontUserPayload(user);
    }

    @PutMapping("/user/profile")
    public Void updateProfile(@RequestBody ProfileUpdateBody body)
    {
        if (body == null)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "参数不能为空");
        }
        Long currentUserId = blogLoginAccessor.getCurrentUserId();
        if (currentUserId == null || currentUserId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        User currentUser = userService.selectUserById(currentUserId);
        if (currentUser == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        currentUser.setNickname(StringUtils.trim(body.getNickName()));
        currentUser.setEmail(StringUtils.trim(body.getEmail()));
        currentUser.setPhone(StringUtils.trim(body.getPhonenumber()));
        currentUser.setSex(body.getSex());
        currentUser.setIntroduction(StringUtils.trim(body.getIntroduction()));

        if (userService.updateUser(currentUser) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "资料更新失败");
        }
        return null;
    }

    @PutMapping("/user/updatePwd")
    public Void updatePwd(@RequestParam String oldPassword, @RequestParam String newPassword)
    {
        if (!StpBlogUtil.stpLogic.isLogin())
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "旧密码和新密码不能为空");
        }
        if (newPassword.length() < 6 || newPassword.length() > 20)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "新密码长度必须在 6-20 位之间");
        }
        Long userId = StpBlogUtil.stpLogic.getLoginIdAsLong();
        User user = userService.selectUserById(userId);
        if (user == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (!SecurityUtils.matchesPassword(oldPassword, user.getPassword()))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, user.getPassword()))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新密码不能与旧密码相同");
        }
        if (userService.resetPassword(userId, newPassword) > 0)
        {
            return null;
        }
        throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改密码异常，请联系管理员");
    }

    @PostMapping("/user/avatar")
    public FrontAvatarUploadVO avatar(@RequestParam("file") MultipartFile file)
    {
        Long currentUserId = blogLoginAccessor.getCurrentUserId();
        if (currentUserId == null || currentUserId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        if (file == null || file.isEmpty())
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "上传文件不能为空");
        }
        ApiResponse<SysFile> uploadResult = remoteFileService.upload(file);
        if (uploadResult == null
            || uploadResult.getCode() == null
            || !ResultCode.SUCCESS.getCode().equals(uploadResult.getCode())
            || uploadResult.getData() == null
            || StringUtils.isEmpty(uploadResult.getData().getUrl()))
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "上传头像失败，请稍后重试");
        }
        User currentUser = userService.selectUserById(currentUserId);
        if (currentUser == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        currentUser.setAvatar(uploadResult.getData().getUrl());
        currentUser.setAvatarFileId(uploadResult.getData().getFileId());
        if (userService.updateUser(currentUser) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "头像保存失败");
        }
        return new FrontAvatarUploadVO(uploadResult.getData().getUrl(), uploadResult.getData().getFileId());
    }

    @GetMapping("/users/{id}")
    public FrontUserInfoVO detail(@PathVariable("id") Long id)
    {
        User user = userService.selectUserById(id);
        if (user == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        int articleCount = articleService.countArticleByUserId(id);
        int followerCount = followService.countFansByTargetUserId(id);
        user.setPassword(null);
        user.setArticleCount((long) articleCount);
        user.setFollowerCount((long) followerCount);

        return buildFrontUserPayload(user);
    }

    @GetMapping("/users/articles/{id}")
    public PageResult<ArticleVO> userArticles(@PathVariable("id") Long id,
                                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                              @RequestParam(value = "status", required = false) Integer status)
    {
        PageHelper.startPage(page, pageSize);
        Article filter = new Article();
        filter.setUserId(id);
        Long currentUserId = blogLoginAccessor.getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(id))
        {
            filter.setStatus(ArticleConstants.STATUS_PUBLISHED);
        }
        else if (status != null)
        {
            if (status == 0)
            {
                filter.getParams().put("excludePublished", Boolean.TRUE);
            }
            else if (status == 1 || status == 2 || status == 3)
            {
                filter.setStatus(Long.valueOf(status));
            }
            else
            {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "状态参数不合法");
            }
        }
        List<ArticleVO> rows = articleService.selectArticleVoList(filter);
        PageInfo<ArticleVO> pageInfo = new PageInfo<ArticleVO>(rows);
        return BlogPageResults.of(rows, pageInfo, page, pageSize);
    }

    @GetMapping("/users/followers/{id}")
    public PageResult<FrontFollowRelationVO> userFollowers(@PathVariable("id") Long id,
                                                           @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                           @RequestParam(value = "state", required = false) String state)
    {
        PageHelper.startPage(page, pageSize);
        Follow filter = new Follow();
        filter.setTargetUserId(id);
        filter.setDeleted(0);
        applyRelationStateFilter(state, filter);
        List<Follow> rows = followService.selectFansListWithUser(filter);
        List<FrontFollowRelationVO> normalizedRows = normalizeFollowRows(rows, true);
        PageInfo<Follow> pageInfo = new PageInfo<Follow>(rows);
        return BlogPageResults.of(normalizedRows, pageInfo, page, pageSize);
    }

    @GetMapping("/users/followings/{id}")
    public PageResult<FrontFollowRelationVO> userFollowings(@PathVariable("id") Long id,
                                                            @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                            @RequestParam(value = "state", required = false) String state)
    {
        PageHelper.startPage(page, pageSize);
        Follow filter = new Follow();
        filter.setUserId(id);
        filter.setDeleted(0);
        applyRelationStateFilter(state, filter);
        List<Follow> rows = followService.selectFollowListWithTargetUser(filter);
        List<FrontFollowRelationVO> normalizedRows = normalizeFollowRows(rows, false);
        PageInfo<Follow> pageInfo = new PageInfo<Follow>(rows);
        return BlogPageResults.of(normalizedRows, pageInfo, page, pageSize);
    }

    private void applyRelationStateFilter(String state, Follow filter)
    {
        if (state == null || state.length() == 0 || "all".equals(state))
        {
            return;
        }
        if ("active".equals(state) || "inactive".equals(state))
        {
            filter.getParams().put("relationState", state);
            return;
        }
        throw new BusinessException(ResultCode.VALIDATION_ERROR, "筛选参数不合法");
    }

    private FrontCurrentUserInfoVO buildCurrentUserPayload(User user)
    {
        FrontCurrentUserInfoVO payload = new FrontCurrentUserInfoVO();
        fillFrontUserPayload(payload, user);
        return payload;
    }

    private FrontUserInfoVO buildFrontUserPayload(User user)
    {
        FrontUserInfoVO payload = new FrontUserInfoVO();
        fillFrontUserPayload(payload, user);
        return payload;
    }

    private void fillFrontUserPayload(FrontUserInfoVO payload, User user)
    {
        payload.setId(user.getId());
        payload.setUserName(user.getUsername());
        payload.setNickName(user.getNickname());
        payload.setEmail(user.getEmail());
        payload.setPhonenumber(user.getPhone());
        payload.setSex(user.getSex());
        payload.setIntroduction(user.getIntroduction());
        payload.setAvatar(user.getAvatar());
        payload.setAvatarFileId(user.getAvatarFileId());
        payload.setArticleCount(user.getArticleCount());
        payload.setFollowerCount(user.getFollowerCount());
    }

    private List<FrontFollowRelationVO> normalizeFollowRows(List<Follow> rows, boolean followers)
    {
        List<FrontFollowRelationVO> normalized = new ArrayList<>();
        for (Follow row : rows)
        {
            FrontFollowRelationVO item = new FrontFollowRelationVO();
            item.setId(row.getId());
            item.setUserId(row.getUserId());
            item.setFollowUserId(row.getFollowUserId());
            item.setCreateTime(row.getCreateTime());
            if (followers)
            {
                item.setUser(row.getUser() == null ? null : buildFrontUserPayload(row.getUser()));
            }
            else
            {
                item.setTargetUser(row.getTargetUser() == null ? null : buildFrontUserPayload(row.getTargetUser()));
            }
            normalized.add(item);
        }
        return normalized;
    }

    @Data
    public static class ProfileUpdateBody
    {
        private String nickName;
        private String email;
        private String phonenumber;
        private Integer sex;
        private String introduction;
    }

    @Data
    public static class FrontFollowRelationVO
    {
        private Long id;
        private Long userId;
        private Long followUserId;
        private Date createTime;
        private FrontUserInfoVO user;
        private FrontUserInfoVO targetUser;
    }
}
