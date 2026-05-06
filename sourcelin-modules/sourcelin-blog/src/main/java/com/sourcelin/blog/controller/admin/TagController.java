package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.shared.support.BlogPageResults;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.Tag;
import com.sourcelin.blog.service.ITagService;
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

/**
 * 博客标签 Controller。
 */
@RestController
@RequestMapping("/admin/tag")
public class TagController
{
    @Autowired
    private ITagService tagService;

    /**
     * 查询博客标签列表。
     */
    @SaCheckPermission(type = "admin", value = "blog:tag:list")
    @GetMapping("/list")
    public PageResult<Tag> list(Tag tag)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize(), pageDomain.getOrderBy());
        List<Tag> list = tagService.selectTagList(tag);
        PageInfo<Tag> pageInfo = new PageInfo<Tag>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    /**
     * 获取博客标签详细信息。
     */
    @SaCheckPermission(type = "admin", value = "blog:tag:query")
    @GetMapping(value = "/{id}")
    public Tag getInfo(@PathVariable("id") Long id)
    {
        Tag tag = tagService.selectTagById(id);
        if (tag == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "标签不存在");
        }
        return tag;
    }

    /**
     * 新增博客标签。
     */
    @SaCheckPermission(type = "admin", value = "blog:tag:add")
    @Log(title = "博客标签", businessType = BusinessType.INSERT)
    @PostMapping
    public Void add(@RequestBody Tag tag)
    {
        if (tagService.insertTag(tag) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增失败");
        }
        return null;
    }

    /**
     * 修改博客标签。
     */
    @SaCheckPermission(type = "admin", value = "blog:tag:edit")
    @Log(title = "博客标签", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody Tag tag)
    {
        if (tagService.updateTag(tag) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改失败");
        }
        return null;
    }

    /**
     * 删除博客标签。
     */
    @SaCheckPermission(type = "admin", value = "blog:tag:remove")
    @Log(title = "博客标签", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (tagService.deleteTagByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除失败");
        }
        return null;
    }
}
