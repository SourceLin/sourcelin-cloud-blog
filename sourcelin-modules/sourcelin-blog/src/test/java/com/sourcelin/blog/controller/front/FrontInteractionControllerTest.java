package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.service.interaction.IInteractionCollectService;
import com.sourcelin.blog.service.interaction.IInteractionLikeService;
import com.sourcelin.blog.vo.interaction.InteractionTargetStatVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FrontInteractionControllerTest
{
    @InjectMocks
    private FrontInteractionController controller;

    @Mock
    private IInteractionLikeService interactionLikeService;

    @Mock
    private IInteractionCollectService interactionCollectService;

    @Mock
    private ICollectService collectService;

    @Mock
    private BlogLoginAccessor blogLoginAccessor;

    @Test
    void shouldRejectLikeWhenNotLogin()
    {
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> controller.like("article", 1L));

        assertEquals(ResultCode.UNAUTHORIZED, ex.getResultCode());
        verify(interactionLikeService, never()).like(any(), any(), any());
    }

    @Test
    void shouldDelegateLikeWhenLogin()
    {
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(99L);

        controller.like("article", 12L);

        verify(interactionLikeService).like(99L, "article", 12L);
    }

    @Test
    void shouldReturnEmptyCollectPageWhenNoUser()
    {
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(null);

        PageResult<Collect> page = controller.collects(1, 10, null, null, null, null, null);

        assertEquals(0L, page.getTotal());
        assertEquals(1, page.getPage());
        assertEquals(10, page.getPageSize());
        verify(collectService, never()).selectCollectListWithArticle(any());
    }

    @Test
    void shouldDelegateStatsQuery()
    {
        InteractionTargetStatVO stat = new InteractionTargetStatVO();
        stat.setTargetType("say");
        stat.setTargetId(3L);
        List<InteractionTargetStatVO> expected = Collections.singletonList(stat);
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(7L);
        when(interactionLikeService.queryStats(7L, "say", Arrays.asList(3L, 4L))).thenReturn(expected);

        List<InteractionTargetStatVO> result = controller.stats("say", Arrays.asList(3L, 4L));

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getTargetId());
        verify(interactionLikeService).queryStats(7L, "say", Arrays.asList(3L, 4L));
    }

    @Test
    void shouldDelegateCollectOperations()
    {
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(5L);

        controller.collect("treehole", 9L);
        controller.uncollect("treehole", 9L);

        verify(interactionCollectService).collect(eq(5L), eq("treehole"), eq(9L));
        verify(interactionCollectService).uncollect(eq(5L), eq("treehole"), eq(9L));
    }
}
