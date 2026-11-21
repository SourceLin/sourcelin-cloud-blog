package com.sourcelin.ai.controller;
import com.sourcelin.common.security.annotation.InnerAuth;

import com.sourcelin.ai.api.domain.AiAbilityInvokeRequest;
import com.sourcelin.ai.api.domain.AiAbilityInvokeResult;
import com.sourcelin.ai.service.AiAdapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/abilities")
public class AiAbilityController {

    @Autowired
    private AiAdapterService aiAdapterService;

    /**
     * 同步调用 AI 能力
     */
    @InnerAuth
    @PostMapping("/{abilityCode}/invoke")
    public AiAbilityInvokeResult invoke(
            @PathVariable("abilityCode") String abilityCode,
            @RequestBody AiAbilityInvokeRequest request) {
        return aiAdapterService.invoke(abilityCode, request);
    }

    /**
     * 流式调用 AI 能力 (SSE)
     */
    @InnerAuth
    @PostMapping(value = "/{abilityCode}/invoke/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter invokeStream(
            @PathVariable("abilityCode") String abilityCode,
            @RequestBody AiAbilityInvokeRequest request) {
        return aiAdapterService.invokeStream(abilityCode, request);
    }
}
