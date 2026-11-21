package com.sourcelin.message.api.factory;

import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.message.api.service.RemoteMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoteMessageFallbackFactory implements FallbackFactory<RemoteMessageService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteMessageFallbackFactory.class);

    @Override
    public RemoteMessageService create(Throwable throwable) {
        log.error("Message service call failed: {}", throwable.getMessage());
        return new RemoteMessageService() {
            @Override
            public ApiResponse<Void> pushMessage(String userId, String content, String metadata, String source) {
                log.error("Failed to push message to user: {}, content: {}, error: {}", userId, content, throwable.getMessage());
                return ApiResponse.fail(ResultCode.REMOTE_SERVICE_ERROR, "消息服务暂不可用", null);
            }
        };
    }
}
