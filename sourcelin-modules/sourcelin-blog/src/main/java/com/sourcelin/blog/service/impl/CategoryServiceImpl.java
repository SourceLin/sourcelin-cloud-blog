package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.Category;
import com.sourcelin.blog.mapper.CategoryMapper;
import com.sourcelin.blog.service.ICategoryService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 博客分类Service业务层处理
 * 
 * @author sourcelin
 * @date 2026-01-07
 */
@Service
public class CategoryServiceImpl implements ICategoryService 
{
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询博客分类
     * 
     * @param id 博客分类主键
     * @return 博客分类
     */
    @Override
    public Category selectCategoryById(Long id)
    {
        return categoryMapper.selectCategoryById(id);
    }

    /**
     * 查询所有博客分类
     *
     * @return 博客分类列表
     */
    @Override
    public List<Category> selectCategoryAll()
    {
        return categoryMapper.selectCategoryAll();
    }

    /**
     * 查询博客分类列表
     * 
     * @param category 博客分类
     * @return 博客分类
     */
    @Override
    public List<Category> selectCategoryList(Category category)
    {
        return categoryMapper.selectCategoryList(category);
    }

    /**
     * 新增博客分类
     * 
     * @param category 博客分类
     * @return 结果
     */
    @Override
    public int insertCategory(Category category)
    {
        category.setCreateTime(DateUtils.getNowDate());
        return categoryMapper.insertCategory(category);
    }

    /**
     * 修改博客分类
     * 
     * @param category 博客分类
     * @return 结果
     */
    @Override
    public int updateCategory(Category category)
    {
        category.setUpdateTime(DateUtils.getNowDate());
        return categoryMapper.updateCategory(category);
    }

    /**
     * 批量删除博客分类
     * 
     * @param ids 需要删除的博客分类主键
     * @return 结果
     */
    @Override
    public int deleteCategoryByIds(Long[] ids)
    {
        return categoryMapper.deleteCategoryByIds(ids);
    }

    /**
     * 删除博客分类信息
     * 
     * @param id 博客分类主键
     * @return 结果
     */
    @Override
    public int deleteCategoryById(Long id)
    {
        return categoryMapper.deleteCategoryById(id);
    }
}
