package com.sourcelin.blog.shared.support;

import com.github.pagehelper.PageInfo;
import com.sourcelin.common.core.web.domain.response.PageResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlogPageResultsTest
{
    @Test
    void shouldCreatePageResultFromPageInfo()
    {
        List<String> items = Arrays.asList("a", "b");
        PageInfo<String> pageInfo = new PageInfo<String>();
        pageInfo.setTotal(21L);
        pageInfo.setPageNum(2);
        pageInfo.setPageSize(10);

        PageResult<String> result = BlogPageResults.of(items, pageInfo);

        assertEquals(items, result.getItems());
        assertEquals(21L, result.getTotal());
        assertEquals(2, result.getPage());
        assertEquals(10, result.getPageSize());
        assertEquals(3, result.getTotalPages());
    }

    @Test
    void shouldCreatePageResultWithExplicitPageParameters()
    {
        List<String> items = Arrays.asList("a", "b");
        PageInfo<String> pageInfo = new PageInfo<String>();
        pageInfo.setTotal(11L);

        PageResult<String> result = BlogPageResults.of(items, pageInfo, 2, 5);

        assertEquals(items, result.getItems());
        assertEquals(11L, result.getTotal());
        assertEquals(2, result.getPage());
        assertEquals(5, result.getPageSize());
        assertEquals(3, result.getTotalPages());
    }
}
