package com.sourcelin.blog.domain;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.common.core.annotation.Excel;
import com.sourcelin.common.core.web.domain.BaseEntity;

/**
 * 树洞对象 b_treehole
 *
 * @author sourcelin
 * @date 2026-03-21
 */
public class Treehole extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id（可为空，匿名）
     */
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 用户昵称（联表查询）
     */
    @Excel(name = "用户昵称")
    private String userNickname;

    /**
     * 树洞内容
     */
    @Excel(name = "树洞内容")
    private String content;

    /**
     * 图片地址（多个用逗号分隔）
     */
    @Excel(name = "图片地址")
    private String images;

    /**
     * 点赞数
     */
    @Excel(name = "点赞数")
    private Long likeCount;

    /**
     * 评论数
     */
    @Excel(name = "评论数")
    private Long commentCount;

    /**
     * 收藏数
     */
    @Excel(name = "收藏数")
    private Long collectCount;

    /**
     * 状态（0:正常 1:置顶 2:删除）
     */
    @Excel(name = "状态")
    private Long status;

    /**
     * 是否删除（0:正常 1:删除）
     */
    @Excel(name = "是否删除")
    private Long deleted;

    /**
     * IP地址
     */
    @Excel(name = "IP地址")
    private String ipAddress;

    /**
     * 浏览器
     */
    @Excel(name = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Excel(name = "操作系统")
    private String system;

    /**
     * 用户详情（联表查询）
     */
    private User user;

    /**
     * 当前登录用户是否已点赞
     */
    private Boolean likedByMe;

    /**
     * 当前登录用户是否已收藏
     */
    private Boolean collectedByMe;

    public Treehole() {
    }

    public Treehole(Long userId, String content) {
        this.userId = userId;
        this.content = content;
        this.likeCount = 0L;
        this.commentCount = 0L;
        this.collectCount = 0L;
        this.status = 0L;
        this.deleted = 0L;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Long collectCount) {
        this.collectCount = collectCount;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getDeleted() {
        return deleted;
    }

    public void setDeleted(Long deleted) {
        this.deleted = deleted;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(Boolean likedByMe) {
        this.likedByMe = likedByMe;
    }

    public Boolean getCollectedByMe() {
        return collectedByMe;
    }

    public void setCollectedByMe(Boolean collectedByMe) {
        this.collectedByMe = collectedByMe;
    }

    @Override
    public String toString() {
        return "Treehole{" +
                "id=" + id +
                ", userId=" + userId +
                ", userNickname='" + userNickname + '\'' +
                ", content='" + content + '\'' +
                ", images='" + images + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", collectCount=" + collectCount +
                ", status=" + status +
                ", deleted=" + deleted +
                ", ipAddress='" + ipAddress + '\'' +
                ", browser='" + browser + '\'' +
                ", system='" + system + '\'' +
                ", user=" + user +
                ", likedByMe=" + likedByMe +
                ", collectedByMe=" + collectedByMe +
                ", createTime=" + getCreateTime() +
                ", updateTime=" + getUpdateTime() +
                '}';
    }
}
