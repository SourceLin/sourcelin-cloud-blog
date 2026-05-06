package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.mapper.CollectMapper;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收藏Service业务层处理
 * 
 * @author sourcelin
 * @date 2023-11-18
 */
@Service
public class CollectServiceImpl implements ICollectService 
{
    @Autowired
    private CollectMapper collectMapper;

    /**
     * 查询收藏
     * 
     * @param id 收藏主键
     * @return 收藏
     */
    @Override
    public Collect selectCollectById(Long id)
    {
        return collectMapper.selectCollectById(id);
    }

    /**
     * 查询收藏列表
     * 
     * @param collect 收藏
     * @return 收藏
     */
    @Override
    public List<Collect> selectCollectList(Collect collect)
    {
        return collectMapper.selectCollectList(collect);
    }

    /**
     * 查询收藏列表（带文章详情）
     *
     * @param collect 收藏
     * @return 收藏集合
     */
    @Override
    public List<Collect> selectCollectListWithArticle(Collect collect)
    {
        return collectMapper.selectCollectListWithArticle(collect);
    }

    @Override
    public Collect selectCollectByUserAndTarget(Long userId, String targetType, Long targetId)
    {
        return collectMapper.selectCollectByUserAndTarget(userId, targetType, targetId);
    }

    @Override
    public int countCollectByUserId(Long userId)
    {
        return collectMapper.countCollectByUserId(userId);
    }

    @Override
    public long countActiveCollectRecords()
    {
        return collectMapper.countActiveCollectRecords();
    }

    @Override
    public long countTodayActiveCollectUsers()
    {
        return collectMapper.countTodayActiveCollectUsers();
    }

    /**
     * 新增收藏
     * 
     * @param collect 收藏
     * @return 结果
     */
    @Override
    public int insertCollect(Collect collect)
    {
        if (collect.getDeleted() == null)
        {
            collect.setDeleted(0);
        }
        collect.setCreateTime(DateUtils.getNowDate());
        return collectMapper.insertCollect(collect);
    }

    /**
     * 修改收藏
     * 
     * @param collect 收藏
     * @return 结果
     */
    @Override
    public int updateCollect(Collect collect)
    {
        collect.setUpdateTime(DateUtils.getNowDate());
        return collectMapper.updateCollect(collect);
    }

    /**
     * 批量删除收藏
     * 
     * @param ids 需要删除的收藏主键
     * @return 结果
     */
    @Override
    public int deleteCollectByIds(Long[] ids)
    {
        return collectMapper.deleteCollectByIds(ids);
    }

    /**
     * 删除收藏信息
     * 
     * @param id 收藏主键
     * @return 结果
     */
    @Override
    public int deleteCollectById(Long id)
    {
        return collectMapper.deleteCollectById(id);
    }
}
