package com.sourcelin.blog.vo;

import lombok.Data;

/**
 * 统计趋势数据
 */
@Data
public class BlogTrendCount
{
    private String date;
    private Long count;
}
