package com.sourcelin.blog.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FrontStatsVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long articleCount;

    private Long articleTrend;

    private Long commentCount;

    private Long commentTrend;

    private Long viewCount;

    private Long viewTrend;

    private Long userCount;

    private Long userTrend;
}
