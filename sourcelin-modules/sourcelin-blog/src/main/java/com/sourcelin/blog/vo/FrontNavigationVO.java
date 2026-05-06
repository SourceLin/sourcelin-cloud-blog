package com.sourcelin.blog.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FrontNavigationVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String url;

    private String description;

    private String icon;

    private String cover;

    private String category;

    private String source;

    private Integer isRecommend;

    private Integer orderNum;

    private Long clickCount;
}
