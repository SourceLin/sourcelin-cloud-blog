package com.sourcelin.ai.service;

import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncRequest;
import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncResult;

/**
 * Dify 知识库同步服务。
 */
public interface DifyKnowledgeService {

    /**
     * 创建、更新或删除 Dify 知识库文档。
     *
     * @param request 同步请求
     * @return 同步结果
     */
    DifyKnowledgeDocumentSyncResult syncDocument(DifyKnowledgeDocumentSyncRequest request);
}
