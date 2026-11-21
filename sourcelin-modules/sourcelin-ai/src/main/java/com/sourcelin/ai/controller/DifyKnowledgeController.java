package com.sourcelin.ai.controller;
import com.sourcelin.common.security.annotation.InnerAuth;

import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncRequest;
import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncResult;
import com.sourcelin.ai.service.DifyKnowledgeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dify 知识库内部同步接口。
 */
@RestController
@RequestMapping("/knowledge/dify")
public class DifyKnowledgeController {

    private final DifyKnowledgeService difyKnowledgeService;

    public DifyKnowledgeController(DifyKnowledgeService difyKnowledgeService) {
        this.difyKnowledgeService = difyKnowledgeService;
    }

    @InnerAuth
    @PostMapping("/documents/sync")
    public DifyKnowledgeDocumentSyncResult syncDocument(@RequestBody DifyKnowledgeDocumentSyncRequest request) {
        return difyKnowledgeService.syncDocument(request);
    }
}
