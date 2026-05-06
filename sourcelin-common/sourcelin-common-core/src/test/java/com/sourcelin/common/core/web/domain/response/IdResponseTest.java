package com.sourcelin.common.core.web.domain.response;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IdResponseTest
{
    @Test
    void shouldCreateIdResponseWithId()
    {
        IdResponse response = IdResponse.of(1001L);

        assertEquals(1001L, response.getId());
    }

    @Test
    void shouldWrapPageResultAndIdResponseWithApiResponse()
    {
        PageResult<String> pageResult = PageResult.of(Arrays.asList("a", "b"), 12L, 2, 10);
        ApiResponse<PageResult<String>> pageResponse = ApiResponse.success(pageResult);
        ApiResponse<IdResponse> idResponse = ApiResponse.success(IdResponse.of(1001L));

        assertEquals(0, pageResponse.getCode());
        assertEquals(12L, pageResponse.getData().getTotal());
        assertEquals(2, pageResponse.getData().getItems().size());
        assertEquals(0, idResponse.getCode());
        assertEquals(1001L, idResponse.getData().getId());
        assertNotNull(pageResponse.getRequestId());
        assertNotNull(idResponse.getTimestamp());
    }
}
