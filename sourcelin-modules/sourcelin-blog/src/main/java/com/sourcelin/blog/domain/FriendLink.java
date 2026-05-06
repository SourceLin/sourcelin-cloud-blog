package com.sourcelin.blog.domain;

import com.sourcelin.common.core.annotation.Excel;
import com.sourcelin.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 友链对象 b_friend_link
 *
 * @author sourcelin
 * @date 2023-11-18
 */
public class FriendLink extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 名称
     */
    @Excel(name = "名称")
    private String name;

    /**
     * 头像
     * 用途：外链头像时使用
     * 优先级：低于 avatarFileId
     * 注意：新增头像请优先使用 avatarFileId 字段
     */
    @Excel(name = "头像")
    private String avatar;

    /**
     * 头像文件ID
     * 用途：本地上传头像时使用
     * 优先级：高，优先使用此字段
     */
    private Long avatarFileId;

    /**
     * 链接
     */
    @Excel(name = "链接")
    private String url;

    /**
     * 描述
     */
    private String description;

    /**
     * 封面
     */
    private String cover;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 标签
     */
    private String label;

    /**
     * 分类(1推荐 2精选 3更多)
     */
    @Excel(name = "分类(1推荐 2精选 3更多)")
    private Integer category;

    /**
     * 状态(0申请 1上架 2下架)
     */
    @Excel(name = "状态(0申请 1上架 2下架)")
    private Integer status;

    /**
     * 下架原因
     */
    private String reason;

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

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getCategory() {
        return category;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("avatar", getAvatar())
                .append("url", getUrl())
                .append("description", getDescription())
                .append("cover", getCover())
                .append("email", getEmail())
                .append("orderNum", getOrderNum())
                .append("label", getLabel())
                .append("category", getCategory())
                .append("status", getStatus())
                .append("reason", getReason())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
