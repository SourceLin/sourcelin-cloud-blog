package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.constant.ArticleConstants;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.domain.Tag;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ITagService;
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
@RequestMapping("/front/tags")
public class FrontTagController
{
    @Autowired
    private ITagService tagService;
    @Autowired
    private IArticleService articleService;

    @GetMapping
    public ListResult<Tag> list()
    {
        return ListResult.of(tagService.selectTagAll());
    }

    @GetMapping("/{id}")
    public Tag detail(@PathVariable("id") Long id)
    {
        Tag tag = tagService.selectTagById(id);
        if (tag == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "标签不存在");
        }
        return tag;
    }

    @GetMapping("/articles/{id}")
    public PageResult<ArticleVO> articles(@PathVariable("id") String id,
                                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        PageHelper.startPage(page, pageSize);
        Article filter = new Article();
        filter.setTagId(id);
        filter.setStatus(ArticleConstants.STATUS_PUBLISHED);
        List<ArticleVO> articles = articleService.selectArticleVoList(filter);
        PageInfo<ArticleVO> pageInfo = new PageInfo<ArticleVO>(articles);
        return BlogPageResults.of(articles, pageInfo, page, pageSize);
    }

    @GetMapping("/search")
    public PageResult<Tag> search(@RequestParam(value = "keyword", required = false) String keyword,
                                  @RequestParam(value = "page", defaultValue = "1") Integer page,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        PageHelper.startPage(page, pageSize);
        Tag filter = new Tag();
        filter.setName(keyword);
        List<Tag> items = tagService.selectTagList(filter);
        PageInfo<Tag> pageInfo = new PageInfo<Tag>(items);
        return BlogPageResults.of(items, pageInfo, page, pageSize);
    }
}
