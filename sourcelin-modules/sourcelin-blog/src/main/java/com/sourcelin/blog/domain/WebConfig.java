package com.sourcelin.blog.domain;

import com.sourcelin.common.core.web.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 网站配置对象 b_web_config
 *
 * @author sourcelin
 * @date 2026-01-04
 */
public class WebConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 网站logo
     */
    private String logo;

    /**
     * 游客头像
     */
    private String touristAvatar;

    /**
     * 网站名称
     */
    private String name;

    /**
     * 简介
     */
    private String summary;

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 备案号
     */
    private String recordNum;

    /**
     * 网站地址
     */
    private String webUrl;

    /**
     * 公告
     */
    private String notice;

    /**
     * 支付宝收款码FileId
     */
    private String aliPay;

    /**
     * 微信收款码FileId
     */
    private String weixinPay;

    /**
     * github地址
     */
    private String github;

    /**
     * gitee地址
     */
    private String gitee;

    /**
     * QQ号
     */
    private String qqNumber;

    /**
     * QQ群
     */
    private String qqGroup;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 显示的列表（用于控制邮箱、QQ、QQ群、Github、Gitee、微信是否显示在前端）
     */
    private String showList;

    /**
     * 登录方式列表（用于控制前端登录方式，如账号密码,码云,Github,QQ,微信）
     */
    private String loginTypeList;

    /**
     * 是否开启评论(0:否 1:是)
     */
    private String openComment;

    /**
     * 是否开启赞赏(0:否， 1:是)
     */
    private String openAdmiration;

    /**
     * 作者
     */
    private String author;

    /**
     * 作者简介
     */
    private String authorInfo;

    /**
     * 作者头像
     */
    private String authorAvatar;

    /**
     * 关于我
     */
    private String aboutMe;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo() {
        return logo;
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

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setRecordNum(String recordNum) {
        this.recordNum = recordNum;
    }

    public String getRecordNum() {
        return recordNum;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public void setAliPay(String aliPay) {
        this.aliPay = aliPay;
    }

    public String getAliPay() {
        return aliPay;
    }

    public void setWeixinPay(String weixinPay) {
        this.weixinPay = weixinPay;
    }

    public String getWeixinPay() {
        return weixinPay;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getGithub() {
        return github;
    }

    public void setGitee(String gitee) {
        this.gitee = gitee;
    }

    public String getGitee() {
        return gitee;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqGroup(String qqGroup) {
        this.qqGroup = qqGroup;
    }

    public String getQqGroup() {
        return qqGroup;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getWechat() {
        return wechat;
    }

    public void setShowList(String showList) {
        this.showList = showList;
    }

    public String getShowList() {
        return showList;
    }

    public void setLoginTypeList(String loginTypeList) {
        this.loginTypeList = loginTypeList;
    }

    public String getLoginTypeList() {
        return loginTypeList;
    }

    public String getOpenComment() {
        return openComment;
    }

    public void setOpenComment(String openComment) {
        this.openComment = openComment;
    }

    public String getOpenAdmiration() {
        return openAdmiration;
    }

    public void setOpenAdmiration(String openAdmiration) {
        this.openAdmiration = openAdmiration;
    }

    public void setTouristAvatar(String touristAvatar) {
        this.touristAvatar = touristAvatar;
    }

    public String getTouristAvatar() {
        return touristAvatar;
    }

    public void setAuthorInfo(String authorInfo) {
        this.authorInfo = authorInfo;
    }

    public String getAuthorInfo() {
        return authorInfo;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("logo", getLogo())
                .append("name", getName())
                .append("summary", getSummary())
                .append("keyword", getKeyword())
                .append("author", getAuthor())
                .append("recordNum", getRecordNum())
                .append("webUrl", getWebUrl())
                .append("aliPay", getAliPay())
                .append("weixinPay", getWeixinPay())
                .append("github", getGithub())
                .append("gitee", getGitee())
                .append("qqNumber", getQqNumber())
                .append("qqGroup", getQqGroup())
                .append("email", getEmail())
                .append("wechat", getWechat())
                .append("showList", getShowList())
                .append("loginTypeList", getLoginTypeList())
                .append("openComment", getOpenComment())
                .append("openAdmiration", getOpenAdmiration())
                .append("touristAvatar", getTouristAvatar())
                .append("authorInfo", getAuthorInfo())
                .append("authorAvatar", getAuthorAvatar())
                .append("aboutMe", getAboutMe())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
