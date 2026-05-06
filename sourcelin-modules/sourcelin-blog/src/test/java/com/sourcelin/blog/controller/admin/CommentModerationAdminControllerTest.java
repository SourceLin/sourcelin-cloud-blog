package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.config.ModerationProperties;
import com.sourcelin.blog.utils.BlogSensitiveWordTool;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentModerationAdminControllerTest
{
    @InjectMocks
    private CommentModerationAdminController controller;

    @Mock
    private ModerationProperties moderationProperties;

    @Mock
    private BlogSensitiveWordTool blogSensitiveWordTool;

    @Test
    void shouldReturnTypedCurrentKeywordBody()
    {
        ModerationProperties.Article article = new ModerationProperties.Article();
        article.setAutoPassOnClean(false);
        ModerationProperties.Keyword keyword = new ModerationProperties.Keyword();
        keyword.setVersion(3);
        keyword.setAllow(Collections.singletonList("白名单"));
        keyword.setBlock(Collections.singletonList("违禁词"));
        keyword.setSuspect(Collections.singletonList("灰词"));

        when(moderationProperties.getArticle()).thenReturn(article);
        when(moderationProperties.getKeyword()).thenReturn(keyword);
        when(moderationProperties.isAiEnabled()).thenReturn(true);
        when(blogSensitiveWordTool.getCurrentVersion()).thenReturn(5);

        CommentModerationAdminController.KeywordCurrentVO result = controller.currentKeyword();

        assertEquals(Integer.valueOf(3), result.getVersion());
        assertEquals(Collections.singletonList("白名单"), result.getAllow());
        assertEquals(Collections.singletonList("违禁词"), result.getBlock());
        assertEquals(Collections.singletonList("灰词"), result.getSuspect());
        assertEquals(Integer.valueOf(5), result.getLoadedVersion());
        assertEquals(Boolean.FALSE, result.getAutoPassOnClean());
        assertEquals(Boolean.TRUE, result.getAiEnabled());
    }

    @Test
    void shouldRefreshKeywordCacheByTypedBody()
    {
        CommentModerationAdminController.KeywordSaveBody body = new CommentModerationAdminController.KeywordSaveBody();
        body.setVersion(7);
        body.setAllow(Collections.singletonList("allow"));
        body.setBlock(Arrays.asList("block-a", "block-b"));

        Void result = controller.saveKeyword(body);

        assertNull(result);
        ArgumentCaptor<Map<String, java.util.List<String>>> keywordCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Integer> versionCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(blogSensitiveWordTool).refreshKeywordCache(keywordCaptor.capture(), versionCaptor.capture());
        assertEquals(Collections.singletonList("allow"), keywordCaptor.getValue().get("allow"));
        assertEquals(Arrays.asList("block-a", "block-b"), keywordCaptor.getValue().get("block"));
        assertEquals(Integer.valueOf(7), versionCaptor.getValue());
    }

    @Test
    void shouldRejectNullKeywordBody()
    {
        BusinessException ex = assertThrows(BusinessException.class, () -> controller.saveKeyword(null));

        assertEquals(ResultCode.VALIDATION_ERROR, ex.getResultCode());
        assertEquals("参数不能为空", ex.getMessage());
    }
}
