package com.sourcelin.system.controller;

import java.util.List;

import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.security.annotation.InnerAuth;
import com.sourcelin.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.system.domain.SysNotice;

/**
 * 公告 信息操作处理
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/notice")
public class SysNoticeController extends BaseController
{
    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 获取通知公告列表
     */
    @SaCheckPermission(type = "admin", value = "system:notice:list")
    @GetMapping("/list")
    public PageResult<SysNotice> list(SysNotice notice)
    {
        startPage();
        List<SysNotice> list = noticeService.selectNoticeList(notice);
        return toPageResult(list);
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @SaCheckPermission(type = "admin", value = "system:notice:query")
    @GetMapping(value = "/{noticeId}")
    public SysNotice getInfo(@PathVariable Long noticeId)
    {
        return noticeService.selectNoticeById(noticeId);
    }

    /**
     * 新增通知公告
     */
    @SaCheckPermission(type = "admin", value = "system:notice:add")
    @Log(title = "通知公告", businessType = BusinessType.INSERT)
    @PostMapping
    public Boolean add(@Validated @RequestBody SysNotice notice)
    {
        notice.setCreateBy(adminLoginAccessor.getUsername());
        if (noticeService.insertNotice(notice) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增公告失败");
        }
        return true;
    }

    /**
     * 修改通知公告
     */
    @SaCheckPermission(type = "admin", value = "system:notice:edit")
    @Log(title = "通知公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean edit(@Validated @RequestBody SysNotice notice)
    {
        notice.setUpdateBy(adminLoginAccessor.getUsername());
        if (noticeService.updateNotice(notice) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改公告失败");
        }
        return true;
    }

    /**
     * 删除通知公告
     */
    @SaCheckPermission(type = "admin", value = "system:notice:remove")
    @Log(title = "通知公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{noticeIds}")
    public Boolean remove(@PathVariable Long[] noticeIds)
    {
        if (noticeService.deleteNoticeByIds(noticeIds) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除公告失败");
        }
        return true;
    }

    /**
     * 内部接口：已发布公告列表（供博客等微服务 Feign 调用）
     */
    @InnerAuth
    @GetMapping("/inner/published")
    public R<List<SysNotice>> listPublishedInner(@RequestHeader(SecurityConstants.FROM_SOURCE) String source)
    {
        SysNotice query = new SysNotice();
        query.setStatus("0");
        List<SysNotice> list = noticeService.selectNoticeList(query);
        return R.ok(list);
    }

    /**
     * 内部接口：已发布公告详情
     */
    @InnerAuth
    @GetMapping("/inner/{noticeId}")
    public R<SysNotice> getPublishedInner(@PathVariable Long noticeId,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source)
    {
        SysNotice n = noticeService.selectNoticeById(noticeId);
        if (n == null || !"0".equals(n.getStatus()))
        {
            return R.fail("公告不存在或已关闭");
        }
        return R.ok(n);
    }
}
