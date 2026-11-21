package com.sourcelin.message.api.service;

import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.constant.ServiceNameConstants;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.message.api.factory.RemoteMessageFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
    contextId = "remoteMessageService",
    value = ServiceNameConstants.MESSAGE_SERVICE,
    fallbackFactory = RemoteMessageFallbackFactory.class
)
public interface RemoteMessageService {

    /**
     * 向指定用户推送实时消息
     *
     * @param userId   用户ID
     * @param content  消息内容
     * @param metadata 附加元数据（如 JSON 对象描述多媒体类型或进度信息）
     * @param source   内部请求来源标识
     * @return 返回统一的响应封装 ApiResponse<Void>
     */
    @PostMapping("/inner/message/push")
    ApiResponse<Void> pushMessage(
        @RequestParam("userId") String userId,
        @RequestParam("content") String content,
        @RequestParam("metadata") String metadata,
        @RequestHeader(SecurityConstants.FROM_SOURCE) String source
    );
}
