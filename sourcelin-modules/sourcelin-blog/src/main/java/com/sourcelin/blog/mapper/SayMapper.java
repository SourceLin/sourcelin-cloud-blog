package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.Say;

import java.util.List;

/**
 * 说说Mapper接口
 * 
 * @author sourcelin
 * @date 2026-03-21
 */
public interface SayMapper 
{
    /**
     * 查询说说
     * 
     * @param id 说说主键
     * @return 说说
     */
    public Say selectSayById(Long id);

    /**
     * 查询说说列表
     * 
     * @param say 说说
     * @return 说说集合
     */
    public List<Say> selectSayList(Say say);

    /**
     * 新增说说
     * 
     * @param say 说说
     * @return 结果
     */
    public int insertSay(Say say);

    /**
     * 修改说说
     * 
     * @param say 说说
     * @return 结果
     */
    public int updateSay(Say say);

    /**
     * 删除说说
     * 
     * @param id 说说主键
     * @return 结果
     */
    public int deleteSayById(Long id);

    /**
     * 批量删除说说
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSayByIds(Long[] ids);

    /**
     * 增加点赞数
     * 
     * @param id 说说主键
     * @return 结果
     */
    public int updateLikeCount(Long id);

    /**
     * 减少点赞数
     *
     * @param id 说说主键
     * @return 结果
     */
    public int updateLikeCountDecrease(Long id);

    /**
     * 增加评论数
     * 
     * @param id 说说主键
     * @return 结果
     */
    public int updateCommentCount(Long id);

    /**
     * 减少评论数
     *
     * @param id 说说主键
     * @return 结果
     */
    public int updateCommentCountDecrease(Long id);

    /**
     * 增加收藏数
     *
     * @param id 说说主键
     * @return 结果
     */
    public int updateCollectCountIncrease(Long id);

    /**
     * 减少收藏数
     *
     * @param id 说说主键
     * @return 结果
     */
    public int updateCollectCountDecrease(Long id);
}
