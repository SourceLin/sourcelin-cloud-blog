package com.sourcelin.common.security.accessor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BlogLoginAccessorTest
{
    @Test
    void shouldReturnNullUserIdWhenNotLoggedIn()
    {
        TestBlogLoginAccessor accessor = new TestBlogLoginAccessor(false, 3L);

        assertNull(accessor.getCurrentUserId());
    }

    @Test
    void shouldReturnUserIdWhenLoggedIn()
    {
        TestBlogLoginAccessor accessor = new TestBlogLoginAccessor(true, 3L);

        assertEquals(Long.valueOf(3L), accessor.getCurrentUserId());
    }

    private static class TestBlogLoginAccessor extends BlogLoginAccessor
    {
        private final boolean login;
        private final Long userId;

        private TestBlogLoginAccessor(boolean login, Long userId)
        {
            this.login = login;
            this.userId = userId;
        }

        @Override
        public boolean isLogin()
        {
            return login;
        }

        @Override
        protected Long getLoginIdAsLong()
        {
            return userId;
        }
    }
}
