package com.sourcelin.blog.domain;

import com.sourcelin.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 评论管理对象 b_comment
 *
 * @author sourcelin
 * @date 2023-11-16
 */
public class Comment extends BaseEntity {
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
     * 父发表用户名ID
     */
    private Long parentUserId;

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
     * 评论来源（博客文章、关于本站、留言板、友链）
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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setParentUserId(Long parentUserId) {
        this.parentUserId = parentUserId;
    }

    public Long getParentUserId() {
        return parentUserId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setCommentInfo(String commentInfo) {
        this.commentInfo = commentInfo;
    }

    public String getCommentInfo() {
        return commentInfo;
    }

    public void setFloorCommentId(Long floorCommentId) {
        this.floorCommentId = floorCommentId;
    }

    public Long getFloorCommentId() {
        return floorCommentId;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    public String getBrowserVersion() {
        return browserVersion;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getSystem() {
        return system;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getIpSource() {
        return ipSource;
    }

    public void setIpSource(String ipSource) {
        this.ipSource = ipSource;
    }


    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("parentUserId", getParentUserId())
                .append("articleId", getArticleId())
                .append("parentCommentId", getParentCommentId())
                .append("content", getContent())
                .append("commentInfo", getCommentInfo())
                .append("floorCommentId", getFloorCommentId())
                .append("likeCount", getLikeCount())
                .append("status", getStatus())
                .append("source", getSource())
                .append("browser", getBrowser())
                .append("browserVersion", getBrowserVersion())
                .append("system", getSystem())
                .append("systemVersion", getSystemVersion())
                .append("ipAddress", getIpAddress())
                .append("ipSource", getIpSource())
                .append("deleted", getDeleted())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
