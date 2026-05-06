package com.sourcelin.blog.service;

import cn.hutool.dfa.WordTree;
import com.sourcelin.blog.config.ModerationProperties;
import com.sourcelin.blog.constant.ModerationDecisionEnum;
import com.sourcelin.blog.utils.BlogSensitiveWordTool;
import com.sourcelin.blog.vo.ModerationOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModerationPipelineTest {

    @Mock
    private ModerationProperties moderationProperties;

    @Mock
    private ModerationProperties.Article articleProps;

    @Mock
    private ModerationProperties.Keyword keywordProps;

    @Mock
    private BlogSensitiveWordTool sensitiveWordTool;

    private final WordTree allowTree = new WordTree();
    private final WordTree blockTree = new WordTree();
    private final WordTree suspectTree = new WordTree();

    private ModerationPipeline pipeline;

    @BeforeEach
    void setUp() {
        lenient().when(moderationProperties.getArticle()).thenReturn(articleProps);
        lenient().when(moderationProperties.getKeyword()).thenReturn(keywordProps);
        lenient().when(keywordProps.getBlock()).thenReturn(Collections.emptyList());
        lenient().when(sensitiveWordTool.getAllowTree()).thenReturn(allowTree);
        lenient().when(sensitiveWordTool.getBlockTree()).thenReturn(blockTree);
        lenient().when(sensitiveWordTool.getSuspectTree()).thenReturn(suspectTree);
        lenient().when(sensitiveWordTool.normalizeContent(anyString())).thenAnswer(inv -> inv.getArgument(0));
        pipeline = new ModerationPipeline(moderationProperties, sensitiveWordTool);
    }

    @Test
    void cleanContentAutoPassOn() {
        when(articleProps.isAutoPassOnClean()).thenReturn(true);
        when(sensitiveWordTool.matchWordTree(any(WordTree.class), anyString())).thenReturn(Collections.emptyList());

        ModerationOutcome out = pipeline.moderate("hello");
        assertEquals(ModerationDecisionEnum.PASS, out.getDecision());
        assertEquals(1, out.getStatus());
    }

    @Test
    void cleanContentAutoPassOffGoesReview() {
        when(articleProps.isAutoPassOnClean()).thenReturn(false);
        when(sensitiveWordTool.matchWordTree(any(WordTree.class), anyString())).thenReturn(Collections.emptyList());

        ModerationOutcome out = pipeline.moderate("hello");
        assertEquals(ModerationDecisionEnum.REVIEW, out.getDecision());
        assertEquals(0, out.getStatus());
    }

    @Test
    void blockHitsReject() {
        blockTree.addWord("bad");
        when(articleProps.isAutoPassOnClean()).thenReturn(true);
        when(sensitiveWordTool.matchWordTree(any(WordTree.class), anyString())).thenAnswer(inv -> {
            WordTree tree = inv.getArgument(0);
            String text = inv.getArgument(1);
            return tree.matchAll(text);
        });

        ModerationOutcome out = pipeline.moderate("bad");
        assertEquals(ModerationDecisionEnum.REJECT, out.getDecision());
        assertEquals(2, out.getStatus());
    }
}
