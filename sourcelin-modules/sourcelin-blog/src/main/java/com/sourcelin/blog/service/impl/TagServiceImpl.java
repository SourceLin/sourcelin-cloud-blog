package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.Tag;
import com.sourcelin.blog.mapper.TagMapper;
import com.sourcelin.blog.service.ITagService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 博客标签Service业务层处理
 * 
 * @author sourcelin
 * @date 2026-01-07
 */
@Service
public class TagServiceImpl implements ITagService 
{
    @Autowired
    private TagMapper tagMapper;

    /**
     * 查询博客标签
     * 
     * @param id 博客标签主键
     * @return 博客标签
     */
    @Override
    public Tag selectTagById(Long id)
    {
        return tagMapper.selectTagById(id);
    }

    /**
     * 查询所有博客标签
     *
     * @return 博客标签列表
     */
    @Override
    public List<Tag> selectTagAll()
    {
        return tagMapper.selectTagAll();
    }

    /**
     * 查询博客标签列表
     * 
     * @param tag 博客标签
     * @return 博客标签
     */
    @Override
    public List<Tag> selectTagList(Tag tag)
    {
        return tagMapper.selectTagList(tag);
    }

    /**
     * 新增博客标签
     * 
     * @param tag 博客标签
     * @return 结果
     */
    @Override
    public int insertTag(Tag tag)
    {
        tag.setCreateTime(DateUtils.getNowDate());
        return tagMapper.insertTag(tag);
    }

    /**
     * 修改博客标签
     * 
     * @param tag 博客标签
     * @return 结果
     */
    @Override
    public int updateTag(Tag tag)
    {
        tag.setUpdateTime(DateUtils.getNowDate());
        return tagMapper.updateTag(tag);
    }

    /**
     * 批量删除博客标签
     * 
     * @param ids 需要删除的博客标签主键
     * @return 结果
     */
    @Override
    public int deleteTagByIds(Long[] ids)
    {
        return tagMapper.deleteTagByIds(ids);
    }

    /**
     * 删除博客标签信息
     * 
     * @param id 博客标签主键
     * @return 结果
     */
    @Override
    public int deleteTagById(Long id)
    {
        return tagMapper.deleteTagById(id);
    }
}
