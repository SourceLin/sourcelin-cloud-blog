package com.sourcelin.ai.api.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Dify 知识库文档同步结果。
 */
@Data
public class DifyKnowledgeDocumentSyncResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String action;
    private String sourceType;
    private String sourceId;
    private String documentId;
    private String batch;
    private String indexingStatus;
    private String contentHash;
    private String errorCode;
    private String errorMessage;
}
