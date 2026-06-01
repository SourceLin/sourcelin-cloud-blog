package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.LikeRecord;
import com.sourcelin.blog.dto.interaction.InteractionTargetCountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 点赞记录 Mapper。
 */
public interface LikeRecordMapper
{
    LikeRecord selectLikeRecordById(@Param("id") Long id);

    LikeRecord selectLikeByUserAndTarget(@Param("userId") Long userId,
                                         @Param("targetType") String targetType,
                                         @Param("targetId") Long targetId);

    LikeRecord selectAnyLikeByUserAndTarget(@Param("userId") Long userId,
                                            @Param("targetType") String targetType,
                                            @Param("targetId") Long targetId);

    int countActiveLikeByTarget(@Param("targetType") String targetType, @Param("targetId") Long targetId);

    List<InteractionTargetCountDTO> selectActiveLikeCountsByTargetIds(@Param("targetType") String targetType,
                                                                      @Param("targetIds") List<Long> targetIds);

    List<Long> selectActiveTargetIdsByUserAndType(@Param("userId") Long userId,
                                                  @Param("targetType") String targetType,
                                                  @Param("targetIds") List<Long> targetIds);

    int insertLikeRecord(LikeRecord record);

    int updateLikeRecord(LikeRecord record);

    int deleteLikeRecordByIds(@Param("ids") Long[] ids);

    List<LikeRecord> selectLikeRecordList(LikeRecord record);

    List<LikeRecord> selectLikeRecordListWithTarget(LikeRecord record);

    long countActiveLikeRecords();

    long countTodayActiveLikeUsers();
}
