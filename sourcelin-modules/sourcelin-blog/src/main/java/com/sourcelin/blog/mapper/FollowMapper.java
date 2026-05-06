package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.Follow;

import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 关注Mapper接口
 * 
 * @author sourcelin
 * @date 2023-11-18
 */
public interface FollowMapper 
{
    /**
     * 查询关注
     * 
     * @param id 关注主键
     * @return 关注
     */
    public Follow selectFollowById(Long id);

    /**
     * 查询关注列表
     * 
     * @param follow 关注
     * @return 关注集合
     */
    public List<Follow> selectFollowList(Follow follow);

    /**
     * 查询关注列表（带目标用户信息）
     *
     * @param follow 关注
     * @return 关注集合
     */
    public List<Follow> selectFollowListWithTargetUser(Follow follow);

    /**
     * 查询粉丝列表（带用户信息）
     *
     * @param follow 关注
     * @return 粉丝集合
     */
    public List<Follow> selectFansListWithUser(Follow follow);

    /**
     * 根据用户与目标用户查询关注关系
     *
     * @param userId 用户ID
     * @param followUserId 目标用户ID
     * @return 关注记录
     */
    public Follow selectFollowByUserAndTarget(@Param("userId") Long userId, @Param("followUserId") Long followUserId);

    /**
     * 统计用户关注数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    public int countFollowByUserId(@Param("userId") Long userId);

    /**
     * 统计粉丝数量
     *
     * @param targetUserId 目标用户ID
     * @return 数量
     */
    public int countFansByTargetUserId(@Param("targetUserId") Long targetUserId);

    /**
     * 新增关注
     * 
     * @param follow 关注
     * @return 结果
     */
    public int insertFollow(Follow follow);

    /**
     * 修改关注
     * 
     * @param follow 关注
     * @return 结果
     */
    public int updateFollow(Follow follow);

    /**
     * 删除关注
     * 
     * @param id 关注主键
     * @return 结果
     */
    public int deleteFollowById(Long id);

    /**
     * 批量删除关注
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFollowByIds(Long[] ids);
}
