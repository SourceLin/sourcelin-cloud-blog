package com.sourcelin.blog.service;

import com.sourcelin.blog.domain.Tag;

import java.util.List;

/**
 * 博客标签Service接口
 * 
 * @author sourcelin
 * @date 2023-11-07
 */
public interface ITagService 
{
    /**
     * 查询博客标签
     * 
     * @param id 博客标签主键
     * @return 博客标签
     */
    public Tag selectTagById(Long id);

    /**
     * 查询所有博客标签
     *
     * @return 博客标签列表
     */
    public List<Tag> selectTagAll();

    /**
     * 查询博客标签列表
     * 
     * @param tag 博客标签
     * @return 博客标签集合
     */
    public List<Tag> selectTagList(Tag tag);

    /**
     * 新增博客标签
     * 
     * @param tag 博客标签
     * @return 结果
     */
    public int insertTag(Tag tag);

    /**
     * 修改博客标签
     * 
     * @param tag 博客标签
     * @return 结果
     */
    public int updateTag(Tag tag);

    /**
     * 批量删除博客标签
     * 
     * @param ids 需要删除的博客标签主键集合
     * @return 结果
     */
    public int deleteTagByIds(Long[] ids);

    /**
     * 删除博客标签信息
     * 
     * @param id 博客标签主键
     * @return 结果
     */
    public int deleteTagById(Long id);
}
