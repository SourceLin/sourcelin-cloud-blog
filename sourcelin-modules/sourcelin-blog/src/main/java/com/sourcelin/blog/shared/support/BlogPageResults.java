package com.sourcelin.blog.shared.support;

import com.github.pagehelper.PageInfo;
import com.sourcelin.common.core.web.domain.response.PageResult;

import java.util.List;

/**
 * 博客模块分页响应工厂。
 */
public final class BlogPageResults
{
    private BlogPageResults()
    {
    }

    public static <T> PageResult<T> of(List<T> items, PageInfo<?> pageInfo)
    {
        return PageResult.of(items, pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    public static <T> PageResult<T> of(List<T> items, PageInfo<?> pageInfo, int page, int pageSize)
    {
        return PageResult.of(items, pageInfo.getTotal(), page, pageSize);
    }
}
