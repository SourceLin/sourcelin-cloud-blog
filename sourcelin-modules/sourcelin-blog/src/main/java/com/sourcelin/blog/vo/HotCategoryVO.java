package com.sourcelin.blog.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 热门分类数据项。
 */
@Data
public class HotCategoryVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Long count;
}
