package com.sourcelin.blog.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.ai.api.domain.*;
import com.sourcelin.ai.api.service.RemoteAiService;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.*;
import com.sourcelin.blog.service.BlogAiAdminService;
import com.sourcelin.ai.api.domain.AiAbilityInvokeResult;
import com.sourcelin.ai.api.domain.AiTaskCreateRequest;
import com.sourcelin.ai.api.domain.AiTaskStatusVO;
import com.sourcelin.ai.api.service.RemoteAiService;
import com.sourcelin.blog.domain.BlogAiRecord;
import com.sourcelin.blog.service.IBlogAiService;
import com.sourcelin.blog.vo.AiStatusVO;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.common.core.web.domain.response.ListResult;
import com.sourcelin.common.security.stp.StpAdminUtil;
import com.sourcelin.blog.config.BlogAiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 博客 AI 管理控制器
 */
@RestController
@RequestMapping("/admin/blog/ai")
public class BlogAiController extends com.sourcelin.common.core.web.controller.BaseController {

    @Autowired
    private IBlogAiService blogAiService;

    @Autowired
    private BlogAiProperties blogAiProperties;

    @Autowired
    private BlogAiAdminService adminService;

    /**
     * 获取 AI 功能状态
     */
    @GetMapping("/status")
    public AiStatusVO getStatus() {
        return new AiStatusVO(blogAiProperties.isEnabled());
    }

    /**
     * 同步调用 AI 能力
     */
    @SaCheckPermission(type = "admin", value = "blog:article:edit")
    @PostMapping("/abilities/{abilityCode}/invoke")
    public AiAbilityInvokeResult invoke(
            @PathVariable("abilityCode") String abilityCode,
            @RequestParam(value = "bizId", required = false) String bizId,
            @RequestBody Map<String, Object> input) {
        String userId = "admin:" + StpAdminUtil.stpLogic.getLoginIdAsString();
        return blogAiService.invokeAbility(abilityCode, bizId, input, userId);
    }

    /**
     * 创建 AI 异步任务（如封面生成）
     */
    @SaCheckPermission(type = "admin", value = "blog:article:edit")
    @PostMapping("/tasks")
    public AiTaskStatusVO createTask(@RequestBody AiTaskCreateRequest request) {
        String userId = "admin:" + StpAdminUtil.stpLogic.getLoginIdAsString();
        return blogAiService.createAsyncTask(request, userId);
    }

    /**
     * 查询 AI 异步任务状态
     */
    @SaCheckPermission(type = "admin", value = "blog:article:edit")
    @GetMapping("/tasks/{taskId}")
    public AiTaskStatusVO getTaskStatus(@PathVariable("taskId") String taskId) {
        return blogAiService.getAsyncTaskStatus(taskId, "admin:" + StpAdminUtil.stpLogic.getLoginIdAsString());
    }

    /**
     * 采纳 AI 结果并回写到正式字段
     */
    @SaCheckPermission(type = "admin", value = "blog:article:edit")
    @PostMapping("/records/{recordId}/adopt")
    public Boolean adopt(@PathVariable("recordId") Long recordId) {
        String userId = "admin:" + StpAdminUtil.stpLogic.getLoginIdAsString();
        return blogAiService.adoptResult(recordId, userId);
    }

    /**
     * 忽略/放弃 AI 结果
     */
    @SaCheckPermission(type = "admin", value = "blog:article:edit")
    @PostMapping("/records/{recordId}/discard")
    public Boolean discard(@PathVariable("recordId") Long recordId) {
        return blogAiService.discardResult(recordId, "admin:" + StpAdminUtil.stpLogic.getLoginIdAsString());
    }

    /**
     * 获取 AI 调用采纳记录列表
     */
    @SaCheckPermission(type = "admin", value = "blog:article:list")
    @GetMapping("/records")
    public PageResult<BlogAiRecord> list(BlogAiRecord query) {
        startPage();
        List<BlogAiRecord> records = blogAiService.selectRecordList(query);
        return toPageResult(records);
    }

    // ---- AI 能力配置管理（代理 sourcelin-ai） ----

    @SaCheckPermission(type = "admin", value = "blog:ai:config")
    @GetMapping("/ability-configs/provider-types")
    public ListResult<AiProviderOptionVO> getProviderTypes() {
        return adminService.getProviderTypes();
    }

    @SaCheckPermission(type = "admin", value = "blog:ai:config")
    @GetMapping("/ability-configs")
    public PageResult<AiAbilityConfigDTO> getAbilityConfigList(AiAdminPageQuery query) {
        return adminService.getAbilityConfigList(query);
    }

    @SaCheckPermission(type = "admin", value = "blog:ai:config")
    @PostMapping("/ability-configs")
    public Integer createAbilityConfig(@RequestBody AiAbilityConfigDTO config) {
        return adminService.createAbilityConfig(config);
    }

    @SaCheckPermission(type = "admin", value = "blog:ai:config")
    @PutMapping("/ability-configs")
    public Integer updateAbilityConfig(@RequestBody AiAbilityConfigDTO config) {
        return adminService.updateAbilityConfig(config);
    }

    @SaCheckPermission(type = "admin", value = "blog:ai:config")
    @DeleteMapping("/ability-configs/{id}")
    public Integer deleteAbilityConfig(@PathVariable("id") Long id) {
        return adminService.deleteAbilityConfig(id);
    }

    // ---- AI 调用日志（代理 sourcelin-ai） ----

    @SaCheckPermission(type = "admin", value = "blog:ai:log")
    @GetMapping("/call-logs")
    public PageResult<AiCallLogDTO> getCallLogList(AiAdminPageQuery query) {
        return adminService.getCallLogList(query);
    }

    // ---- AI 异步任务（代理 sourcelin-ai） ----

    @SaCheckPermission(type = "admin", value = "blog:ai:task")
    @GetMapping("/tasks")
    public PageResult<AiTaskDTO> getTaskList(AiAdminPageQuery query) {
        return adminService.getTaskList(query);
    }

    @SaCheckPermission(type = "admin", value = "blog:ai:task")
    @GetMapping("/tasks/{taskId}/events")
    public ListResult<AiTaskEventDTO> getTaskEvents(@PathVariable("taskId") String taskId) {
        return adminService.getTaskEvents(taskId);
    }

    @SaCheckPermission(type = "admin", value = "blog:ai:task")
    @PostMapping("/tasks/{taskId}/retry")
    public String retryTask(@PathVariable("taskId") String taskId) {
        return adminService.retryTask(taskId);
    }
}
