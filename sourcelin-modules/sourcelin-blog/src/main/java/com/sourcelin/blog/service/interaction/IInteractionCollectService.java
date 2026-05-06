package com.sourcelin.blog.service.interaction;

/**
 * 收藏互动服务。
 */
public interface IInteractionCollectService
{
    boolean collect(Long userId, String targetType, Long targetId);

    boolean uncollect(Long userId, String targetType, Long targetId);
}

