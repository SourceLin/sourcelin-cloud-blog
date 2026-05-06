package com.sourcelin.blog.vo;

import lombok.Data;

/**
 * 前台友链申请请求体（仅允许白名单字段，服务端再落库）
 */
@Data
public class FriendLinkApplyRequest
{
    /** 站点名称 */
    private String name;
    /** 站点地址 */
    private String url;
    /** 简介 */
    private String description;
    /** 头像外链 */
    private String avatar;
    /** 联系邮箱 */
    private String email;
}
