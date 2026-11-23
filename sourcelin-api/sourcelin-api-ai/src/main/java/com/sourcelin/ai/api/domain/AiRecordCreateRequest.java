package com.sourcelin.ai.api.domain;

/** AI 服务向博客服务归档生成结果的内部请求。 */
public record AiRecordCreateRequest(String abilityCode, String bizId, String aiResult, String ownerId) {
}
