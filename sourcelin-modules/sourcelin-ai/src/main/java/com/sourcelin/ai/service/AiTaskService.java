package com.sourcelin.ai.service;

import com.sourcelin.ai.domain.AiTask;
import com.sourcelin.ai.domain.AiTaskEvent;
import java.util.List;

public interface AiTaskService {
    int insert(AiTask task);
    int updateByTaskId(AiTask task);
    int resetFailedTask(String taskId, int retryCount);
    AiTask selectByTaskId(String taskId);
    List<AiTask> selectList(AiTask query);

    int insertEvent(AiTaskEvent event);
    List<AiTaskEvent> selectEventsByTaskId(String taskId);
}
