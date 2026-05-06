package com.sourcelin.blog.service.interaction;

import com.sourcelin.blog.constant.InteractionTargetTypeEnum;
import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.service.BlogInteractionGuard;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.service.interaction.impl.InteractionCollectServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InteractionCollectServiceImplTest
{
    @InjectMocks
    private InteractionCollectServiceImpl service;

    @Mock
    private ICollectService collectService;

    @Mock
    private InteractionTargetSupport interactionTargetSupport;

    @Mock
    private BlogInteractionGuard blogInteractionGuard;

    @Test
    void shouldBeIdempotentWhenCollectAlreadyExists()
    {
        Collect active = new Collect();
        active.setId(1L);
        when(interactionTargetSupport.validateTarget("article", 2L)).thenReturn(InteractionTargetTypeEnum.ARTICLE);
        when(blogInteractionGuard.tryInteractionAction(9L, "collect")).thenReturn(BlogInteractionGuard.Decision.OK);
        when(collectService.selectCollectByUserAndTarget(9L, "article", 2L)).thenReturn(active);

        boolean changed = service.collect(9L, "article", 2L);

        assertFalse(changed);
        verify(collectService, never()).insertCollect(any());
        verify(interactionTargetSupport, never()).increaseCollectCount(any(), any());
    }

    @Test
    void shouldRestoreDeletedCollectRecord()
    {
        Collect history = new Collect();
        history.setId(10L);
        history.setDeleted(1);
        when(interactionTargetSupport.validateTarget("say", 3L)).thenReturn(InteractionTargetTypeEnum.SAY);
        when(blogInteractionGuard.tryInteractionAction(8L, "collect")).thenReturn(BlogInteractionGuard.Decision.OK);
        when(collectService.selectCollectByUserAndTarget(8L, "say", 3L)).thenReturn(null);
        when(collectService.selectCollectList(any(Collect.class))).thenReturn(Collections.singletonList(history));
        when(collectService.updateCollect(any(Collect.class))).thenReturn(1);

        boolean changed = service.collect(8L, "say", 3L);

        assertTrue(changed);
        verify(collectService).updateCollect(any(Collect.class));
        verify(interactionTargetSupport).increaseCollectCount(InteractionTargetTypeEnum.SAY, 3L);
    }

    @Test
    void shouldBeIdempotentWhenUncollectNotExists()
    {
        when(interactionTargetSupport.validateTarget("treehole", 7L)).thenReturn(InteractionTargetTypeEnum.TREEHOLE);
        when(blogInteractionGuard.tryInteractionAction(5L, "collect")).thenReturn(BlogInteractionGuard.Decision.OK);
        when(collectService.selectCollectByUserAndTarget(5L, "treehole", 7L)).thenReturn(null);

        boolean changed = service.uncollect(5L, "treehole", 7L);

        assertFalse(changed);
        verify(interactionTargetSupport, never()).decreaseCollectCount(any(), any());
    }

    @Test
    void shouldMarkUncollectAndDecreaseCount()
    {
        Collect active = new Collect();
        active.setId(6L);
        active.setDeleted(0);
        when(interactionTargetSupport.validateTarget("treehole", 7L)).thenReturn(InteractionTargetTypeEnum.TREEHOLE);
        when(blogInteractionGuard.tryInteractionAction(5L, "collect")).thenReturn(BlogInteractionGuard.Decision.OK);
        when(collectService.selectCollectByUserAndTarget(5L, "treehole", 7L)).thenReturn(active);
        when(collectService.updateCollect(any(Collect.class))).thenReturn(1);

        boolean changed = service.uncollect(5L, "treehole", 7L);

        assertTrue(changed);
        verify(collectService).updateCollect(any(Collect.class));
        verify(interactionTargetSupport).decreaseCollectCount(InteractionTargetTypeEnum.TREEHOLE, 7L);
    }
}
