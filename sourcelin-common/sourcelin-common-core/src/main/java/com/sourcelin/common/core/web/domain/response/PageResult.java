package com.sourcelin.common.core.web.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 平台统一分页结果。
 *
 * @param <T> 列表项类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private List<T> items = Collections.emptyList();

    private Long total;

    private Integer page;

    private Integer pageSize;

    private Integer totalPages;

    public static <T> PageResult<T> of(List<T> items, long total, int page, int pageSize)
    {
        int totalPages = total == 0 ? 0 : (int) Math.ceil((double) total / (double) pageSize);
        return PageResult.<T>builder()
            .items(items == null ? Collections.<T>emptyList() : items)
            .total(total)
            .page(page)
            .pageSize(pageSize)
            .totalPages(totalPages)
            .build();
    }

    public static <T> PageResult<T> empty(int page, int pageSize)
    {
        return PageResult.<T>builder()
            .items(Collections.<T>emptyList())
            .total(0L)
            .page(page)
            .pageSize(pageSize)
            .totalPages(0)
            .build();
    }
}
