package com.sourcelin.ai.mapper;

import com.sourcelin.ai.domain.AiTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AiTaskMapper {
    int insert(AiTask task);
    int claimPending(String taskId);
    int updateByTaskId(AiTask task);
    int resetFailedTask(@Param("taskId") String taskId, @Param("retryCount") int retryCount);
    AiTask selectByTaskId(@Param("taskId") String taskId);
    List<AiTask> selectList(AiTask query);
}
