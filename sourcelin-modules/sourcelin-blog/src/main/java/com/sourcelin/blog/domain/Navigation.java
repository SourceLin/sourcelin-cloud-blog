package com.sourcelin.blog.domain;

import com.sourcelin.common.core.annotation.Excel;
import com.sourcelin.common.core.web.domain.BaseEntity;
import lombok.Data;

@Data
public class Navigation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "Name")
    private String name;

    @Excel(name = "URL")
    private String url;

    @Excel(name = "Description")
    private String description;

    @Excel(name = "Icon")
    private String icon;

    @Excel(name = "Cover")
    private String cover;

    @Excel(name = "Category")
    private String category;

    @Excel(name = "Source")
    private String source;

    @Excel(name = "Recommend")
    private Integer isRecommend;

    @Excel(name = "Status")
    private Integer status;

    @Excel(name = "Order")
    private Integer orderNum;

    @Excel(name = "Click Count")
    private Long clickCount;
}
