package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.sourcelin.blog.domain.FriendLink;
import com.sourcelin.blog.service.IFriendLinkService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 友链Controller
 * 
 * @author sourcelin
 * @date 2026-01-18
 */
@RestController
@RequestMapping("/admin/link")
public class FriendLinkController
{
    @Autowired
    private IFriendLinkService friendLinkService;

    /**
     * 查询友链列表
     */
    @SaCheckPermission(type = "admin", value = "blog:link:list")
    @GetMapping("/list")
    public PageResult<FriendLink> list(FriendLink friendLink)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize());
        List<FriendLink> list = friendLinkService.selectFriendLinkList(friendLink);
        PageInfo<FriendLink> pageInfo = new PageInfo<FriendLink>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    /**
     * 导出友链列表
     */
    @SaCheckPermission(type = "admin", value = "blog:link:export")
    @Log(title = "友链", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FriendLink friendLink)
    {
        List<FriendLink> list = friendLinkService.selectFriendLinkList(friendLink);
        ExcelUtil<FriendLink> util = new ExcelUtil<FriendLink>(FriendLink.class);
        util.exportExcel(response, list, "友链数据");
    }

    /**
     * 获取友链详细信息
     */
    @SaCheckPermission(type = "admin", value = "blog:link:query")
    @GetMapping(value = "/{id}")
    public FriendLink getInfo(@PathVariable("id") Long id)
    {
        FriendLink friendLink = friendLinkService.selectFriendLinkById(id);
        if (friendLink == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "友链不存在");
        }
        return friendLink;
    }

    /**
     * 新增友链
     */
    @SaCheckPermission(type = "admin", value = "blog:link:add")
    @Log(title = "友链", businessType = BusinessType.INSERT)
    @PostMapping
    public Void add(@RequestBody FriendLink friendLink)
    {
        // 友链名称和链接为必填字段，防止 Jackson 反序列化丢失导致 SQL 报错
        if (friendLink.getName() == null || friendLink.getName().isEmpty())
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "友链名称不能为空");
        }
        if (friendLink.getUrl() == null || friendLink.getUrl().isEmpty())
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "友链链接不能为空");
        }
        if (friendLinkService.insertFriendLink(friendLink) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "新增友链失败");
        }
        return null;
    }

    /**
     * 修改友链
     */
    @SaCheckPermission(type = "admin", value = "blog:link:edit")
    @Log(title = "友链", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody FriendLink friendLink)
    {
        if (friendLinkService.updateFriendLink(friendLink) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改友链失败");
        }
        return null;
    }

    /**
     * 审核/上下架（仅更新状态与原因，权限可与「编辑」分离）
     */
    @SaCheckPermission(type = "admin", value = "blog:link:examine")
    @Log(title = "友链", businessType = BusinessType.UPDATE)
    @PutMapping("/status")
    public Void changeStatus(@RequestBody FriendLink payload)
    {
        if (payload.getId() == null)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "友链ID不能为空");
        }
        FriendLink db = friendLinkService.selectFriendLinkById(payload.getId());
        if (db == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "友链不存在");
        }
        db.setStatus(payload.getStatus());
        if (payload.getReason() != null)
        {
            db.setReason(payload.getReason());
        }
        if (friendLinkService.updateFriendLink(db) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "更新友链状态失败");
        }
        return null;
    }

    /**
     * 删除友链
     */
    @SaCheckPermission(type = "admin", value = "blog:link:remove")
    @Log(title = "友链", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (friendLinkService.deleteFriendLinkByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "删除友链失败");
        }
        return null;
    }
}
