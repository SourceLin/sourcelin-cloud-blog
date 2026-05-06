package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.Say;
import com.sourcelin.blog.mapper.SayMapper;
import com.sourcelin.blog.service.ISayService;
import com.sourcelin.blog.support.BlogFileCenterSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 说说Service业务层处理
 *
 * @author sourcelin
 * @date 2026-03-21
 */
@Service
public class SayServiceImpl implements ISayService
{
    @Autowired
    private SayMapper sayMapper;
    @Autowired
    private BlogFileCenterSupport blogFileCenterSupport;

    /**
     * 查询说说
     *
     * @param id 说说主键
     * @return 说说
     */
    @Override
    public Say selectSayById(Long id)
    {
        return sayMapper.selectSayById(id);
    }

    /**
     * 查询说说列表
     *
     * @param say 说说
     * @return 说说
     */
    @Override
    public List<Say> selectSayList(Say say)
    {
        return sayMapper.selectSayList(say);
    }

    /**
     * 新增说说
     *
     * @param say 说说
     * @return 结果
     */
    @Override
    public int insertSay(Say say)
    {
        int rows = sayMapper.insertSay(say);
        if (rows > 0 && say != null && say.getId() != null)
        {
            blogFileCenterSupport.confirmFiles(say.getImageFileIds(), "say", String.valueOf(say.getId()));
        }
        return rows;
    }

    /**
     * 修改说说
     *
     * @param say 说说
     * @return 结果
     */
    @Override
    public int updateSay(Say say)
    {
        int rows = sayMapper.updateSay(say);
        if (rows > 0 && say != null && say.getId() != null)
        {
            blogFileCenterSupport.confirmFiles(say.getImageFileIds(), "say", String.valueOf(say.getId()));
        }
        return rows;
    }

    /**
     * 批量删除说说
     *
     * @param ids 需要删除的说说主键集合
     * @return 结果
     */
    @Override
    public int deleteSayByIds(Long[] ids)
    {
        return sayMapper.deleteSayByIds(ids);
    }

    /**
     * 删除说说信息
     *
     * @param id 说说主键
     * @return 结果
     */
    @Override
    public int deleteSayById(Long id)
    {
        return sayMapper.deleteSayById(id);
    }

    /**
     * 增加点赞数
     *
     * @param id 说说主键
     * @return 结果
     */
    @Override
    public int updateLikeCount(Long id)
    {
        return sayMapper.updateLikeCount(id);
    }

    @Override
    public int updateLikeCountDecrease(Long id)
    {
        return sayMapper.updateLikeCountDecrease(id);
    }

    /**
     * 增加评论数
     *
     * @param id 说说主键
     * @return 结果
     */
    @Override
    public int updateCommentCount(Long id)
    {
        return sayMapper.updateCommentCount(id);
    }

    /**
     * 减少评论数
     *
     * @param id 说说主键
     * @return 结果
     */
    @Override
    public int updateCommentCountDecrease(Long id)
    {
        return sayMapper.updateCommentCountDecrease(id);
    }

    @Override
    public int updateCollectCountIncrease(Long id)
    {
        return sayMapper.updateCollectCountIncrease(id);
    }

    @Override
    public int updateCollectCountDecrease(Long id)
    {
        return sayMapper.updateCollectCountDecrease(id);
    }
}
