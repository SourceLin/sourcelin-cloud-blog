package com.sourcelin.ai.service;

/**
 * AI会话映射 Service
 */
public interface AiConversationService {

    /**
     * 查找已有的 Dify conversation_id
     * @return conversation_id，首次调用返回 null
     */
    String lookupConversationId(String sessionKey, String abilityCode);

    /**
     * 保存会话映射（INSERT ... ON DUPLICATE KEY UPDATE）
     */
    void saveConversationId(String sessionKey, String abilityCode, String conversationId, String userId);
}
