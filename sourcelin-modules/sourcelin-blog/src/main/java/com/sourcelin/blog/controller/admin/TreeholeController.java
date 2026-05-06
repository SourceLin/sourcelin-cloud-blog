package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.shared.support.BlogPageResults;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.Treehole;
import com.sourcelin.blog.service.ITreeholeService;
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
 * 树洞管理
 */
@RestController
@RequestMapping("/admin/treehole")
public class TreeholeController
{
    @Autowired
    private ITreeholeService treeholeService;

    @SaCheckPermission(type = "admin", value = "blog:treehole:list")
    @GetMapping("/list")
    public PageResult<Treehole> list(Treehole treehole)
    {
        // 后台管理列表默认不包含已删除记录
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize(), pageDomain.getOrderBy());
        List<Treehole> list = treeholeService.selectTreeholeList(treehole);
        PageInfo<Treehole> pageInfo = new PageInfo<Treehole>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    @SaCheckPermission(type = "admin", value = "blog:treehole:query")
    @GetMapping(value = "/{id}")
    public Treehole getInfo(@PathVariable("id") Long id)
    {
        Treehole treehole = treeholeService.selectTreeholeById(id);
        if (treehole == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "树洞不存在");
        }
        return treehole;
    }

    @SaCheckPermission(type = "admin", value = "blog:treehole:add")
    @Log(title = "树洞", businessType = BusinessType.INSERT)
    @PostMapping
    public Void add(@RequestBody Treehole treehole)
    {
        if (treehole.getDeleted() == null)
        {
            treehole.setDeleted(0L);
        }
        if (treehole.getStatus() == null)
        {
            treehole.setStatus(0L);
        }
        if (treeholeService.insertTreehole(treehole) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增失败");
        }
        return null;
    }

    @SaCheckPermission(type = "admin", value = "blog:treehole:edit")
    @Log(title = "树洞", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody Treehole treehole)
    {
        if (treeholeService.updateTreehole(treehole) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改失败");
        }
        return null;
    }

    @SaCheckPermission(type = "admin", value = "blog:treehole:remove")
    @Log(title = "树洞", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (treeholeService.deleteTreeholeByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除失败");
        }
        return null;
    }
}
