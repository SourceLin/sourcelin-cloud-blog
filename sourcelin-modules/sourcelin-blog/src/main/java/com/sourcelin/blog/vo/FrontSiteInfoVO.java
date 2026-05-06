package com.sourcelin.blog.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class FrontSiteInfoVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String author;

    private String title;

    private String authorInfo;

    private String avatar;

    private String bio;

    private String webName;

    private String siteName;

    private String logo;

    private String footer;

    private String backgroundImage;

    private String recordNum;

    private String github;

    private String gitee;

    private String qqNumber;

    private String qqGroup;

    private String email;

    private String wechat;

    private String webUrl;

    private String aliPay;

    private String weixinPay;

    private String openComment;

    private String openAdmiration;

    private String loginTypeList;

    private List<String> notices = new ArrayList<>();

    private List<String> webTitle = new ArrayList<>();

    private List<Integer> showList = new ArrayList<>();
}
