package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.constant.ArticleConstants;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Category;
import com.sourcelin.blog.domain.Tag;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ICategoryService;
import com.sourcelin.blog.service.ITagService;
import com.sourcelin.blog.vo.ArticleVO;
import com.sourcelin.common.core.web.domain.response.ListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/front/search")
public class FrontSearchController
{
    @Autowired
    private IArticleService articleService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ITagService tagService;

    @GetMapping("/hot")
    public ListResult<String> hot()
    {
        Article filter = new Article();
        filter.setStatus(ArticleConstants.STATUS_PUBLISHED);
        filter.getParams().put("orderBy", "a.view_count");
        List<ArticleVO> rows = articleService.selectArticleVoList(filter);
        List<String> result = new ArrayList<String>();
        for (ArticleVO row : rows)
        {
            if (row.getTitle() != null && !result.contains(row.getTitle()))
            {
                result.add(row.getTitle());
            }
            if (result.size() >= 10)
            {
                break;
            }
        }
        return ListResult.of(result);
    }

    @GetMapping("/suggestions")
    public ListResult<String> suggestions(@RequestParam("keyword") String keyword)
    {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        if (safeKeyword.isEmpty())
        {
            return ListResult.of(new ArrayList<String>());
        }
        Set<String> result = new LinkedHashSet<String>();

        Article article = new Article();
        article.setStatus(ArticleConstants.STATUS_PUBLISHED);
        article.setTitle(safeKeyword);
        for (ArticleVO row : articleService.selectArticleVoList(article))
        {
            if (row.getTitle() != null)
            {
                result.add(row.getTitle());
            }
            if (result.size() >= 10)
            {
                return ListResult.of(new ArrayList<String>(result));
            }
        }

        Category category = new Category();
        category.setName(safeKeyword);
        for (Category row : categoryService.selectCategoryList(category))
        {
            if (row.getName() != null)
            {
                result.add(row.getName());
            }
            if (result.size() >= 10)
            {
                return ListResult.of(new ArrayList<String>(result));
            }
        }

        Tag tag = new Tag();
        tag.setName(safeKeyword);
        for (Tag row : tagService.selectTagList(tag))
        {
            if (row.getName() != null)
            {
                result.add(row.getName());
            }
            if (result.size() >= 10)
            {
                break;
            }
        }
        return ListResult.of(new ArrayList<String>(result));
    }
}
