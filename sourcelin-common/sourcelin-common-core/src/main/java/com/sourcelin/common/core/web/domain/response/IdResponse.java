package com.sourcelin.common.core.web.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 平台统一 ID 响应结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdResponse implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;

    public static IdResponse of(Long id)
    {
        return IdResponse.builder()
            .id(id)
            .build();
    }
}
