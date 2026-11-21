package com.sourcelin.blog.controller.front;

import com.sourcelin.ai.api.domain.AiAbilityInvokeResult;
import com.sourcelin.blog.vo.AiStatusVO;
import com.sourcelin.blog.config.BlogAiProperties;
import com.sourcelin.blog.service.IBlogAiService;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import com.sourcelin.ai.api.domain.AiTaskCreateRequest;
import com.sourcelin.ai.api.domain.AiTaskStatusVO;

/**
 * 博客前台 AI 状态控制器
 */
@RestController
@RequestMapping("/front/ai")
public class FrontAiController {

    @Autowired
    private BlogAiProperties blogAiProperties;

    @Autowired
    private IBlogAiService blogAiService;

    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    /**
     * 获取 AI 功能状态
     */
    @GetMapping("/status")
    public AiStatusVO getStatus() {
        return new AiStatusVO(blogAiProperties.isEnabled());
    }

    /**
     * 同步调用 AI 能力（前台）
     */
    @PostMapping("/abilities/{abilityCode}/invoke")
    public AiAbilityInvokeResult invoke(
            @PathVariable("abilityCode") String abilityCode,
            @RequestParam(value = "bizId", required = false) String bizId,
            @RequestBody Map<String, Object> input) {
        Long userId = blogLoginAccessor.getCurrentUserId();
        String userIdStr = userId != null && userId > 0 ? "blog:" + userId : "anonymous";
        return blogAiService.invokeAbility(abilityCode, bizId, input, userIdStr);
    }

    /**
     * 流式调用 AI 能力（前台 SSE）
     */
    @PostMapping(value = "/abilities/{abilityCode}/invoke/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter invokeStream(
            @PathVariable("abilityCode") String abilityCode,
            @RequestParam(value = "bizId", required = false) String bizId,
            @RequestBody Map<String, Object> input) {
        Long userId = blogLoginAccessor.getCurrentUserId();
        String userIdStr = userId != null && userId > 0 ? "blog:" + userId : "anonymous";
        return blogAiService.invokeStream(abilityCode, bizId, input, userIdStr);
    }

    /**
     * 创建 AI 异步任务（前台）
     */
    @PostMapping("/tasks")
    public AiTaskStatusVO createTask(@RequestBody AiTaskCreateRequest request) {
        Long userId = blogLoginAccessor.getCurrentUserId();
        String userIdStr = userId != null && userId > 0 ? "blog:" + userId : "anonymous";
        return blogAiService.createAsyncTask(request, userIdStr);
    }

    /**
     * 查询 AI 异步任务状态（前台）
     */
    @GetMapping("/tasks/{taskId}")
    public AiTaskStatusVO getTaskStatus(@PathVariable("taskId") String taskId) {
        return blogAiService.getAsyncTaskStatus(taskId, principal());
    }

    /**
     * 采纳 AI 结果（前台）
     */
    @PostMapping("/records/{recordId}/adopt")
    public Boolean adopt(@PathVariable("recordId") Long recordId) {
        Long userId = blogLoginAccessor.getCurrentUserId();
        String userIdStr = userId != null && userId > 0 ? "blog:" + userId : "anonymous";
        return blogAiService.adoptResult(recordId, userIdStr);
    }
    private String principal() {
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId == null) throw new com.sourcelin.common.core.exception.BusinessException(com.sourcelin.common.core.enums.ResultCode.UNAUTHORIZED);
        return "blog:" + userId;
    }
}
