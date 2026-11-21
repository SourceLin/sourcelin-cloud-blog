package com.sourcelin.ai.provider;

import com.sourcelin.ai.domain.AiAbilityConfig;
import com.sourcelin.ai.domain.ProviderTypeEnum;
import com.sourcelin.ai.provider.common.AiStreamResultCallback;
import java.util.Map;

public interface AiProviderClient {
    /**
     * 判断当前客户端是否支持对应的平台类型
     */
    boolean supports(ProviderTypeEnum providerType);

    /**
     * 同步调用能力
     */
    String invoke(AiAbilityConfig config, Map<String, Object> inputs, String userId) throws Exception;

    /**
     * 流式调用能力 (默认抛出不支持异常，供客户端平滑升级)
     */
    default void invokeStream(AiAbilityConfig config, Map<String, Object> inputs,
                              org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter,
                              String bizId, String requestId,
                              AiStreamResultCallback callback) throws Exception {
        throw new UnsupportedOperationException("Streaming is not supported by " + this.getClass().getSimpleName());
    }
}
