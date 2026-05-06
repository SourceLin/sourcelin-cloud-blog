package com.sourcelin.blog.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FrontUserCenterStatsVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long articleCount;
    private Long collectCount;
    private Long followCount;
    private Long fansCount;
}

