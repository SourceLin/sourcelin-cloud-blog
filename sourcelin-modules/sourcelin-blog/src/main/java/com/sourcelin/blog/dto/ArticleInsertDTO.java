package com.sourcelin.blog.dto;

import lombok.Data;

import java.util.List;

/**
 * 新增/修改博客文章对象DTO
 *
 * @author sourcelin
 * @date 2026-01-15
 */
@Data
public class ArticleInsertDTO {
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
     * 分类id
     */
    private Long categoryId;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面地址
     */
    private String avatar;

    /**
     * 封面文件ID（优先于 avatar URL）
     */
    private Long avatarFileId;

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
     * 文章标签 id 集合。
     * <p><b>更新文章（PUT）时语义：</b></p>
     * <ul>
     *   <li>请求体<b>未包含</b>该字段或值为 {@code null}：<b>不修改</b>文章与标签的关联（保持原标签）。</li>
     *   <li>值为<b>空列表</b> {@code []}：<b>清空</b>该文章的全部标签。</li>
     *   <li>值为非空列表：<b>以本次列表为准</b>全量替换标签关联。</li>
     * </ul>
     * 管理端编辑表单应在提交时始终带上 {@code tagIds}（含空数组），避免与「null 不更新」混淆。
     */
    private List<Long> tagIds;

}
