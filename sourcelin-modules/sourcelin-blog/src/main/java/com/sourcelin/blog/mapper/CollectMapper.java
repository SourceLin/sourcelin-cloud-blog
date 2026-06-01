package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.Collect;

import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 收藏Mapper接口
 * 
 * @author sourcelin
 * @date 2026-01-18
 */
public interface CollectMapper 
{
    /**
     * 查询收藏
     * 
     * @param id 收藏主键
     * @return 收藏
     */
    public Collect selectCollectById(Long id);

    /**
     * 查询收藏列表
     * 
     * @param collect 收藏
     * @return 收藏集合
     */
    public List<Collect> selectCollectList(Collect collect);

    /**
     * 查询收藏列表（带文章详情）
     *
     * @param collect 收藏
     * @return 收藏集合
     */
    public List<Collect> selectCollectListWithArticle(Collect collect);

    /**
     * 根据用户与目标查询收藏
     *
     * @param userId 用户ID
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 收藏记录
     */
    public Collect selectCollectByUserAndTarget(@Param("userId") Long userId,
                                                @Param("targetType") String targetType,
                                                @Param("targetId") Long targetId);

    /**
     * 统计用户收藏数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    public int countCollectByUserId(@Param("userId") Long userId);

    /**
     * 新增收藏
     * 
     * @param collect 收藏
     * @return 结果
     */
    public int insertCollect(Collect collect);

    /**
     * 修改收藏
     * 
     * @param collect 收藏
     * @return 结果
     */
    public int updateCollect(Collect collect);

    /**
     * 统计有效收藏总量
     *
     * @return 数量
     */
    public long countActiveCollectRecords();

    /**
     * 统计今日发生收藏行为的用户数
     *
     * @return 数量
     */
    public long countTodayActiveCollectUsers();

    /**
     * 删除收藏
     * 
     * @param id 收藏主键
     * @return 结果
     */
    public int deleteCollectById(Long id);

    /**
     * 批量删除收藏
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCollectByIds(Long[] ids);
}
