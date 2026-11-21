package com.sourcelin.blog.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 文章知识库批量同步结果。
 */
@Data
public class ArticleKnowledgeBatchSyncVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int total;
    private int success;
    private int skipped;
    private int failed;
}
