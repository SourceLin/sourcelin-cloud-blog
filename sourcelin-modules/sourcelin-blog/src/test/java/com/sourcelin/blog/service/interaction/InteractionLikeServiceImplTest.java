package com.sourcelin.blog.service.interaction;

import com.sourcelin.blog.constant.InteractionTargetTypeEnum;
import com.sourcelin.blog.domain.LikeRecord;
import com.sourcelin.blog.mapper.LikeRecordMapper;
import com.sourcelin.blog.service.BlogInteractionGuard;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.service.interaction.impl.InteractionLikeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InteractionLikeServiceImplTest
{
    @InjectMocks
    private InteractionLikeServiceImpl service;

    @Mock
    private LikeRecordMapper likeRecordMapper;

    @Mock
    private ICollectService collectService;

    @Mock
    private InteractionTargetSupport interactionTargetSupport;

    @Mock
    private BlogInteractionGuard blogInteractionGuard;

    @Test
    void shouldBeIdempotentWhenLikeAlreadyExists()
    {
        LikeRecord active = new LikeRecord();
        active.setId(1L);
        when(interactionTargetSupport.validateTarget("say", 9L)).thenReturn(InteractionTargetTypeEnum.SAY);
        when(blogInteractionGuard.tryInteractionAction(1L, "like")).thenReturn(BlogInteractionGuard.Decision.OK);
        when(likeRecordMapper.selectLikeByUserAndTarget(1L, "say", 9L)).thenReturn(active);

        boolean changed = service.like(1L, "say", 9L);

        assertFalse(changed);
        verify(likeRecordMapper, never()).insertLikeRecord(any());
        verify(interactionTargetSupport, never()).increaseLikeCount(any(), any());
    }

    @Test
    void shouldInsertLikeRecordAndIncreaseCount()
    {
        when(interactionTargetSupport.validateTarget("article", 7L)).thenReturn(InteractionTargetTypeEnum.ARTICLE);
        when(blogInteractionGuard.tryInteractionAction(2L, "like")).thenReturn(BlogInteractionGuard.Decision.OK);
        when(likeRecordMapper.selectLikeByUserAndTarget(2L, "article", 7L)).thenReturn(null);
        when(likeRecordMapper.selectAnyLikeByUserAndTarget(2L, "article", 7L)).thenReturn(null);
        when(likeRecordMapper.insertLikeRecord(any(LikeRecord.class))).thenReturn(1);

        boolean changed = service.like(2L, "article", 7L);

        assertTrue(changed);
        verify(likeRecordMapper).insertLikeRecord(any(LikeRecord.class));
        verify(interactionTargetSupport).increaseLikeCount(InteractionTargetTypeEnum.ARTICLE, 7L);
    }

    @Test
    void shouldBeIdempotentWhenUnlikeNotExists()
    {
        when(interactionTargetSupport.validateTarget("treehole", 3L)).thenReturn(InteractionTargetTypeEnum.TREEHOLE);
        when(blogInteractionGuard.tryInteractionAction(5L, "like")).thenReturn(BlogInteractionGuard.Decision.OK);
        when(likeRecordMapper.selectLikeByUserAndTarget(5L, "treehole", 3L)).thenReturn(null);

        boolean changed = service.unlike(5L, "treehole", 3L);

        assertFalse(changed);
        verify(interactionTargetSupport, never()).decreaseLikeCount(any(), any());
    }

    @Test
    void shouldMarkUnlikeAndDecreaseCount()
    {
        LikeRecord active = new LikeRecord();
        active.setId(8L);
        when(interactionTargetSupport.validateTarget("treehole", 12L)).thenReturn(InteractionTargetTypeEnum.TREEHOLE);
        when(blogInteractionGuard.tryInteractionAction(6L, "like")).thenReturn(BlogInteractionGuard.Decision.OK);
        when(likeRecordMapper.selectLikeByUserAndTarget(6L, "treehole", 12L)).thenReturn(active);
        when(likeRecordMapper.updateLikeRecord(any(LikeRecord.class))).thenReturn(1);

        boolean changed = service.unlike(6L, "treehole", 12L);

        assertTrue(changed);
        verify(likeRecordMapper).updateLikeRecord(any(LikeRecord.class));
        verify(interactionTargetSupport).decreaseLikeCount(eq(InteractionTargetTypeEnum.TREEHOLE), eq(12L));
    }
}
