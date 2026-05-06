package com.sourcelin.blog.vo;

import lombok.Data;

/**
 * 前台友链列表展示（不含邮箱等敏感字段）
 */
@Data
public class FriendLinkPublicVO
{
    private Long id;
    private String name;
    private String url;
    /** 展示用头像地址（外链或已由 avatar_file_id 解析） */
    private String avatar;
    private String description;
}
