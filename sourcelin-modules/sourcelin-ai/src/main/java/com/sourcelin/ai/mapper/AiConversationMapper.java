package com.sourcelin.ai.mapper;

import com.sourcelin.ai.domain.AiConversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * AI会话映射 Mapper
 */
@Mapper
public interface AiConversationMapper {

    /**
     * 根据 sessionKey + abilityCode 查找会话映射
     */
    AiConversation selectBySessionKeyAndAbilityCode(@Param("sessionKey") String sessionKey, @Param("abilityCode") String abilityCode);

    /**
     * 插入或更新会话映射（ON DUPLICATE KEY UPDATE）
     */
    int insertOrUpdate(AiConversation conversation);
}
