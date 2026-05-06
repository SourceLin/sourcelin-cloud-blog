package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Category;
import com.sourcelin.blog.domain.Tag;
import com.sourcelin.blog.service.IArticleReadAuthService;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ICategoryService;
import com.sourcelin.blog.service.ITagService;
import com.sourcelin.blog.service.IWebConfigService;
import com.sourcelin.blog.support.FrontBlogProjectionSupport;
import com.sourcelin.blog.vo.ArticleVO;
import com.sourcelin.blog.vo.FrontHomeVO;
import com.sourcelin.common.core.utils.PageUtils;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Front home aggregation endpoints.
 */
@RestController
@RequestMapping("/front/home")
public class FrontHomeController
{
    @Autowired
    private IArticleService articleService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ITagService tagService;
    @Autowired
    private IWebConfigService webConfigService;
    @Autowired
    private IArticleReadAuthService articleReadAuthService;
    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    @GetMapping
    public FrontHomeVO home(@RequestParam(value = "page", defaultValue = "1") Integer page,
                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                            @RequestParam(value = "categoryId", required = false) Long categoryId)
    {
        page = PageUtils.clampPage(page);
        pageSize = PageUtils.clampPageSize(pageSize);
        Article latestFilter = new Article();
        latestFilter.setStatus(2L);
        if (categoryId != null)
        {
            latestFilter.setCategoryId(categoryId);
        }
        PageHelper.startPage(page, pageSize);
        List<ArticleVO> latestList = articleService.selectArticleVoList(latestFilter);
        PageInfo<ArticleVO> latestPageInfo = new PageInfo<ArticleVO>(latestList);
        PageHelper.clearPage();

        Article recommendFilter = new Article();
        recommendFilter.setStatus(2L);
        recommendFilter.setIsRecommend(1);
        PageHelper.startPage(1, 6);
        List<ArticleVO> recommendList = articleService.selectArticleVoList(recommendFilter);
        PageHelper.clearPage();

        Article topFilter = new Article();
        topFilter.setStatus(2L);
        topFilter.setIsTop(1);
        PageHelper.startPage(1, 5);
        List<ArticleVO> topList = articleService.selectArticleVoList(topFilter);
        PageHelper.clearPage();

        List<Category> categories = categoryService.selectCategoryAll();
        List<Tag> tags = tagService.selectTagAll();
        Long viewerId = blogLoginAccessor.getCurrentUserId();
        applyContentVisibility(latestList, viewerId);
        applyContentVisibility(recommendList, viewerId);
        applyContentVisibility(topList, viewerId);

        FrontHomeVO home = new FrontHomeVO();
        home.setSiteInfo(FrontBlogProjectionSupport.buildSiteInfo(webConfigService.getWebConfig()));
        home.setLatest(BlogPageResults.of(latestList, latestPageInfo, page, pageSize));
        home.setRecommend(recommendList);
        home.setCarousel(topList);
        home.setCategories(categories);
        home.setTags(tags);
        return home;
    }

    private void applyContentVisibility(List<ArticleVO> list, Long viewerId)
    {
        if (list == null)
        {
            return;
        }
        for (ArticleVO vo : list)
        {
            articleReadAuthService.applyContentVisibilityVo(vo, viewerId);
        }
    }
}
