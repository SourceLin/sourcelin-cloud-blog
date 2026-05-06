package com.sourcelin.blog.service;

import com.sourcelin.blog.domain.Treehole;

import java.util.List;

/**
 * 树洞Service接口
 * 
 * @author sourcelin
 * @date 2026-03-21
 */
public interface ITreeholeService 
{
    /**
     * 查询树洞
     * 
     * @param id 树洞主键
     * @return 树洞
     */
    public Treehole selectTreeholeById(Long id);

    /**
     * 查询树洞列表
     * 
     * @param treehole 树洞
     * @return 树洞集合
     */
    public List<Treehole> selectTreeholeList(Treehole treehole);

    /**
     * 新增树洞
     * 
     * @param treehole 树洞
     * @return 结果
     */
    public int insertTreehole(Treehole treehole);

    /**
     * 修改树洞
     * 
     * @param treehole 树洞
     * @return 结果
     */
    public int updateTreehole(Treehole treehole);

    /**
     * 批量删除树洞
     * 
     * @param ids 需要删除的树洞主键集合
     * @return 结果
     */
    public int deleteTreeholeByIds(Long[] ids);

    /**
     * 删除树洞信息
     * 
     * @param id 树洞主键
     * @return 结果
     */
    public int deleteTreeholeById(Long id);

    /**
     * 增加点赞数
     * 
     * @param id 树洞主键
     * @return 结果
     */
    public int updateLikeCount(Long id);

    /**
     * 减少点赞数
     *
     * @param id 树洞主键
     * @return 结果
     */
    public int updateLikeCountDecrease(Long id);

    /**
     * 增加评论数
     * 
     * @param id 树洞主键
     * @return 结果
     */
    public int updateCommentCount(Long id);

    /**
     * 减少评论数
     *
     * @param id 树洞主键
     * @return 结果
     */
    public int updateCommentCountDecrease(Long id);

    /**
     * 增加收藏数
     *
     * @param id 树洞主键
     * @return 结果
     */
    public int updateCollectCountIncrease(Long id);

    /**
     * 减少收藏数
     *
     * @param id 树洞主键
     * @return 结果
     */
    public int updateCollectCountDecrease(Long id);
}
