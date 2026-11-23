package com.sourcelin.common.security.config;

import cn.dev33.satoken.json.SaJsonTemplateForJackson3;
import cn.dev33.satoken.session.SaSession;
import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.api.model.LoginUser;
import com.sourcelin.system.api.domain.SysDept;
import com.sourcelin.system.api.domain.SysRole;
import com.sourcelin.system.api.domain.SysUser;
import com.sourcelin.system.api.model.SysLoginUser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SaTokenSessionSerializationTest
{
    @Test
    void readsAdminPermissionsAndBlogIdentityFromSerializedSessions()
    {
        SysUser admin = new SysUser();
        admin.setDept(new SysDept());
        admin.setRoles(List.of(new SysRole()));
        SysLoginUser adminLogin = new SysLoginUser();
        adminLogin.setSysUser(admin);
        adminLogin.setPermissions(Set.of("system:user:list"));

        User author = new User();
        author.setId(42L);
        LoginUser blogLogin = new LoginUser();
        blogLogin.setUser(author);

        assertEquals(Set.of("system:user:list"),
                assertInstanceOf(SysLoginUser.class, deserialize(adminLogin)).getPermissions());
        assertEquals(42L,
                assertInstanceOf(LoginUser.class, deserialize(blogLogin)).getUser().getId());
    }

    private Object deserialize(Object loginUser)
    {
        SaSession session = new SaSession("test-session");
        session.getDataMap().put("loginUser", loginUser);
        SaJsonTemplateForJackson3 json = new SaJsonTemplateForJackson3();
        return json.jsonToObject(json.objectToJson(session), SaSession.class).get("loginUser");
    }
}
