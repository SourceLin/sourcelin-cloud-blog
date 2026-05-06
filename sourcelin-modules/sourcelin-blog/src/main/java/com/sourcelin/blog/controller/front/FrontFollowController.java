package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.Follow;
import com.sourcelin.blog.service.IFollowService;
import com.sourcelin.blog.vo.FrontFollowActionVO;
import com.sourcelin.common.core.utils.PageUtils;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Front follow endpoints.
 */
@RestController
@RequestMapping("/front/follows")
public class FrontFollowController
{
    @Autowired
    private IFollowService followService;
    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    @GetMapping("/following")
    public PageResult<Follow> following(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                        Long userId)
    {
        page = PageUtils.clampPage(page);
        pageSize = PageUtils.clampPageSize(pageSize);
        if (userId == null || userId <= 0)
        {
            Long currentUserId = blogLoginAccessor.getCurrentUserId();
            if (currentUserId != null && currentUserId > 0)
            {
                userId = currentUserId;
            }
        }
        if (userId == null || userId <= 0)
        {
            return PageResult.empty(page, pageSize);
        }
        PageHelper.startPage(page, pageSize);
        Follow filter = new Follow();
        filter.setUserId(userId);
        filter.setDeleted(0);
        List<Follow> follows = followService.selectFollowListWithTargetUser(filter);
        PageInfo<Follow> pageInfo = new PageInfo<Follow>(follows);
        return BlogPageResults.of(follows, pageInfo, page, pageSize);
    }

    @GetMapping("/fans")
    public PageResult<Follow> fans(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                   Long targetUserId)
    {
        page = PageUtils.clampPage(page);
        pageSize = PageUtils.clampPageSize(pageSize);
        if (targetUserId == null || targetUserId <= 0)
        {
            Long currentUserId = blogLoginAccessor.getCurrentUserId();
            if (currentUserId != null && currentUserId > 0)
            {
                targetUserId = currentUserId;
            }
        }
        if (targetUserId == null || targetUserId <= 0)
        {
            return PageResult.empty(page, pageSize);
        }
        PageHelper.startPage(page, pageSize);
        Follow filter = new Follow();
        filter.setTargetUserId(targetUserId);
        filter.setDeleted(0);
        List<Follow> fans = followService.selectFansListWithUser(filter);
        PageInfo<Follow> pageInfo = new PageInfo<Follow>(fans);
        return BlogPageResults.of(fans, pageInfo, page, pageSize);
    }

    @PostMapping
    public FrontFollowActionVO add(@RequestBody Follow follow)
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId == null || userId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        Long targetUserId = follow.getTargetUserId();
        if (targetUserId == null)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "关注用户不能为空");
        }
        if (targetUserId.equals(userId))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "不能关注自己");
        }
        Follow existing = followService.selectFollowByUserAndTarget(userId, targetUserId);
        if (existing != null)
        {
            return new FrontFollowActionVO(existing.getId(), true);
        }
        follow.setUserId(userId);
        follow.setFollowUserId(targetUserId);
        if (follow.getDeleted() == null)
        {
            follow.setDeleted(0);
        }
        int result = followService.insertFollow(follow);
        if (result <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "关注失败");
        }
        return new FrontFollowActionVO(follow.getId(), true);
    }

    @DeleteMapping("/{id}")
    public Void delete(@PathVariable("id") Long id)
    {
        Long currentUserId = blogLoginAccessor.getCurrentUserId();
        if (currentUserId == null || currentUserId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        Follow follow = followService.selectFollowById(id);
        if (follow == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "关注记录不存在");
        }
        if (!currentUserId.equals(follow.getUserId()) && !currentUserId.equals(follow.getFollowUserId()))
        {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权删除他人的关注记录");
        }
        int result = followService.deleteFollowById(id);
        if (result <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "取消关注失败");
        }
        return null;
    }
}
