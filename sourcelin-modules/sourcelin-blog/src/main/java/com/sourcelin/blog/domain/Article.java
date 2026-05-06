package com.sourcelin.blog.domain;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.common.core.annotation.Excel;
import com.sourcelin.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Transient;

import java.util.List;

/**
 * 博客文章对象 b_article
 *
 * @author sourcelin
 * @date 2023-11-09
 */
public class Article extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 分类id
     */
    @Excel(name = "分类id")
    private Long categoryId;

    /**
     * 标题
     */
    @Excel(name = "标题")
    private String title;

    /**
     * 封面地址
     * 用途：外链封面时使用（如转载文章复制他人封面）
     * 优先级：低于 avatarFileId
     * 注意：新增封面请优先使用 avatarFileId 字段
     */
    @Excel(name = "封面地址")
    private String avatar;

    /**
     * 封面文件ID
     * 用途：本地上传封面时使用
     * 优先级：高，优先使用此字段
     */
    private Long avatarFileId;

    /**
     * 简介
     */
    @Excel(name = "简介")
    private String summary;

    /**
     * 内容
     */
    @Excel(name = "内容")
    private String content;

    /**
     * 阅读权限（1公开可见 2登录阅读  3评论阅读  4验证阅读  5会员可见  6付费阅读  7点赞阅读 8收藏阅读 9关注阅读）
     */
    @Excel(name = "阅读权限", readConverterExp = "1=公开可见,2=登录阅读,3=评论阅读,4=验证阅读,5=会员可见,6=付费阅读,7=点赞阅读,8=收藏阅读,9=关注阅读")
    private Integer readAuth;

    /**
     * 文章状态 （0下架 1待审批 2发布 3草稿）
     */
    @Excel(name = "文章状态 ", readConverterExp = "0=下架,1=待审批,2=发布,3=草稿")
    private Long status;

    /**
     * 是否启用评论 （0否 1是）
     */
    @Excel(name = "是否启用评论 ", readConverterExp = "0=否,1=是")
    private Integer isComment;

    /**
     * 是否推荐 （0否 1是）
     */
    @Excel(name = "是否推荐 ", readConverterExp = "0=否,1=是")
    private Integer isRecommend;

    /**
     * 是否置顶 （0否 1是）
     */
    @Excel(name = "是否置顶 ", readConverterExp = "0=否,1=是")
    private Integer isTop;

    /**
     * 是否原创  （0转载 1原创）
     */
    @Excel(name = "是否原创  ", readConverterExp = "0=转载,1=原创")
    private Integer isOriginal;

    /**
     * 转载地址
     */
    @Excel(name = "转载地址")
    private String originalUrl;

    /**
     * 浏览量
     */
    @Excel(name = "浏览量")
    private Long viewCount;

    /**
     * 点赞数
     */
    @Excel(name = "点赞数")
    private Long likeCount;

    /**
     * 收藏数
     */
    @Excel(name = "收藏数")
    private Long collectCount;

    private Long commentCount;

    /**
     * 是否删除（0未删除 1已删除）
     */
    @Excel(name = "是否删除", readConverterExp = "0=未删除,1=已删除")
    private Integer deleted;

    /**
     * 标签ID
     */
    @Transient
    private String tagId;

    /**
     * 博客文章作者
     */
    private User user;

    /**
     * 博客文章分类
     */
    private Category category;

    /**
     * 博客文章标签
     */
    private List<Tag> tags;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatarFileId(Long avatarFileId) {
        this.avatarFileId = avatarFileId;
    }

    public Long getAvatarFileId() {
        return avatarFileId;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setReadAuth(Integer readAuth) {
        this.readAuth = readAuth;
    }

    public Integer getReadAuth() {
        return readAuth;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }

    public void setIsComment(Integer isComment) {
        this.isComment = isComment;
    }

    public Integer getIsComment() {
        return isComment;
    }

    public void setIsRecommend(Integer isRecommend) {
        this.isRecommend = isRecommend;
    }

    public Integer getIsRecommend() {
        return isRecommend;
    }

    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public Integer getIsTop() {
        return isTop;
    }

    public void setIsOriginal(Integer isOriginal) {
        this.isOriginal = isOriginal;
    }

    public Integer getIsOriginal() {
        return isOriginal;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setCollectCount(Long collectCount) {
        this.collectCount = collectCount;
    }

    public Long getCollectCount() {
        return collectCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("categoryId", getCategoryId())
                .append("title", getTitle())
                .append("avatar", getAvatar())
                .append("summary", getSummary())
                .append("content", getContent())
                .append("readAuth", getReadAuth())
                .append("status", getStatus())
                .append("isComment", getIsComment())
                .append("isRecommend", getIsRecommend())
                .append("isTop", getIsTop())
                .append("isOriginal", getIsOriginal())
                .append("originalUrl", getOriginalUrl())
                .append("viewCount", getViewCount())
                .append("likeCount", getLikeCount())
                .append("collectCount", getCollectCount())
                .append("commentCount", getCommentCount())
                .append("deleted", getDeleted())
                .toString();
    }
}
