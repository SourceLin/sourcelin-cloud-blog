package com.sourcelin.blog.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class FrontAboutInfoVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String author;

    private String authorInfo;

    private String avatar;

    private String bio;

    private String webName;

    private String summary;

    private String keyword;

    private String recordNum;

    private String webUrl;

    private String github;

    private String gitee;

    private String qqNumber;

    private String qqGroup;

    private String email;

    private String wechat;

    private String aliPay;

    private String weixinPay;

    private String openComment;

    private String openAdmiration;

    private List<Integer> showList = new ArrayList<>();

    private List<String> notices = new ArrayList<>();
}
