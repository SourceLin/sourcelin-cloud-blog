package com.sourcelin.ai.controller;
import com.sourcelin.common.security.annotation.InnerAuth;

import com.sourcelin.ai.domain.AiCallLog;
import com.sourcelin.ai.service.AiCallLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI 调用日志查询 Controller（只读）
 */
@RestController
@RequestMapping("/call-logs")
public class AiCallLogController extends com.sourcelin.common.core.web.controller.BaseController {

    @Autowired
    private AiCallLogService aiCallLogService;

    /**
     * 分页查询调用日志
     */
    @InnerAuth
    @GetMapping
    public com.sourcelin.common.core.web.domain.response.PageResult<AiCallLog> list(AiCallLog query) {
        startPage(); return toPageResult(aiCallLogService.selectList(query));
    }
}
