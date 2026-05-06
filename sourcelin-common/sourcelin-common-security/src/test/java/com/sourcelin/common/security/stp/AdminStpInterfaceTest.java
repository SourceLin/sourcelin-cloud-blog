package com.sourcelin.common.security.stp;

import com.sourcelin.system.api.model.SysLoginUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AdminStpInterfaceTest
{
    @Test
    void shouldNotBeLimitedToSystemApplicationOnly()
    {
        assertNull(
            AdminStpInterface.class.getAnnotation(ConditionalOnProperty.class),
            "Admin permission provider must be available in every management service"
        );
    }

    @Test
    void shouldResolveRolesAndPermissionsFromSessionLoginUser()
    {
        SysLoginUser loginUser = new SysLoginUser();
        loginUser.setRoles(new HashSet<>(Collections.singletonList("admin")));
        loginUser.setPermissions(new HashSet<>(Arrays.asList("system:user:list", "system:user:add")));
        TestAdminStpInterface stpInterface = new TestAdminStpInterface(loginUser);

        assertEquals(1, stpInterface.getRoleList(1L, "admin").size());
        assertEquals(2, stpInterface.getPermissionList(1L, "admin").size());
    }

    @Test
    void shouldResolveRolesAndPermissionsFromSessionMap()
    {
        Map<String, Object> loginUserMap = new LinkedHashMap<>();
        loginUserMap.put("roles", Arrays.asList("admin", "ops"));
        loginUserMap.put("permissions", Arrays.asList("*:*:*", "system:role:list"));
        TestAdminStpInterface stpInterface = new TestAdminStpInterface(loginUserMap);

        assertEquals(2, stpInterface.getRoleList(1L, "admin").size());
        assertEquals(2, stpInterface.getPermissionList(1L, "admin").size());
    }

    @Test
    void shouldReturnEmptyListsWhenSessionLoginUserMissing()
    {
        TestAdminStpInterface stpInterface = new TestAdminStpInterface(null);

        assertEquals(Collections.emptyList(), stpInterface.getRoleList(1L, "admin"));
        assertEquals(Collections.emptyList(), stpInterface.getPermissionList(1L, "admin"));
    }

    private static class TestAdminStpInterface extends AdminStpInterface
    {
        private final Object loginUser;

        private TestAdminStpInterface(Object loginUser)
        {
            this.loginUser = loginUser;
        }

        @Override
        protected Object getSessionValue(Object loginId, String key)
        {
            return loginUser;
        }
    }
}
