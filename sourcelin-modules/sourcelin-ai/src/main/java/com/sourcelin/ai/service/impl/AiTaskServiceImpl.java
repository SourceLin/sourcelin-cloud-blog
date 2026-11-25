package com.sourcelin.ai.service.impl;

import com.sourcelin.ai.domain.AiTask;
import com.sourcelin.ai.domain.AiTaskEvent;
import com.sourcelin.ai.mapper.AiTaskEventMapper;
import com.sourcelin.ai.mapper.AiTaskMapper;
import com.sourcelin.ai.service.AiTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AiTaskServiceImpl implements AiTaskService {
    @Autowired
    private AiTaskMapper aiTaskMapper;

    @Autowired
    private AiTaskEventMapper aiTaskEventMapper;

    @Override
    public int claimPending(String taskId) {
        return aiTaskMapper.claimPending(taskId);
    }

    @Override
    public int insert(AiTask task) {
        return aiTaskMapper.insert(task);
    }

    @Override
    public int updateByTaskId(AiTask task) {
        return aiTaskMapper.updateByTaskId(task);
    }

    @Override
    public int resetFailedTask(String taskId, int retryCount) {
        return aiTaskMapper.resetFailedTask(taskId, retryCount);
    }

    @Override
    public AiTask selectByTaskId(String taskId) {
        return aiTaskMapper.selectByTaskId(taskId);
    }

    @Override
    public List<AiTask> selectList(AiTask query) {
        return aiTaskMapper.selectList(query);
    }

    @Override
    public int insertEvent(AiTaskEvent event) {
        return aiTaskEventMapper.insert(event);
    }

    @Override
    public List<AiTaskEvent> selectEventsByTaskId(String taskId) {
        return aiTaskEventMapper.selectByTaskId(taskId);
    }
}
