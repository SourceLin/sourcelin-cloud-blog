package com.sourcelin.blog.domain;

import com.sourcelin.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 博客分类对象 b_category
 *
 * @author sourcelin
 * @date 2023-11-07
 */
public class Category extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 博客分类ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 分类简介
     */
    private String summary;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 点击量
     */
    private Long clickCount;

    /**
     * 已发布文章数（非表字段，由查询聚合填充）
     */
    private Long articleCount;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setArticleCount(Long articleCount) {
        this.articleCount = articleCount;
    }

    public Long getArticleCount() {
        return articleCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("summary", getSummary())
                .append("icon", getIcon())
                .append("orderNum", getOrderNum())
                .append("clickCount", getClickCount())
                .append("articleCount", getArticleCount())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
