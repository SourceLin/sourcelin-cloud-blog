package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.shared.support.BlogPageResults;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.Follow;
import com.sourcelin.blog.service.IFollowService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/follow")
public class FollowController
{
    @Autowired
    private IFollowService followService;

    @SaCheckPermission(type = "admin", value = "blog:follow:list")
    @GetMapping("/list")
    public PageResult<Follow> list(@RequestParam(value = "userId", required = false) Long userId,
                                   @RequestParam(value = "targetUserId", required = false) Long targetUserId)
    {
        Follow filter = new Follow();
        filter.setDeleted(0);
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize(), pageDomain.getOrderBy());
        List<Follow> rows;
        if (targetUserId != null)
        {
            filter.setTargetUserId(targetUserId);
            rows = followService.selectFansListWithUser(filter);
        }
        else
        {
            filter.setUserId(userId);
            rows = followService.selectFollowListWithTargetUser(filter);
        }
        PageInfo<Follow> pageInfo = new PageInfo<Follow>(rows);
        return BlogPageResults.of(rows, pageInfo);
    }

    @SaCheckPermission(type = "admin", value = "blog:follow:query")
    @GetMapping("/{id}")
    public Follow getInfo(@PathVariable("id") Long id)
    {
        Follow follow = followService.selectFollowById(id);
        if (follow == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "关注记录不存在");
        }
        return follow;
    }

    @SaCheckPermission(type = "admin", value = "blog:follow:remove")
    @Log(title = "关注管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (followService.deleteFollowByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除失败");
        }
        return null;
    }
}
