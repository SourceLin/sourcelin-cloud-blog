package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.Follow;
import com.sourcelin.blog.mapper.FollowMapper;
import com.sourcelin.blog.service.IFollowService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 关注Service业务层处理
 * 
 * @author sourcelin
 * @date 2026-01-18
 */
@Service
public class FollowServiceImpl implements IFollowService 
{
    @Autowired
    private FollowMapper followMapper;

    /**
     * 查询关注
     * 
     * @param id 关注主键
     * @return 关注
     */
    @Override
    public Follow selectFollowById(Long id)
    {
        return followMapper.selectFollowById(id);
    }

    /**
     * 查询关注列表
     * 
     * @param follow 关注
     * @return 关注
     */
    @Override
    public List<Follow> selectFollowList(Follow follow)
    {
        return followMapper.selectFollowList(follow);
    }

    /**
     * 查询关注列表（带目标用户信息）
     *
     * @param follow 关注
     * @return 关注集合
     */
    @Override
    public List<Follow> selectFollowListWithTargetUser(Follow follow)
    {
        return followMapper.selectFollowListWithTargetUser(follow);
    }

    /**
     * 查询粉丝列表（带用户信息）
     *
     * @param follow 关注
     * @return 粉丝集合
     */
    @Override
    public List<Follow> selectFansListWithUser(Follow follow)
    {
        return followMapper.selectFansListWithUser(follow);
    }

    @Override
    public Follow selectFollowByUserAndTarget(Long userId, Long followUserId)
    {
        return followMapper.selectFollowByUserAndTarget(userId, followUserId);
    }

    @Override
    public int countFollowByUserId(Long userId)
    {
        return followMapper.countFollowByUserId(userId);
    }

    @Override
    public int countFansByTargetUserId(Long targetUserId)
    {
        return followMapper.countFansByTargetUserId(targetUserId);
    }

    /**
     * 新增关注
     * 
     * @param follow 关注
     * @return 结果
     */
    @Override
    public int insertFollow(Follow follow)
    {
        if (follow.getDeleted() == null)
        {
            follow.setDeleted(0);
        }
        follow.setCreateTime(DateUtils.getNowDate());
        return followMapper.insertFollow(follow);
    }

    /**
     * 修改关注
     * 
     * @param follow 关注
     * @return 结果
     */
    @Override
    public int updateFollow(Follow follow)
    {
        return followMapper.updateFollow(follow);
    }

    /**
     * 批量删除关注
     * 
     * @param ids 需要删除的关注主键
     * @return 结果
     */
    @Override
    public int deleteFollowByIds(Long[] ids)
    {
        return followMapper.deleteFollowByIds(ids);
    }

    /**
     * 删除关注信息
     * 
     * @param id 关注主键
     * @return 结果
     */
    @Override
    public int deleteFollowById(Long id)
    {
        return followMapper.deleteFollowById(id);
    }
}
