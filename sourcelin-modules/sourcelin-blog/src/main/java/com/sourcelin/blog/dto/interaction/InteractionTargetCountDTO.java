package com.sourcelin.blog.dto.interaction;

import lombok.Data;

/**
 * 互动目标计数统计（按 targetId 聚合）。
 */
@Data
public class InteractionTargetCountDTO
{
    private Long targetId;

    private Long count;
}

