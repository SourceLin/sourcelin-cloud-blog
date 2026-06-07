package com.sourcelin.blog.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FrontUserInfoVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String userName;
    private String nickName;
    private String email;
    private String phonenumber;
    private Integer sex;
    private String introduction;
    private String avatar;
    private Long avatarFileId;
    private Long articleCount;
    private Long followerCount;
    private Integer userType;
    private String userTypeLabel;
}
