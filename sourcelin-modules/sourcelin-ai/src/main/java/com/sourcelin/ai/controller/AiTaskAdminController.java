package com.sourcelin.ai.controller;
import com.sourcelin.common.security.annotation.InnerAuth;

import com.sourcelin.ai.domain.AiTask;
import com.sourcelin.ai.domain.AiTaskEvent;
import com.sourcelin.ai.service.AiAdapterService;
import com.sourcelin.ai.service.AiTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 异步任务管理 Controller
 */
@RestController
@RequestMapping("/admin/tasks")
public class AiTaskAdminController extends com.sourcelin.common.core.web.controller.BaseController {

    @Autowired
    private AiTaskService aiTaskService;

    @Autowired
    private AiAdapterService aiAdapterService;

    /**
     * 分页查询异步任务
     */
    @InnerAuth
    @GetMapping
    public com.sourcelin.common.core.web.domain.response.PageResult<AiTask> list(AiTask query) {
        startPage(); return toPageResult(aiTaskService.selectList(query));
    }

    /**
     * 查询任务事件流水
     */
    @InnerAuth
    @GetMapping("/{taskId}/events")
    public com.sourcelin.common.core.web.domain.response.ListResult<AiTaskEvent> events(@PathVariable("taskId") String taskId) {
        return com.sourcelin.common.core.web.domain.response.ListResult.of(aiTaskService.selectEventsByTaskId(taskId));
    }

    /**
     * 重试失败任务
     */
    @InnerAuth
    @PostMapping("/{taskId}/retry")
    public String retry(@PathVariable("taskId") String taskId) {
        return aiAdapterService.retryTask(taskId);
    }
}
