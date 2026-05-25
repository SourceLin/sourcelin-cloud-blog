package com.sourcelin.blog.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 博客文章vo
 *
 * @author sourcelin
 * @date 2026-01-09
 */
@Data
public class ArticleVO {
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
    private String author;

    /**
     * 分类id
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 标签ids
     */
    private String tagIds;

    /**
     * 标签名称
     */
    private String tagNames;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面地址
     */
    private String avatar;

    /**
     * 简介
     */
    private String summary;

    /**
     * 内容
     */
    private String content;

    /**
     * 阅读权限（1公开可见 2登录阅读  3评论阅读  4验证阅读  5会员可见  6付费阅读  7点赞阅读 8收藏阅读 9关注阅读）
     */
    private Integer readAuth;

    /**
     * 文章状态 （0下架 1待审批 2发布 3草稿）
     */
    private Long status;

    /**
     * 是否启用评论 （0否 1是）
     */
    private Integer isComment;

    /**
     * 是否推荐 （0否 1是）
     */
    private Integer isRecommend;

    /**
     * 是否置顶 （0否 1是）
     */
    private Integer isTop;

    /**
     * 是否原创  （0转载 1原创）
     */
    private Integer isOriginal;

    /**
     * 转载地址
     */
    private String originalUrl;

    /**
     * 浏览量
     */
    private Long viewCount;

    /**
     * 点赞数
     */
    private Long likeCount;

    /**
     * 收藏数
     */
    private Long collectCount;

    private Long commentCount;

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

    /**
     * 备注
     */
    private String remark;
}
