package com.sourcelin.ai.mapper;

import com.sourcelin.ai.domain.AiTaskEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AiTaskEventMapper {
    int insert(AiTaskEvent event);
    List<AiTaskEvent> selectByTaskId(@Param("taskId") String taskId);
}
