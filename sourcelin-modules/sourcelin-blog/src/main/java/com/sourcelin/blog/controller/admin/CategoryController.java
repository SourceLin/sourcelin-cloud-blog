package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.shared.support.BlogPageResults;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.Category;
import com.sourcelin.blog.service.ICategoryService;
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
 * 博客分类 Controller。
 */
@RestController
@RequestMapping("/admin/category")
public class CategoryController
{
    @Autowired
    private ICategoryService categoryService;

    /**
     * 查询博客分类列表。
     */
    @SaCheckPermission(type = "admin", value = "blog:category:list")
    @GetMapping("/list")
    public PageResult<Category> list(Category category)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize(), pageDomain.getOrderBy());
        List<Category> list = categoryService.selectCategoryList(category);
        PageInfo<Category> pageInfo = new PageInfo<Category>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    /**
     * 获取博客分类详细信息。
     */
    @SaCheckPermission(type = "admin", value = "blog:category:query")
    @GetMapping(value = "/{id}")
    public Category getInfo(@PathVariable("id") Long id)
    {
        Category category = categoryService.selectCategoryById(id);
        if (category == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }
        return category;
    }

    /**
     * 新增博客分类。
     */
    @SaCheckPermission(type = "admin", value = "blog:category:add")
    @Log(title = "博客分类", businessType = BusinessType.INSERT)
    @PostMapping
    public Void add(@RequestBody Category category)
    {
        if (categoryService.insertCategory(category) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增失败");
        }
        return null;
    }

    /**
     * 修改博客分类。
     */
    @SaCheckPermission(type = "admin", value = "blog:category:edit")
    @Log(title = "博客分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody Category category)
    {
        if (categoryService.updateCategory(category) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改失败");
        }
        return null;
    }

    /**
     * 删除博客分类。
     */
    @SaCheckPermission(type = "admin", value = "blog:category:remove")
    @Log(title = "博客分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (categoryService.deleteCategoryByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除失败");
        }
        return null;
    }
}
