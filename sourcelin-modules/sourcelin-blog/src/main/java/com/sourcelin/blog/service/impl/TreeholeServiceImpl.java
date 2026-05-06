package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.Treehole;
import com.sourcelin.blog.mapper.TreeholeMapper;
import com.sourcelin.blog.service.ITreeholeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 树洞Service业务层处理
 *
 * @author sourcelin
 * @date 2026-03-21
 */
@Service
public class TreeholeServiceImpl implements ITreeholeService
{
    @Autowired
    private TreeholeMapper treeholeMapper;

    /**
     * 查询树洞
     *
     * @param id 树洞主键
     * @return 树洞
     */
    @Override
    public Treehole selectTreeholeById(Long id)
    {
        return treeholeMapper.selectTreeholeById(id);
    }

    /**
     * 查询树洞列表
     *
     * @param treehole 树洞
     * @return 树洞
     */
    @Override
    public List<Treehole> selectTreeholeList(Treehole treehole)
    {
        return treeholeMapper.selectTreeholeList(treehole);
    }

    /**
     * 新增树洞
     *
     * @param treehole 树洞
     * @return 结果
     */
    @Override
    public int insertTreehole(Treehole treehole)
    {
        return treeholeMapper.insertTreehole(treehole);
    }

    /**
     * 修改树洞
     *
     * @param treehole 树洞
     * @return 结果
     */
    @Override
    public int updateTreehole(Treehole treehole)
    {
        return treeholeMapper.updateTreehole(treehole);
    }

    /**
     * 批量删除树洞
     *
     * @param ids 需要删除的树洞主键集合
     * @return 结果
     */
    @Override
    public int deleteTreeholeByIds(Long[] ids)
    {
        return treeholeMapper.deleteTreeholeByIds(ids);
    }

    /**
     * 删除树洞信息
     *
     * @param id 树洞主键
     * @return 结果
     */
    @Override
    public int deleteTreeholeById(Long id)
    {
        return treeholeMapper.deleteTreeholeById(id);
    }

    /**
     * 增加点赞数
     *
     * @param id 树洞主键
     * @return 结果
     */
    @Override
    public int updateLikeCount(Long id)
    {
        return treeholeMapper.updateLikeCount(id);
    }

    @Override
    public int updateLikeCountDecrease(Long id)
    {
        return treeholeMapper.updateLikeCountDecrease(id);
    }

    /**
     * 增加评论数
     *
     * @param id 树洞主键
     * @return 结果
     */
    @Override
    public int updateCommentCount(Long id)
    {
        return treeholeMapper.updateCommentCount(id);
    }

    /**
     * 减少评论数
     *
     * @param id 树洞主键
     * @return 结果
     */
    @Override
    public int updateCommentCountDecrease(Long id)
    {
        return treeholeMapper.updateCommentCountDecrease(id);
    }

    @Override
    public int updateCollectCountIncrease(Long id)
    {
        return treeholeMapper.updateCollectCountIncrease(id);
    }

    @Override
    public int updateCollectCountDecrease(Long id)
    {
        return treeholeMapper.updateCollectCountDecrease(id);
    }
}
