package com.sourcelin.blog.domain;

import com.sourcelin.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
2 * 移动端能力配置对象 b_mobile_capability
3 * 
4 * @author sourcelin
5 * @date 2026-06-05
6 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MobileCapability extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 客户端类型(mini/h5/app) */
    private String client;

    /** 小程序安全模式 */
    private Boolean reviewSafeMode;

    /** 文章阅读 */
    private Boolean articleReadEnabled;

    /** 搜索 */
    private Boolean searchEnabled;

    /** 个人资料 */
    private Boolean profileEnabled;

    /** 收藏 */
    private Boolean favoriteEnabled;

    /** 阅读历史 */
    private Boolean readingHistoryEnabled;

    /** 体验设置 */
    private Boolean settingsEnabled;

    /** 协议与政策 */
    private Boolean policyEnabled;

    /** 关于本站 */
    private Boolean aboutEnabled;

    /** 友情链接 */
    private Boolean friendLinkEnabled;

    /** 网站导航 */
    private Boolean navigationEnabled;

    /** 写文章 */
    private Boolean articlePublishEnabled;

    /** 允许写文章的最小角色 */
    private String articlePublishRole;

    /** 评论与回复 */
    private Boolean commentEnabled;

    /** 社区Tab */
    private Boolean communityEnabled;

    /** 发布说说 */
    private Boolean sayPublishEnabled;

    /** 树洞投递 */
    private Boolean treeholeEnabled;

    /** 我的互动 */
    private Boolean interactionCenterEnabled;

    /** 关注/粉丝 */
    private Boolean followEnabled;

    /** 消息中心 */
    private Boolean messageCenterEnabled;

    /** 用户主页 */
    private Boolean userHomeEnabled;

    /** 普通用户上传图片/音频 */
    private Boolean userUploadEnabled;
}
