package com.sourcelin.blog.domain;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 收藏对象 b_collect
 *
 * @author sourcelin
 * @date 2023-11-18
 */
public class Collect extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String TARGET_TYPE_ARTICLE = "article";
    public static final String TARGET_TYPE_SAY = "say";
    public static final String TARGET_TYPE_TREEHOLE = "treehole";

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 收藏目标类型（article/say）
     */
    private String targetType;

    /**
     * 收藏目标ID
     */
    private Long targetId;

    /**
     * 是否删除（0正常 1删除）
     */
    private Integer deleted;

    /**
     * 文章详情
     */
    private Article article;

    /**
     * 说说详情
     */
    private Say say;

    /**
     * 树洞详情
     */
    private Treehole treehole;

    /**
     * 用户详情（联表查询）
     */
    private User user;

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

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Say getSay() {
        return say;
    }

    public void setSay(Say say) {
        this.say = say;
    }

    public Treehole getTreehole() {
        return treehole;
    }

    public void setTreehole(Treehole treehole) {
        this.treehole = treehole;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("targetType", getTargetType())
                .append("targetId", getTargetId())
                .append("deleted", getDeleted())
                .append("createTime", getCreateTime())
                .toString();
    }
}
