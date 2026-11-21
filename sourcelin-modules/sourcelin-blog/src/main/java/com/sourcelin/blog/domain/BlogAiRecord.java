package com.sourcelin.blog.domain;

import com.sourcelin.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;

/**
 * 博客 AI 调用采纳记录实体 b_blog_ai_record
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlogAiRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String ownerId;
    private String abilityCode;
    private String bizId;
    private String aiResult;
    private String status;
    private String adoptUserId;
    private Date adoptTime;
}
