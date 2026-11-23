package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.config.BlogAiProperties;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;

import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncRequest;
import com.sourcelin.ai.api.domain.DifyKnowledgeDocumentSyncResult;
import com.sourcelin.ai.api.service.RemoteAiService;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.ArticleKnowledgeSync;
import com.sourcelin.blog.domain.Tag;
import com.sourcelin.blog.mapper.ArticleKnowledgeSyncMapper;
import com.sourcelin.blog.mapper.ArticleMapper;
import com.sourcelin.blog.service.IArticleKnowledgeSyncService;
import com.sourcelin.blog.vo.ArticleKnowledgeBatchSyncVO;
import com.sourcelin.blog.vo.ArticleKnowledgeSyncVO;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import feign.FeignException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文章知识库同步服务实现。
 */
@Service
public class ArticleKnowledgeSyncServiceImpl implements IArticleKnowledgeSyncService {
    private static final Logger log = LoggerFactory.getLogger(ArticleKnowledgeSyncServiceImpl.class);
    private static final String SOURCE_TYPE_ARTICLE = "article";
    private static final String ABILITY_CODE = "dify_knowledge_article_sync";
    private static final Long STATUS_PUBLISHED = 2L;
    private static final Integer READ_AUTH_PUBLIC = 1;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ArticleMapper articleMapper;
    private final ArticleKnowledgeSyncMapper articleKnowledgeSyncMapper;
    private final RemoteAiService remoteAiService;
    private final BlogAiProperties properties;
    private final TransactionTemplate transactions;

    public ArticleKnowledgeSyncServiceImpl(ArticleMapper articleMapper,
                                           ArticleKnowledgeSyncMapper articleKnowledgeSyncMapper,
                                           RemoteAiService remoteAiService,
                                           BlogAiProperties properties,
                                           TransactionTemplate transactions) {
        this.articleMapper = articleMapper;
        this.articleKnowledgeSyncMapper = articleKnowledgeSyncMapper;
        this.remoteAiService = remoteAiService;
        this.properties = properties;
        this.transactions = transactions;
    }

    @Override
    public ArticleKnowledgeSyncVO syncArticle(Long articleId) {
        requireKnowledgeSyncEnabled();
        return transactions.execute(status -> synchronizeCurrentArticle(articleId));
    }

    private ArticleKnowledgeSyncVO synchronizeCurrentArticle(Long articleId) {
        // 在读取文章之前锁定同步记录，串行处理同一文章的人工补同步和提交后事件。
        articleKnowledgeSyncMapper.lockArticle(articleId);
        Article article = articleMapper.selectArticleById(articleId);
        ArticleKnowledgeSync sync = articleKnowledgeSyncMapper.selectByArticleId(articleId);
        if (!shouldSync(article)) {
            return deleteRemoteDocument(articleId, sync);
        }

        String documentText = buildDocumentText(article);
        String contentHash = sha256(documentText);
        if (sync != null
                && StringUtils.isNotBlank(sync.getDocumentId())
                && "success".equals(sync.getSyncStatus())
                && contentHash.equals(sync.getContentHash())) {
            return skipped(article, sync, "文章内容未变化，跳过同步");
        }

        DifyKnowledgeDocumentSyncRequest request = new DifyKnowledgeDocumentSyncRequest();
        request.setAbilityCode(ABILITY_CODE);
        request.setAction("UPSERT");
        request.setSourceType(SOURCE_TYPE_ARTICLE);
        request.setSourceId(String.valueOf(article.getId()));
        request.setDocumentId(sync == null ? null : sync.getDocumentId());
        request.setName(buildDocumentName(article));
        request.setText(documentText);
        request.setContentHash(contentHash);

        DifyKnowledgeDocumentSyncResult remoteResult = invokeRemoteSync(request);
        ArticleKnowledgeSync stored = saveSyncResult(article.getId(), sync, remoteResult, contentHash);
        return toVO(article, stored, remoteResult.isSuccess() ? "同步已提交，等待 Dify 索引" : remoteResult.getErrorMessage());
    }

    @Override
    public ArticleKnowledgeBatchSyncVO syncPublishedArticles() {
        requireKnowledgeSyncEnabled();
        List<Long> ids = articleMapper.selectKnowledgeSyncCandidateIds();
        ArticleKnowledgeBatchSyncVO batch = new ArticleKnowledgeBatchSyncVO();
        batch.setTotal(ids.size());
        for (Long id : ids) {
            try {
                ArticleKnowledgeSyncVO result = syncArticle(id);
                if ("skipped".equals(result.getSyncStatus())) {
                    batch.setSkipped(batch.getSkipped() + 1);
                } else if ("success".equals(result.getSyncStatus())) {
                    batch.setSuccess(batch.getSuccess() + 1);
                } else {
                    batch.setFailed(batch.getFailed() + 1);
                }
            } catch (Exception e) {
                log.error("文章知识库批量同步失败，articleId={}", id, e);
                batch.setFailed(batch.getFailed() + 1);
            }
        }
        return batch;
    }

    private void requireKnowledgeSyncEnabled() {
        if (!properties.isEnabled() || !properties.isKnowledgeSyncEnabled()) {
            throw new BusinessException(ResultCode.CONFLICT, "文章知识库同步未启用");
        }
    }

    private ArticleKnowledgeSyncVO deleteRemoteDocument(Long articleId, ArticleKnowledgeSync sync) {
        if (sync == null || StringUtils.isBlank(sync.getDocumentId())) {
            ArticleKnowledgeSyncVO vo = new ArticleKnowledgeSyncVO();
            vo.setArticleId(articleId);
            vo.setAction("DELETE");
            vo.setSyncStatus("skipped");
            vo.setMessage("文章未同步过或不符合公开发布条件，跳过删除");
            return vo;
        }

        DifyKnowledgeDocumentSyncRequest request = new DifyKnowledgeDocumentSyncRequest();
        request.setAbilityCode(ABILITY_CODE);
        request.setAction("DELETE");
        request.setSourceType(SOURCE_TYPE_ARTICLE);
        request.setSourceId(String.valueOf(articleId));
        request.setDocumentId(sync.getDocumentId());
        request.setContentHash(sync.getContentHash());

        DifyKnowledgeDocumentSyncResult remoteResult = invokeRemoteSync(request);
        if (remoteResult.isSuccess()) {
            remoteResult.setDocumentId(null);
            remoteResult.setContentHash(null);
        }
        ArticleKnowledgeSync stored = saveSyncResult(articleId, sync, remoteResult,
                remoteResult.isSuccess() ? null : sync.getContentHash());
        ArticleKnowledgeSyncVO vo = toVO(null, stored, remoteResult.isSuccess() ? "知识库文档已删除" : remoteResult.getErrorMessage());
        vo.setArticleId(articleId);
        return vo;
    }

    private DifyKnowledgeDocumentSyncResult invokeRemoteSync(DifyKnowledgeDocumentSyncRequest request) {
        ApiResponse<DifyKnowledgeDocumentSyncResult> response;
        try {
            response = remoteAiService.syncDifyKnowledgeDocument(request, SecurityConstants.INNER);
        } catch (FeignException e) {
            log.warn("知识库请求失败，文章 {}，HTTP 状态 {}", request.getSourceId(), e.status());
            return failedResult(request, "REMOTE_REQUEST_FAILED", "AI 服务请求失败，HTTP 状态 " + e.status());
        }
        if (response == null) {
            return failedResult(request, "REMOTE_EMPTY_RESPONSE", "AI 服务未返回同步结果");
        }
        if (!ResultCode.SUCCESS.getCode().equals(response.getCode())) {
            return failedResult(request, "REMOTE_SERVICE_ERROR", response.getMessage());
        }
        DifyKnowledgeDocumentSyncResult data = response.getData();
        if (data == null) {
            return failedResult(request, "REMOTE_EMPTY_DATA", "AI 服务同步结果为空");
        }
        return data;
    }

    private ArticleKnowledgeSync saveSyncResult(Long articleId,
                                                ArticleKnowledgeSync current,
                                                DifyKnowledgeDocumentSyncResult remoteResult,
                                                String contentHash) {
        ArticleKnowledgeSync sync = current == null ? new ArticleKnowledgeSync() : current;
        sync.setArticleId(articleId);
        sync.setSourceType(SOURCE_TYPE_ARTICLE);
        sync.setAbilityCode(ABILITY_CODE);
        if (remoteResult.isSuccess() || StringUtils.isNotBlank(remoteResult.getDocumentId())) {
            sync.setDocumentId(remoteResult.getDocumentId());
        }
        sync.setContentHash(contentHash);
        sync.setSyncStatus(remoteResult.isSuccess() ? "success" : "failed");
        sync.setIndexingStatus(remoteResult.getIndexingStatus());
        sync.setBatch(remoteResult.getBatch());
        sync.setErrorMessage(remoteResult.getErrorMessage());
        sync.setLastSyncTime(new Date());
        if (current == null) {
            articleKnowledgeSyncMapper.insert(sync);
        } else {
            articleKnowledgeSyncMapper.updateByArticleId(sync);
        }
        return sync;
    }

    private DifyKnowledgeDocumentSyncResult failedResult(DifyKnowledgeDocumentSyncRequest request, String code, String message) {
        DifyKnowledgeDocumentSyncResult result = new DifyKnowledgeDocumentSyncResult();
        result.setSuccess(false);
        result.setAction(request.getAction());
        result.setSourceType(request.getSourceType());
        result.setSourceId(request.getSourceId());
        result.setDocumentId(request.getDocumentId());
        result.setContentHash(request.getContentHash());
        result.setErrorCode(code);
        result.setErrorMessage(message);
        return result;
    }

    private boolean shouldSync(Article article) {
        return article != null
                && (article.getDeleted() == null || article.getDeleted() == 0)
                && STATUS_PUBLISHED.equals(article.getStatus())
                && READ_AUTH_PUBLIC.equals(article.getReadAuth());
    }

    private String buildDocumentName(Article article) {
        return "article-" + article.getId() + "-" + safeText(article.getTitle()) + ".md";
    }

    private String buildDocumentText(Article article) {
        String tags = "";
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            tags = article.getTags().stream()
                    .map(Tag::getName)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("、"));
        }

        StringBuilder builder = new StringBuilder();
        builder.append("# ").append(safeText(article.getTitle())).append("\n\n");
        builder.append("- 文章ID：").append(article.getId()).append("\n");
        builder.append("- 分类：").append(article.getCategory() == null ? "" : safeText(article.getCategory().getName())).append("\n");
        builder.append("- 标签：").append(tags).append("\n");
        builder.append("- 链接：/article/").append(article.getId()).append("\n");
        builder.append("- 发布时间：").append(formatDate(article.getCreateTime())).append("\n");
        builder.append("- 更新时间：").append(formatDate(article.getUpdateTime())).append("\n\n");
        if (StringUtils.isNotBlank(article.getSummary())) {
            builder.append("## 摘要\n").append(safeText(article.getSummary())).append("\n\n");
        }
        builder.append("## 正文\n").append(toPlainText(article.getContent())).append("\n");
        return builder.toString();
    }

    private ArticleKnowledgeSyncVO skipped(Article article, ArticleKnowledgeSync sync, String message) {
        ArticleKnowledgeSyncVO vo = toVO(article, sync, message);
        vo.setAction("UPSERT");
        vo.setSyncStatus("skipped");
        return vo;
    }

    private ArticleKnowledgeSyncVO toVO(Article article, ArticleKnowledgeSync sync, String message) {
        ArticleKnowledgeSyncVO vo = new ArticleKnowledgeSyncVO();
        if (article != null) {
            vo.setArticleId(article.getId());
            vo.setTitle(article.getTitle());
        }
        if (sync != null) {
            vo.setArticleId(sync.getArticleId());
            vo.setAction("deleted".equals(sync.getIndexingStatus()) ? "DELETE" : "UPSERT");
            vo.setSyncStatus(sync.getSyncStatus());
            vo.setIndexingStatus(sync.getIndexingStatus());
            vo.setDocumentId(sync.getDocumentId());
            vo.setBatch(sync.getBatch());
        }
        vo.setMessage(message);
        return vo;
    }

    private String toPlainText(String html) {
        if (StringUtils.isBlank(html)) {
            return "";
        }
        return Jsoup.parse(html).text();
    }

    private String safeText(String text) {
        return text == null ? "" : text.trim();
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_TIME_FORMATTER.format(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
    }

    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException("计算文章知识库同步哈希失败", e);
        }
    }
}
