package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Category;
import com.sourcelin.blog.domain.Tag;
import com.sourcelin.blog.service.IArticleReadAuthService;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ICategoryService;
import com.sourcelin.blog.service.ITagService;
import com.sourcelin.blog.service.IWebConfigService;
import com.sourcelin.blog.vo.ArticleVO;
import com.sourcelin.blog.vo.FrontHomeVO;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FrontHomeControllerTest {

    @InjectMocks
    private FrontHomeController controller;

    @Mock
    private IArticleService articleService;

    @Mock
    private ICategoryService categoryService;

    @Mock
    private ITagService tagService;

    @Mock
    private IWebConfigService webConfigService;

    @Mock
    private IArticleReadAuthService articleReadAuthService;

    @Mock
    private BlogLoginAccessor blogLoginAccessor;

    @Test
    void shouldPassCategoryIdToLatestFilterAndReturnPagedLatestFromHome() {
        ArticleVO latest = new ArticleVO();
        latest.setId(9L);
        latest.setCategoryId(18L);

        when(articleService.selectArticleVoList(any(Article.class))).thenReturn(
                Collections.singletonList(latest),
                Collections.<ArticleVO>emptyList(),
                Collections.<ArticleVO>emptyList());
        when(categoryService.selectCategoryAll()).thenReturn(Collections.<Category>emptyList());
        when(tagService.selectTagAll()).thenReturn(Collections.<Tag>emptyList());

        FrontHomeVO home = controller.home(1, 10, 18L);

        ArgumentCaptor<Article> latestCaptor = ArgumentCaptor.forClass(Article.class);
        verify(articleService, times(3)).selectArticleVoList(latestCaptor.capture());
        assertEquals(Long.valueOf(18L), latestCaptor.getAllValues().get(0).getCategoryId());
        assertEquals(1, home.getLatest().getPage());
        assertEquals(10, home.getLatest().getPageSize());
        assertEquals(1, home.getLatest().getItems().size());
        assertSame(latest, home.getLatest().getItems().get(0));
    }

    @Test
    void shouldApplyReadVisibilityToAllAggregatedArticleLists() {
        ArticleVO latest = new ArticleVO();
        latest.setId(1L);
        ArticleVO recommend = new ArticleVO();
        recommend.setId(2L);
        ArticleVO carousel = new ArticleVO();
        carousel.setId(3L);

        when(articleService.selectArticleVoList(any(Article.class))).thenReturn(
                Collections.singletonList(latest),
                Collections.singletonList(recommend),
                Collections.singletonList(carousel));
        when(categoryService.selectCategoryAll()).thenReturn(Collections.<Category>emptyList());
        when(tagService.selectTagAll()).thenReturn(Collections.<Tag>emptyList());
        when(blogLoginAccessor.getCurrentUserId()).thenReturn(7L);

        controller.home(1, 10, null);

        verify(articleReadAuthService).applyContentVisibilityVo(eq(latest), eq(7L));
        verify(articleReadAuthService).applyContentVisibilityVo(eq(recommend), eq(7L));
        verify(articleReadAuthService).applyContentVisibilityVo(eq(carousel), eq(7L));
        verify(articleReadAuthService, times(3)).applyContentVisibilityVo(any(ArticleVO.class), eq(7L));
    }
}
