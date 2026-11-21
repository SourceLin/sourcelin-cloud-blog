package com.sourcelin.ai.feign;

import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.constant.ServiceNameConstants;
import com.sourcelin.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * 内部调用 Feign 客户端：用于向博客服务回写 AI 记录，解耦对 sourcelin-api-blog 依赖。
 */
@FeignClient(contextId = "remoteBlogAiCallbackService", value = ServiceNameConstants.BLOG_SERVICE)
public interface RemoteBlogAiCallbackService {

    /**
     * 内网调用：保存流式生成完毕的 AI 结果记录，并返回生成的记录 ID
     */
    @PostMapping("/inner/blog/ai/records/create")
    R<Long> createRecord(
            @RequestParam("abilityCode") String abilityCode,
            @RequestParam("bizId") String bizId,
            @RequestParam("aiResult") String aiResult,
            @RequestParam("ownerId") String ownerId,
            @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
