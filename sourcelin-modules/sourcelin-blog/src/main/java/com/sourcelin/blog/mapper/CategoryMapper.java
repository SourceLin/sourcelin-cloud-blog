package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.Category;

import java.util.List;

/**
 * 博客分类Mapper接口
 * 
 * @author sourcelin
 * @date 2023-11-07
 */
public interface CategoryMapper 
{
    /**
     * 查询博客分类
     * 
     * @param id 博客分类主键
     * @return 博客分类
     */
    public Category selectCategoryById(Long id);

    /**
     * 查询所有博客分类
     *
     * @return 所有博客分类
     */
    public List<Category> selectCategoryAll();

    /**
     * 查询博客分类列表
     * 
     * @param category 博客分类
     * @return 博客分类集合
     */
    public List<Category> selectCategoryList(Category category);

    /**
     * 新增博客分类
     * 
     * @param category 博客分类
     * @return 结果
     */
    public int insertCategory(Category category);

    /**
     * 修改博客分类
     * 
     * @param category 博客分类
     * @return 结果
     */
    public int updateCategory(Category category);

    /**
     * 删除博客分类
     * 
     * @param id 博客分类主键
     * @return 结果
     */
    public int deleteCategoryById(Long id);

    /**
     * 批量删除博客分类
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCategoryByIds(Long[] ids);
}
