package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.Comment;
import com.sourcelin.blog.constant.CommentSourceEnum;
import com.sourcelin.blog.domain.Say;
import com.sourcelin.blog.domain.Treehole;
import com.sourcelin.blog.constant.ModerationDecisionEnum;
import com.sourcelin.blog.service.BlogInteractionGuard;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ICommentService;
import com.sourcelin.blog.service.ISayService;
import com.sourcelin.blog.service.ITreeholeService;
import com.sourcelin.blog.service.ModerationPipeline;
import com.sourcelin.blog.support.BlogRichTextSanitizer;
import com.sourcelin.blog.vo.CommentVO;
import com.sourcelin.blog.vo.ModerationOutcome;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FrontCommentControllerTest {

    @InjectMocks
    private FrontCommentController controller;

    @Mock
    private ICommentService commentService;

    @Mock
    private IArticleService articleService;

    @Mock
    private ISayService sayService;

    @Mock
    private ITreeholeService treeholeService;

    @Mock
    private BlogInteractionGuard blogInteractionGuard;

    @Mock
    private BlogLoginAccessor blogLoginAccessor;

    @Mock
    private ModerationPipeline moderationPipeline;
    @Mock
    private BlogRichTextSanitizer blogRichTextSanitizer;

    private MockHttpServletRequest mockRequest;

    @BeforeEach
    void setUpRequestContext() {
        mockRequest = new MockHttpServletRequest();
        mockRequest.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
        lenient().when(blogRichTextSanitizer.sanitizePlainText(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(moderationPipeline.moderate(anyString())).thenReturn(
                ModerationOutcome.builder()
                        .decision(ModerationDecisionEnum.PASS)
                        .status(1)
                        .hitKeywords(Collections.emptyList())
                        .persistModeration(true)
                        .build());
    }

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldModerateAnonymousTreeholeCommentAndBumpCountWhenPass() {
        Comment comment = new Comment();
        comment.setArticleId(100L);
        comment.setSource(CommentSourceEnum.TREEHOLE.getCode());
        comment.setContent("匿名树洞评论");

        Treehole treehole = new Treehole();
        treehole.setId(100L);
        treehole.setDeleted(0L);

        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);
        when(blogInteractionGuard.tryAnonymousCommentAdd(anyString())).thenReturn(BlogInteractionGuard.Decision.OK);
        when(treeholeService.selectTreeholeById(100L)).thenReturn(treehole);
        when(commentService.insertCommentWithModeration(any(Comment.class), any(ModerationOutcome.class))).thenReturn(1);

        Void result = controller.add(comment, mockRequest);
        assertNull(result);
        ArgumentCaptor<Comment> cCaptor = ArgumentCaptor.forClass(Comment.class);
        ArgumentCaptor<ModerationOutcome> oCaptor = ArgumentCaptor.forClass(ModerationOutcome.class);
        verify(commentService).insertCommentWithModeration(cCaptor.capture(), oCaptor.capture());
        Comment saved = cCaptor.getValue();
        assertEquals(CommentSourceEnum.TREEHOLE.getCode(), saved.getSource());
        assertNull(saved.getStatus());
        assertNull(saved.getUserId());
        assertEquals(1, oCaptor.getValue().getStatus());
        verify(treeholeService).updateCommentCount(100L);
        verify(blogInteractionGuard).tryAnonymousCommentAdd(anyString());
        verify(commentService, never()).insertComment(any());
        verify(moderationPipeline).moderate("匿名树洞评论");
    }

    @Test
    void shouldIgnoreClientStatusForTreeholeAndRunModeration() {
        Comment comment = new Comment();
        comment.setArticleId(100L);
        comment.setSource(CommentSourceEnum.TREEHOLE.getCode());
        comment.setContent("匿名树洞评论");
        comment.setStatus(0);

        Treehole treehole = new Treehole();
        treehole.setId(100L);
        treehole.setDeleted(0L);

        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);
        when(blogInteractionGuard.tryAnonymousCommentAdd(anyString())).thenReturn(BlogInteractionGuard.Decision.OK);
        when(treeholeService.selectTreeholeById(100L)).thenReturn(treehole);
        when(commentService.insertCommentWithModeration(any(Comment.class), any(ModerationOutcome.class))).thenReturn(1);

        Void result = controller.add(comment, mockRequest);
        assertNull(result);
        ArgumentCaptor<Comment> cCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentService).insertCommentWithModeration(cCaptor.capture(), any(ModerationOutcome.class));
        assertNull(cCaptor.getValue().getStatus());
        verify(treeholeService).updateCommentCount(100L);
        verify(commentService, never()).insertComment(any());
        verify(moderationPipeline).moderate("匿名树洞评论");
    }

    @Test
    void shouldPersistSayCommentAsVisibleForFrontList() {
        Comment comment = new Comment();
        comment.setArticleId(9L);
        comment.setSource(CommentSourceEnum.SAY.getCode());
        comment.setContent("说说评论");

        Say say = new Say();
        say.setId(9L);
        say.setDeleted(0L);

        when(blogLoginAccessor.getCurrentUserId()).thenReturn(1L);
        when(blogInteractionGuard.tryCommentAdd(org.mockito.ArgumentMatchers.eq(1L)))
                .thenReturn(BlogInteractionGuard.Decision.OK);
        when(sayService.selectSayById(9L)).thenReturn(say);
        when(commentService.insertCommentWithModeration(org.mockito.ArgumentMatchers.any(Comment.class),
                org.mockito.ArgumentMatchers.any(ModerationOutcome.class))).thenReturn(1);

        Void result = controller.add(comment, mockRequest);
        assertNull(result);
        ArgumentCaptor<Comment> cCaptor = ArgumentCaptor.forClass(Comment.class);
        ArgumentCaptor<ModerationOutcome> oCaptor = ArgumentCaptor.forClass(ModerationOutcome.class);
        verify(commentService).insertCommentWithModeration(cCaptor.capture(), oCaptor.capture());
        Comment saved = cCaptor.getValue();
        assertEquals(CommentSourceEnum.SAY.getCode(), saved.getSource());
        assertNull(saved.getStatus());
        assertEquals(1, oCaptor.getValue().getStatus());
        verify(sayService).updateCommentCount(9L);
        verify(commentService, never()).insertComment(any());
    }

    @Test
    void shouldRejectAnonymousArticleComment() {
        Comment comment = new Comment();
        comment.setArticleId(1L);
        comment.setContent("文章评论");

        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.add(comment, mockRequest));
        assertEquals("请先登录", ex.getMessage());
        verify(commentService, never()).insertComment(org.mockito.ArgumentMatchers.any(Comment.class));
    }

    @Test
    void shouldAllowAnonymousLikeForTreeholeComment() {
        CommentVO comment = new CommentVO();
        comment.setId(9L);
        comment.setSource(CommentSourceEnum.TREEHOLE.getCode());
        comment.setDeleted(0);
        comment.setStatus(1);

        when(commentService.selectCommentById(9L)).thenReturn(comment);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);
        when(blogInteractionGuard.tryAnonymousCommentLike(anyString(), eq(9L))).thenReturn(BlogInteractionGuard.Decision.OK);
        when(commentService.updateLikeCount(9L)).thenReturn(1);

        Void result = controller.like(9L);
        assertNull(result);
        verify(blogInteractionGuard).tryAnonymousCommentLike(anyString(), eq(9L));
    }

    @Test
    void shouldKeepArticleCommentLikeLoginProtected() {
        CommentVO comment = new CommentVO();
        comment.setId(5L);
        comment.setSource(CommentSourceEnum.ARTICLE.getCode());
        comment.setDeleted(0);
        comment.setStatus(1);

        when(commentService.selectCommentById(5L)).thenReturn(comment);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.like(5L));
        assertEquals("请先登录", ex.getMessage());
        verify(blogInteractionGuard, never()).tryAnonymousCommentLike(anyString(), eq(5L));
    }

    @Test
    void shouldRejectLikeWhenCommentNotApproved() {
        CommentVO comment = new CommentVO();
        comment.setId(5L);
        comment.setSource(CommentSourceEnum.ARTICLE.getCode());
        comment.setDeleted(0);
        comment.setStatus(0);

        when(commentService.selectCommentById(5L)).thenReturn(comment);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.like(5L));
        assertEquals("请先登录", ex.getMessage());
        verify(commentService, never()).updateLikeCount(anyLong());
    }

    @Test
    void shouldRejectLikeWhenCommentRejected() {
        CommentVO comment = new CommentVO();
        comment.setId(5L);
        comment.setSource(CommentSourceEnum.ARTICLE.getCode());
        comment.setDeleted(0);
        comment.setStatus(2);

        when(commentService.selectCommentById(5L)).thenReturn(comment);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.like(5L));
        assertEquals("请先登录", ex.getMessage());
        verify(commentService, never()).updateLikeCount(anyLong());
    }

    @Test
    void shouldNotLeakCommentExistenceForAnonymousLike() {
        when(commentService.selectCommentById(99L)).thenReturn(null);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.like(99L));
        assertEquals("请先登录", ex.getMessage());
        verify(commentService, never()).updateLikeCount(anyLong());
    }

    @Test
    void shouldTellAuthorWhenOwnCommentNotApproved() {
        CommentVO comment = new CommentVO();
        comment.setId(5L);
        comment.setUserId(100L);
        comment.setSource(CommentSourceEnum.ARTICLE.getCode());
        comment.setDeleted(0);
        comment.setStatus(0);

        when(commentService.selectCommentById(5L)).thenReturn(comment);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(100L);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.like(5L));
        assertEquals("评论未通过审核，暂不支持互动", ex.getMessage());
        verify(commentService, never()).updateLikeCount(anyLong());
    }

    @Test
    void shouldHideNotApprovedCommentFromOtherUsers() {
        CommentVO comment = new CommentVO();
        comment.setId(5L);
        comment.setUserId(100L);
        comment.setSource(CommentSourceEnum.ARTICLE.getCode());
        comment.setDeleted(0);
        comment.setStatus(0);

        when(commentService.selectCommentById(5L)).thenReturn(comment);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(200L);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.like(5L));
        assertEquals("评论不存在", ex.getMessage());
        verify(commentService, never()).updateLikeCount(anyLong());
    }

    @Test
    void shouldNotBumpSayCommentCountWhenModerationRejects() {
        when(moderationPipeline.moderate(anyString())).thenReturn(
                ModerationOutcome.builder()
                        .decision(ModerationDecisionEnum.REJECT)
                        .status(2)
                        .hitKeywords(Arrays.asList("bad"))
                        .persistModeration(true)
                        .build());

        Comment comment = new Comment();
        comment.setArticleId(9L);
        comment.setSource(CommentSourceEnum.SAY.getCode());
        comment.setContent("违禁");

        Say say = new Say();
        say.setId(9L);
        say.setDeleted(0L);

        when(blogLoginAccessor.getCurrentUserId()).thenReturn(1L);
        when(blogInteractionGuard.tryCommentAdd(eq(1L))).thenReturn(BlogInteractionGuard.Decision.OK);
        when(sayService.selectSayById(9L)).thenReturn(say);
        when(commentService.insertCommentWithModeration(any(Comment.class), any(ModerationOutcome.class))).thenReturn(1);

        assertNull(controller.add(comment, mockRequest));
        verify(sayService, never()).updateCommentCount(anyLong());
    }

    @Test
    void shouldRejectEmptyContent() {
        Comment comment = new Comment();
        comment.setArticleId(1L);
        comment.setContent("");

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.add(comment, mockRequest));
        assertEquals(ResultCode.VALIDATION_ERROR, ex.getResultCode());
        assertEquals("评论内容不能为空", ex.getMessage());
        verify(commentService, never()).insertComment(any());
    }

    @Test
    void shouldRejectNullContent() {
        Comment comment = new Comment();
        comment.setArticleId(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.add(comment, mockRequest));
        assertEquals("评论内容不能为空", ex.getMessage());
        verify(commentService, never()).insertComment(any());
    }

    @Test
    void shouldSanitizeXssContent() {
        Comment comment = new Comment();
        comment.setArticleId(1L);
        comment.setContent("<script>alert(1)</script>");

        when(blogRichTextSanitizer.sanitizePlainText("<script>alert(1)</script>")).thenReturn("");

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.add(comment, mockRequest));
        assertEquals("评论内容不能为空", ex.getMessage());
        verify(commentService, never()).insertComment(any());
    }

    @Test
    void shouldNotBumpSayCommentCountWhenModerationPending() {
        when(moderationPipeline.moderate(anyString())).thenReturn(
                ModerationOutcome.builder()
                        .decision(ModerationDecisionEnum.AI_REVIEW)
                        .status(0)
                        .hitKeywords(Arrays.asList("ad"))
                        .persistModeration(true)
                        .build());

        Comment comment = new Comment();
        comment.setArticleId(9L);
        comment.setSource(CommentSourceEnum.SAY.getCode());
        comment.setContent("灰区");

        Say say = new Say();
        say.setId(9L);
        say.setDeleted(0L);

        when(blogLoginAccessor.getCurrentUserId()).thenReturn(1L);
        when(blogInteractionGuard.tryCommentAdd(eq(1L))).thenReturn(BlogInteractionGuard.Decision.OK);
        when(sayService.selectSayById(9L)).thenReturn(say);
        when(commentService.insertCommentWithModeration(any(Comment.class), any(ModerationOutcome.class))).thenReturn(1);

        assertNull(controller.add(comment, mockRequest));
        verify(sayService, never()).updateCommentCount(anyLong());
    }

    @Test
    void shouldNotBumpTreeholeCommentCountWhenModerationRejects() {
        when(moderationPipeline.moderate(anyString())).thenReturn(
                ModerationOutcome.builder()
                        .decision(ModerationDecisionEnum.REJECT)
                        .status(2)
                        .hitKeywords(Arrays.asList("bad"))
                        .persistModeration(true)
                        .build());

        Comment comment = new Comment();
        comment.setArticleId(100L);
        comment.setSource(CommentSourceEnum.TREEHOLE.getCode());
        comment.setContent("违禁");

        Treehole treehole = new Treehole();
        treehole.setId(100L);
        treehole.setDeleted(0L);

        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);
        when(blogInteractionGuard.tryAnonymousCommentAdd(anyString())).thenReturn(BlogInteractionGuard.Decision.OK);
        when(treeholeService.selectTreeholeById(100L)).thenReturn(treehole);
        when(commentService.insertCommentWithModeration(any(Comment.class), any(ModerationOutcome.class))).thenReturn(1);

        assertNull(controller.add(comment, mockRequest));
        verify(treeholeService, never()).updateCommentCount(anyLong());
    }
}
