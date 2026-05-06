package com.sourcelin.blog.vo;

import lombok.Data;

/**
 * 博客统计概览
 */
@Data
public class BlogStatsSummaryVO
{
    private Long articleCount;
    private Long commentCount;
    private Long userCount;
    private Long viewCount;

    /** 文章状态分布（与 ArticleConstants：1 审核中 2 已发布 3 草稿） */
    private Long articleReviewCount;
    private Long articlePublishedCount;
    private Long articleDraftCount;
    /** 其它 status 的文章数 */
    private Long articleOtherStatusCount;

    /** 评论待人工审核（status=0） */
    private Long commentPendingCount;
    /** 评论审核通过（status=1） */
    private Long commentApprovedCount;
    /** 评论审核拒绝（status=2） */
    private Long commentRejectedCount;
    /** 评论其它 status */
    private Long commentOtherStatusCount;
    /** 友链待处理（申请中 status=0） */
    private Long friendLinkPendingCount;

    /** 树洞条数（b_treehole.deleted=0） */
    private Long treeholeCount;
    /** 归属树洞的评论数（b_comment.source=treehole） */
    private Long treeholeCommentCount;
    /** 树洞状态：正常 status=0 */
    private Long treeholeNormalCount;
    /** 树洞状态：置顶 status=1 */
    private Long treeholePinnedCount;
    /** 树洞状态：其它（含 status=2 等） */
    private Long treeholeOtherStatusCount;
}
