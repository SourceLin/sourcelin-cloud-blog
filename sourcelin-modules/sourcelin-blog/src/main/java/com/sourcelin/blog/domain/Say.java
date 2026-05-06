package com.sourcelin.blog.domain;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.common.core.annotation.Excel;
import com.sourcelin.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 说说/瞬间对象 b_say
 *
 * @author sourcelin
 * @date 2026-03-21
 */
public class Say extends BaseEntity {
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
     * 用户昵称（联表查询）
     */
    @Excel(name = "用户昵称")
    private String userNickname;

    /**
     * 说说内容
     */
    @Excel(name = "说说内容")
    private String content;

    /**
     * 图片地址（多个用逗号分隔）
     * 用途：外链图片时使用，多个用逗号分隔
     * 优先级：低于 imageFileIds
     * 注意：新增图片请优先使用 imageFileIds 字段
     */
    @Excel(name = "图片地址")
    private String images;

    /**
     * 图片文件ID（多个用逗号分隔）
     * 用途：本地上传图片时使用，多个用逗号分隔
     * 优先级：高，优先使用此字段
     */
    private String imageFileIds;

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
     * 用户详情（联表查询）
     */
    private User user;

    /**
     * 当前登录用户是否已收藏
     */
    private Boolean isCollected;

    /**
     * 当前登录用户是否已点赞
     */
    private Boolean likedByMe;

    /**
     * 当前登录用户的收藏记录ID
     */
    private Long collectId;

    public Say() {
    }

    public Say(Long userId, String content) {
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

    public String getImageFileIds() {
        return imageFileIds;
    }

    public void setImageFileIds(String imageFileIds) {
        this.imageFileIds = imageFileIds;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getIsCollected() {
        return isCollected;
    }

    public void setIsCollected(Boolean isCollected) {
        this.isCollected = isCollected;
    }

    public Boolean getLikedByMe() {
        return likedByMe;
    }

    public void setLikedByMe(Boolean likedByMe) {
        this.likedByMe = likedByMe;
    }

    public Long getCollectId() {
        return collectId;
    }

    public void setCollectId(Long collectId) {
        this.collectId = collectId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("userNickname", getUserNickname())
                .append("content", getContent())
                .append("images", getImages())
                .append("likeCount", getLikeCount())
                .append("commentCount", getCommentCount())
                .append("collectCount", getCollectCount())
                .append("status", getStatus())
                .append("deleted", getDeleted())
                .append("user", getUser())
                .append("isCollected", getIsCollected())
                .append("likedByMe", getLikedByMe())
                .append("collectId", getCollectId())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
