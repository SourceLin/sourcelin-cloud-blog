package com.sourcelin.blog.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.blog.domain.Announcement;
import com.sourcelin.blog.service.IAnnouncementService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/announcement")
public class BlogAnnouncementController extends BaseController
{
    @Autowired
    private IAnnouncementService announcementService;

    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @SaCheckPermission(type = "admin", value = "blog:announcement:list")
    @GetMapping("/list")
    public PageResult<Announcement> list(Announcement announcement)
    {
        startPage();
        List<Announcement> list = announcementService.selectAnnouncementList(announcement);
        return toPageResult(list);
    }

    @SaCheckPermission(type = "admin", value = "blog:announcement:query")
    @GetMapping("/{id}")
    public Announcement getInfo(@PathVariable("id") Long id)
    {
        Announcement announcement = announcementService.selectAnnouncementById(id);
        if (announcement == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "公告不存在");
        }
        return announcement;
    }

    @SaCheckPermission(type = "admin", value = "blog:announcement:add")
    @Log(title = "博客公告", businessType = BusinessType.INSERT)
    @PostMapping
    public Boolean add(@RequestBody Announcement announcement)
    {
        announcement.setCreateBy(adminLoginAccessor.getUsername());
        if (announcement.getPublishStatus() == null || announcement.getPublishStatus().isEmpty())
        {
            announcement.setPublishStatus("0");
        }
        if (announcement.getDeleted() == null)
        {
            announcement.setDeleted(0);
        }
        if (announcementService.insertAnnouncement(announcement) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增公告失败");
        }
        return true;
    }

    @SaCheckPermission(type = "admin", value = "blog:announcement:edit")
    @Log(title = "博客公告", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean edit(@RequestBody Announcement announcement)
    {
        announcement.setUpdateBy(adminLoginAccessor.getUsername());
        if (announcementService.updateAnnouncement(announcement) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改公告失败");
        }
        return true;
    }

    @SaCheckPermission(type = "admin", value = "blog:announcement:publish")
    @Log(title = "博客公告", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/publish")
    public Boolean publish(@PathVariable("id") Long id)
    {
        if (announcementService.publishAnnouncement(id, adminLoginAccessor.getUsername()) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "发布公告失败");
        }
        return true;
    }

    @SaCheckPermission(type = "admin", value = "blog:announcement:offline")
    @Log(title = "博客公告", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/offline")
    public Boolean offline(@PathVariable("id") Long id)
    {
        if (announcementService.offlineAnnouncement(id, adminLoginAccessor.getUsername()) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "下线公告失败");
        }
        return true;
    }

    @SaCheckPermission(type = "admin", value = "blog:announcement:remove")
    @Log(title = "博客公告", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Boolean remove(@PathVariable Long[] ids)
    {
        if (announcementService.deleteAnnouncementByIds(ids, adminLoginAccessor.getUsername()) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除公告失败");
        }
        return true;
    }
}
