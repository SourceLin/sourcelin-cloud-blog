package com.sourcelin.blog.service.interaction;

import com.sourcelin.blog.vo.interaction.InteractionTargetStatVO;

import java.util.List;

/**
 * 点赞互动服务。
 */
public interface IInteractionLikeService
{
    boolean like(Long userId, String targetType, Long targetId);

    boolean unlike(Long userId, String targetType, Long targetId);

    List<InteractionTargetStatVO> queryStats(Long userId, String targetType, List<Long> targetIds);
}

