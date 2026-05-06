package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.constant.ArticleConstants;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Category;
import com.sourcelin.blog.domain.Tag;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ICategoryService;
import com.sourcelin.blog.service.ITagService;
import com.sourcelin.blog.vo.ArticleVO;
import com.sourcelin.blog.vo.HotCategoryVO;
import com.sourcelin.blog.vo.HotSearchVO;
import com.sourcelin.common.core.utils.PageUtils;
import com.sourcelin.common.core.web.domain.response.ListResult;
import com.sourcelin.common.core.web.domain.response.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/front/hot")
public class FrontHotController
{
    @Autowired
    private IArticleService articleService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ITagService tagService;

    @GetMapping("/articles")
    public PageResult<ArticleVO> articles(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                          @RequestParam(value = "categoryId", required = false) Long categoryId)
    {
        page = PageUtils.clampPage(page);
        pageSize = PageUtils.clampPageSize(pageSize);
        PageHelper.startPage(page, pageSize);
        Article filter = new Article();
        filter.setStatus(ArticleConstants.STATUS_PUBLISHED);
        filter.setCategoryId(categoryId);
        filter.getParams().put("orderBy", "((ifnull(a.view_count,0)) + (ifnull(a.like_count,0) * 5) + (ifnull(a.collect_count,0) * 6) + ((select count(1) from b_comment bc where bc.deleted = 0 and bc.article_id = a.id and (bc.source is null or bc.source = 'article')) * 8))");
        List<ArticleVO> items = articleService.selectArticleVoList(filter);
        PageInfo<ArticleVO> pageInfo = new PageInfo<ArticleVO>(items);
        return BlogPageResults.of(items, pageInfo, page, pageSize);
    }

    @GetMapping("/categories")
    public ListResult<HotCategoryVO> categories()
    {
        List<Category> rows = categoryService.selectCategoryAll();
        rows.sort(Comparator.comparing(Category::getClickCount, Comparator.nullsLast(Comparator.reverseOrder())));
        List<HotCategoryVO> items = new ArrayList<HotCategoryVO>();
        for (Category category : rows)
        {
            HotCategoryVO item = new HotCategoryVO();
            item.setId(category.getId());
            item.setName(category.getName());
            item.setCount(category.getClickCount() == null ? 0L : category.getClickCount());
            items.add(item);
        }
        return ListResult.of(items);
    }

    @GetMapping("/searches")
    public ListResult<HotSearchVO> searches()
    {
        List<Tag> rows = tagService.selectTagAll();
        rows.sort(Comparator.comparing(Tag::getClickCount, Comparator.nullsLast(Comparator.reverseOrder())));
        List<HotSearchVO> items = new ArrayList<HotSearchVO>();
        long id = 1L;
        for (Tag tag : rows)
        {
            HotSearchVO item = new HotSearchVO();
            item.setId(id++);
            item.setKeyword(tag.getName());
            item.setCount(tag.getClickCount() == null ? 0L : tag.getClickCount());
            items.add(item);
            if (items.size() >= 10)
            {
                break;
            }
        }
        return ListResult.of(items);
    }
}
