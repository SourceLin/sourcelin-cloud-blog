package com.sourcelin.blog.domain;

import com.sourcelin.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 文章知识库同步记录。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArticleKnowledgeSync extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long articleId;
    private String sourceType;
    private String abilityCode;
    private String documentId;
    private String contentHash;
    private String syncStatus;
    private String indexingStatus;
    private String batch;
    private String errorMessage;
    private Date lastSyncTime;
}
