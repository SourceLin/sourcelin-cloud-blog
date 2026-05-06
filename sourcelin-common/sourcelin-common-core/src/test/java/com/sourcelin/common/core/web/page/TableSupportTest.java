package com.sourcelin.common.core.web.page;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TableSupportTest
{
    @AfterEach
    void tearDown()
    {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldReadUnifiedPageAndSortParameters()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("page", "3");
        request.setParameter("pageSize", "15");
        request.setParameter("sortBy", "createTime");
        request.setParameter("sortOrder", "desc");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        PageDomain pageDomain = TableSupport.getPageDomain();

        assertEquals(3, pageDomain.getPage());
        assertEquals(15, pageDomain.getPageSize());
        assertEquals("create_time desc", pageDomain.getOrderBy());
    }
}