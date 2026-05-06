package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.dto.CommentDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 覆盖实施文档：未登录仅 status=1；已登录合并本人 0/2 —— DTO 侧与 {@link com.sourcelin.blog.mapper.CommentMapper#selectFrontCommentList} 一致。
 */
class FrontCommentListQueryTest {

    @Test
    void anonymousViewerMarksMergeWithoutUserIdSoSqlKeepsOnlyApproved() {
        CommentDTO dto = new CommentDTO();
        dto.setStatus(999);

        new FrontCommentController().applyFrontListQueryForViewer(dto, null);

        assertTrue(Boolean.TRUE.equals(dto.getFrontMergeOwn()));
        assertNull(dto.getViewerUserId());
        assertNull(dto.getStatus());
    }

    @Test
    void loggedInViewerPassesIdForSqlOrBranch() {
        CommentDTO dto = new CommentDTO();

        new FrontCommentController().applyFrontListQueryForViewer(dto, 42L);

        assertTrue(Boolean.TRUE.equals(dto.getFrontMergeOwn()));
        assertEquals(Long.valueOf(42L), dto.getViewerUserId());
        assertNull(dto.getStatus());
    }
}
