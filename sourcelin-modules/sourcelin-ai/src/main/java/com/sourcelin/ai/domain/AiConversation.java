package com.sourcelin.ai.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * AI会话映射
 */
@Data
public class AiConversation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String sessionKey;
    private String abilityCode;
    private String conversationId;
    private String userId;
    private String tenantId;
    private Date createTime;
    private Date updateTime;
}
