package com.sourcelin.blog.utils;

import com.sourcelin.blog.config.ModerationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogSensitiveWordToolTest {

    @Mock
    private ModerationProperties moderationProperties;

    @Mock
    private ModerationProperties.Keyword keywordProps;

    private BlogSensitiveWordTool tool;

    @BeforeEach
    void setUp() {
        lenient().when(moderationProperties.getKeyword()).thenReturn(keywordProps);
        lenient().when(keywordProps.getAllow()).thenReturn(Collections.emptyList());
        lenient().when(keywordProps.getBlock()).thenReturn(Collections.emptyList());
        lenient().when(keywordProps.getSuspect()).thenReturn(Collections.emptyList());
        lenient().when(keywordProps.getVersion()).thenReturn(1);
        tool = new BlogSensitiveWordTool(moderationProperties);
        tool.init();
    }

    @Test
    void normalizeNullToEmpty() {
        assertEquals("", tool.normalizeContent(null));
    }

    @Test
    void normalizeTrimsAndRemovesZeroWidth() {
        assertEquals("hello", tool.normalizeContent("\u200B hello \uFEFF"));
    }

    @Test
    void normalizeCollapsesWhitespace() {
        assertEquals("a b", tool.normalizeContent("  a   b  "));
    }

    @Test
    void matchWordTreeDetectsBlockWord() {
        when(keywordProps.getBlock()).thenReturn(Collections.singletonList("违禁"));
        tool.rebuildFromProperties();
        assertTrue(tool.matchWordTree(tool.getBlockTree(), tool.normalizeContent("含违禁词")).size() > 0);
    }
}
