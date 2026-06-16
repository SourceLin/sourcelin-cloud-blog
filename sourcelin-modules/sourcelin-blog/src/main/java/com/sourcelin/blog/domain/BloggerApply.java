package com.sourcelin.blog.domain;

import com.sourcelin.common.core.annotation.Excel;
import com.sourcelin.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import java.util.Date;

/**
 * 博主申请对象 b_blogger_apply
 *
 * @author sourcelin
 * @date 2026-06-16
 */
public class BloggerApply extends BaseEntity {
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
     * 申请理由
     */
    @Excel(name = "申请理由")
    private String reason;

    /**
     * 联系方式
     */
    @Excel(name = "联系方式")
    private String contact;

    /**
     * 申请状态：0待审核，1已通过，2已拒绝
     */
    @Excel(name = "申请状态", readConverterExp = "0=待审核,1=已通过,2=已拒绝")
    private Integer status;

    /**
     * 审批意见
     */
    @Excel(name = "审批意见")
    private String auditOpinion;

    /**
     * 审批时间
     */
    private Date auditTime;

    /**
     * 审批人
     */
    @Excel(name = "审批人")
    private String auditBy;

    /**
     * 是否删除：0正常，1删除
     */
    private Integer deleted;

    // 以下为联表查询字段，非数据库表物理字段
    /**
     * 用户账号
     */
    @Excel(name = "用户账号")
    private String username;

    /**
     * 用户昵称
     */
    @Excel(name = "用户昵称")
    private String nickname;

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

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion;
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditBy(String auditBy) {
        this.auditBy = auditBy;
    }

    public String getAuditBy() {
        return auditBy;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("reason", getReason())
                .append("contact", getContact())
                .append("status", getStatus())
                .append("auditOpinion", getAuditOpinion())
                .append("auditTime", getAuditTime())
                .append("auditBy", getAuditBy())
                .append("deleted", getDeleted())
                .append("username", getUsername())
                .append("nickname", getNickname())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("createBy", getCreateBy())
                .append("updateBy", getUpdateBy())
                .toString();
    }
}
