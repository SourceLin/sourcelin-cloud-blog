package com.sourcelin.ai.api.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Dify 知识库文档同步请求。
 */
@Data
public class DifyKnowledgeDocumentSyncRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 使用的 AI 能力配置编码，默认 dify_knowledge_article_sync。
     */
    private String abilityCode;

    /**
     * 操作：UPSERT / DELETE。
     */
    private String action;

    /**
     * 业务侧来源类型，例如 article。
     */
    private String sourceType;

    /**
     * 业务侧来源 ID，例如文章 ID。
     */
    private String sourceId;

    /**
     * 已存在的 Dify 文档 ID。存在时执行更新，不存在时创建。
     */
    private String documentId;

    /**
     * 文档名称。
     */
    private String name;

    /**
     * 文档文本内容。
     */
    private String text;

    /**
     * 内容摘要哈希，用于业务侧判断是否需要重复同步。
     */
    private String contentHash;
}
