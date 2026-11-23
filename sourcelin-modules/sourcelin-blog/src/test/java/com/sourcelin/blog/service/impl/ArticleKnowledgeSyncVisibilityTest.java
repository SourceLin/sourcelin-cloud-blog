package com.sourcelin.blog.service.impl;

import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncRequest;
import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncResult;
import com.sourcelin.ai.api.service.RemoteAiService;
import com.sourcelin.blog.config.BlogAiProperties;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.ArticleKnowledgeSync;
import com.sourcelin.blog.mapper.ArticleKnowledgeSyncMapper;
import com.sourcelin.blog.mapper.ArticleMapper;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ArticleKnowledgeSyncVisibilityTest {
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void delayedEventRemovesPreviouslyIndexedArticleThatIsNowDeletedOrPrivate(boolean deleted) {
        long id = 7L;
        Article article = new Article();
        article.setStatus(2L);
        article.setDeleted(deleted ? 1 : 0);
        article.setReadAuth(deleted ? 1 : 2);
        ArticleMapper articles = mock(ArticleMapper.class);
        when(articles.selectArticleById(id)).thenReturn(article);
        ArticleKnowledgeSync record = new ArticleKnowledgeSync();
        record.setArticleId(id);
        record.setDocumentId("published-document");
        record.setContentHash("old-hash");
        ArticleKnowledgeSyncMapper records = mock(ArticleKnowledgeSyncMapper.class);
        when(records.selectByArticleId(id)).thenReturn(record);
        RemoteAiService remote = mock(RemoteAiService.class);
        DifyKnowledgeDocumentSyncResult removed = new DifyKnowledgeDocumentSyncResult();
        removed.setSuccess(true);
        when(remote.syncDifyKnowledgeDocument(any(), eq(SecurityConstants.INNER)))
                .thenReturn(ApiResponse.success(removed));
        BlogAiProperties config = new BlogAiProperties();
        config.setEnabled(true);
        config.setKnowledgeSyncEnabled(true);
        ArticleKnowledgeSyncServiceImpl service = new ArticleKnowledgeSyncServiceImpl(articles, records,
                remote, config, new TransactionTemplate(mock(PlatformTransactionManager.class)));

        service.syncArticle(id);
        service.syncArticle(id);

        ArgumentCaptor<DifyKnowledgeDocumentSyncRequest> request =
                ArgumentCaptor.forClass(DifyKnowledgeDocumentSyncRequest.class);
        verify(remote).syncDifyKnowledgeDocument(request.capture(), eq(SecurityConstants.INNER));
        assertEquals("DELETE", request.getValue().getAction());
        assertEquals("published-document", request.getValue().getDocumentId());
        verify(records).updateByArticleId(record);
        assertNull(record.getDocumentId());
        assertNull(record.getContentHash());
    }
}
