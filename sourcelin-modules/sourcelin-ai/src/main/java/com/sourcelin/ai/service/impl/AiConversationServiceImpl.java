package com.sourcelin.ai.service.impl;

import com.sourcelin.ai.domain.AiConversation;
import com.sourcelin.ai.mapper.AiConversationMapper;
import com.sourcelin.ai.service.AiConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AI会话映射 Service 实现
 */
@Service
public class AiConversationServiceImpl implements AiConversationService {
    private static final Logger log = LoggerFactory.getLogger(AiConversationServiceImpl.class);

    @Autowired
    private AiConversationMapper aiConversationMapper;

    @Override
    public String lookupConversationId(String sessionKey, String abilityCode) {
        AiConversation conversation = aiConversationMapper.selectBySessionKeyAndAbilityCode(sessionKey, abilityCode);
        return conversation != null ? conversation.getConversationId() : null;
    }

    @Override
    public void saveConversationId(String sessionKey, String abilityCode, String conversationId, String userId) {
        try {
            AiConversation conversation = new AiConversation();
            conversation.setSessionKey(sessionKey);
            conversation.setAbilityCode(abilityCode);
            conversation.setConversationId(conversationId);
            conversation.setUserId(userId);
            aiConversationMapper.insertOrUpdate(conversation);
        } catch (Exception e) {
            log.error("Failed to save conversation mapping. sessionKey={}, abilityCode={}", sessionKey, abilityCode, e);
        }
    }
}
