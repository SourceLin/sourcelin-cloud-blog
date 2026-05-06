package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.CommentModeration;

/**
 * 评论机审记录
 */
public interface CommentModerationMapper
{
    int insertCommentModeration(CommentModeration row);
}
