package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Category;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ICategoryService;
import com.sourcelin.blog.vo.ArticleVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.ListResult;
import com.sourcelin.common.core.web.domain.response.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front/categories")
public class FrontCategoryController
{
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IArticleService articleService;

    @GetMapping
    public ListResult<Category> list()
    {
        return ListResult.of(categoryService.selectCategoryAll());
    }

    @GetMapping("/{id}")
    public Category detail(@PathVariable("id") Long id)
    {
        Category category = categoryService.selectCategoryById(id);
        if (category == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }
        return category;
    }

    @GetMapping("/articles/{id}")
    public PageResult<ArticleVO> articles(@PathVariable("id") Long id,
                                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        PageHelper.startPage(page, pageSize);
        Article filter = new Article();
        filter.setCategoryId(id);
        filter.setStatus(2L);
        List<ArticleVO> articles = articleService.selectArticleVoList(filter);
        PageInfo<ArticleVO> pageInfo = new PageInfo<ArticleVO>(articles);
        return BlogPageResults.of(articles, pageInfo, page, pageSize);
    }

    @GetMapping("/search")
    public PageResult<Category> search(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "page", defaultValue = "1") Integer page,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        PageHelper.startPage(page, pageSize);
        Category filter = new Category();
        filter.setName(keyword);
        List<Category> items = categoryService.selectCategoryList(filter);
        PageInfo<Category> pageInfo = new PageInfo<Category>(items);
        return BlogPageResults.of(items, pageInfo, page, pageSize);
    }
}
