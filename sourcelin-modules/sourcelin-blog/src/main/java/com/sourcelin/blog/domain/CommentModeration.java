package com.sourcelin.blog.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 评论机审记录 b_comment_moderation
 */
@Data
public class CommentModeration
{
    private Long id;
    private Long commentId;
    /** PASS / REJECT / REVIEW / AI_REVIEW */
    private String decision;
    private Integer lexiconVersion;
    private String traceId;
    /** JSON 数组字符串 */
    private String hitKeywords;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
