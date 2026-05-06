package com.sourcelin.blog.vo.interaction;

import lombok.Data;

/**
 * 互动看板概览。
 */
@Data
public class InteractionOverviewVO
{
    private Long likeTotal;

    private Long collectTotal;

    private Long todayLikeUsers;

    private Long todayCollectUsers;

    private Long todayActionUsers;
}

