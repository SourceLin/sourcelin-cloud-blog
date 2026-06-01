package com.sourcelin.blog.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 评论vo
 *
 * @author sourcelin
 * @date 2026-01-09
 */
@Data
public class CommentVO {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 父发表用户名ID
     */
    private Long parentUserId;

    /**
     * 用户名称
     */
    private String parentNickname;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 父评论id
     */
    private Long parentCommentId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论额外信息
     */
    private String commentInfo;

    /**
     * 楼层评论ID
     */
    private Long floorCommentId;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 审核状态（0待审核 1通过 2拒绝）
     */
    private Integer status;

    /**
     * 机审决策（联表 b_comment_moderation.decision：PASS/REJECT/REVIEW/AI_REVIEW），无记录时为 null（如树洞或未落子表）
     */
    private String moderationDecision;

    /**
     * 评论来源（博客文章、关于本站、留言板）
     */
    private String source;

    /**
     * 浏览器名
     */
    private String browser;

    /**
     * 浏览器版本
     */
    private String browserVersion;

    /**
     * 系统名
     */
    private String system;

    /**
     * 系统名版本
     */
    private String systemVersion;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 是否删除（0未删除 1已删除）
     */
    private Integer deleted;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
