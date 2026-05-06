package com.sourcelin.blog.domain;

import com.sourcelin.common.core.web.domain.TreeEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 门户导航栏对象 b_web_navbar
 *
 * @author sourcelin
 * @date 2023-11-06
 */
public class WebNavbar extends TreeEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 导航栏ID
     */
    private Long id;

    /**
     * 导航栏名称
     */
    private String name;

    /**
     * 导航栏简介
     */
    private String summary;

    /**
     * 导航栏图标
     */
    private String icon;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 是否为外链（0是 1否）
     */
    private String isFrame;

    /**
     * 导航栏类型（M目录 C菜单）
     */
    private String type;

    /**
     * 导航栏状态（0显示 1隐藏）
     */
    private String visible;

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

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getComponent() {
        return component;
    }

    public String getIsFrame() {
        return isFrame;
    }

    public void setIsFrame(String isFrame) {
        this.isFrame = isFrame;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getVisible() {
        return visible;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("summary", getSummary())
                .append("icon", getIcon())
                .append("parentId", getParentId())
                .append("orderNum", getOrderNum())
                .append("path", getPath())
                .append("component", getComponent())
                .append("isFrame", getIsFrame())
                .append("type", getType())
                .append("visible", getVisible())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
