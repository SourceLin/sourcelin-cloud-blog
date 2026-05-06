package com.sourcelin.common.security.accessor;

import com.sourcelin.system.api.model.SysLoginUser;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class AdminLoginAccessorTest
{
    @Test
    void shouldExposeCurrentLoginUserAccessor()
    {
        Method method = findMethod("getCurrentLoginUser");
        assertNotNull(method, "Admin accessor must expose session-backed login user access");
    }

    private Method findMethod(String name, Class<?>... parameterTypes)
    {
        try
        {
            return AdminLoginAccessor.class.getDeclaredMethod(name, parameterTypes);
        }
        catch (NoSuchMethodException ignored)
        {
            return null;
        }
    }

    @Test
    void shouldReturnNullValuesWhenNotLoggedIn()
    {
        TestAdminLoginAccessor accessor = new TestAdminLoginAccessor(false, 1L, "1", null);

        assertNull(accessor.getCurrentUserId());
        assertNull(accessor.getCurrentLoginUser());
        assertNull(accessor.getUsername());
    }

    @Test
    void shouldReadUsernameFromSessionLoginUser()
    {
        SysLoginUser loginUser = new SysLoginUser();
        loginUser.setUserid(7L);
        loginUser.setUsername("admin");
        TestAdminLoginAccessor accessor = new TestAdminLoginAccessor(true, 7L, "7", loginUser);

        assertEquals(Long.valueOf(7L), accessor.getCurrentUserId());
        assertSame(loginUser, accessor.getCurrentLoginUser());
        assertEquals("admin", accessor.getUsername());
    }

    @Test
    void shouldFallbackToLoginIdWhenSessionLoginUserMissing()
    {
        TestAdminLoginAccessor accessor = new TestAdminLoginAccessor(true, 9L, "9", null);

        assertEquals("9", accessor.getUsername());
    }

    private static class TestAdminLoginAccessor extends AdminLoginAccessor
    {
        private final boolean login;
        private final Long userId;
        private final String loginId;
        private final SysLoginUser loginUser;

        private TestAdminLoginAccessor(boolean login, Long userId, String loginId, SysLoginUser loginUser)
        {
            this.login = login;
            this.userId = userId;
            this.loginId = loginId;
            this.loginUser = loginUser;
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

        @Override
        protected String getLoginIdAsString()
        {
            return loginId;
        }

        @Override
        protected Object getSessionValue(String key)
        {
            return loginUser;
        }
    }
}
