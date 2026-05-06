package com.sourcelin.blog.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 热搜词数据项。
 */
@Data
public class HotSearchVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String keyword;

    private Long count;
}
