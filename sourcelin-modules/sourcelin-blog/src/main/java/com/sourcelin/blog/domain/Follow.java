package com.sourcelin.blog.domain;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 关注对象（用户关注） b_follow
 * 
 * @author sourcelin
 * @date 2023-11-18
 */
public class Follow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 用户id */
    private Long userId;

    /** 关注的用户id */
    private Long followUserId;

    /** 是否删除（0正常 1删除） */
    private Integer deleted;

    /** 目标用户ID（用于查询粉丝） */
    private Long targetUserId;

    /** 关注用户详情 */
    private User targetUser;

    /** 粉丝用户详情 */
    private User user;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }
    public void setFollowUserId(Long followUserId) 
    {
        this.followUserId = followUserId;
    }

    public Long getFollowUserId() 
    {
        return followUserId;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("userId", getUserId())
            .append("followUserId", getFollowUserId())
            .append("deleted", getDeleted())
            .append("createTime", getCreateTime())
            .toString();
    }
}
