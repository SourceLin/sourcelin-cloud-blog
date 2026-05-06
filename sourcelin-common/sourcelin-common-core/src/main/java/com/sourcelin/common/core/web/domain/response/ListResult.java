package com.sourcelin.common.core.web.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 平台统一列表结果。
 *
 * @param <T> 列表项类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListResult<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private List<T> items = Collections.emptyList();

    public static <T> ListResult<T> of(List<T> items)
    {
        return ListResult.<T>builder()
            .items(items == null ? Collections.<T>emptyList() : items)
            .build();
    }
}
