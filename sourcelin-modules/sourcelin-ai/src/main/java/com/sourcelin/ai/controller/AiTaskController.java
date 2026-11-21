package com.sourcelin.ai.controller;
import com.sourcelin.common.security.annotation.InnerAuth;

import com.sourcelin.ai.api.domain.AiTaskCreateRequest;
import com.sourcelin.ai.api.domain.AiTaskStatusVO;
import com.sourcelin.ai.service.AiAdapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class AiTaskController {

    @Autowired
    private AiAdapterService aiAdapterService;

    /**
     * 创建 AI 异步任务
     */
    @InnerAuth
    @PostMapping
    public AiTaskStatusVO createTask(@RequestBody AiTaskCreateRequest request) {
        return aiAdapterService.createTask(request);
    }

    /**
     * 获取 AI 异步任务状态
     */
    @InnerAuth
    @GetMapping("/{taskId}")
    public AiTaskStatusVO getTaskStatus(@PathVariable("taskId") String taskId) {
        return aiAdapterService.getTaskStatus(taskId);
    }
}
