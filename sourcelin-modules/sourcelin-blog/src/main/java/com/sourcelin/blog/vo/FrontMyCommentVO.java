package com.sourcelin.blog.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 当前用户发出的评论（我的互动 - 评论 Tab）。
 */
@Data
public class FrontMyCommentVO
{
    private Long id;

    private Long userId;

    private String content;

    /**
     * 审核状态（0待审核 1通过 2拒绝）
     */
    private Integer status;

    /**
     * 评论来源：article / say / treehole
     */
    private String source;

    private Long targetId;

    private String targetType;

    private String targetTitle;

    private Long parentCommentId;

    private String parentNickname;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
