package com.sourcelin.common.security.stp;

import cn.dev33.satoken.session.SaSession;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.system.api.model.SysLoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sa-Token admin 角色和权限加载接口。
 *
 * @author sourcelin
 */
@Component
public class AdminStpInterface
{
    private static final Logger log = LoggerFactory.getLogger(AdminStpInterface.class);

    public List<String> getRoleList(Object loginId, String loginType)
    {
        return new ArrayList<>(resolveLoginUserField(loginId, "roles"));
    }

    public List<String> getPermissionList(Object loginId, String loginType)
    {
        Set<String> permissions = resolveLoginUserField(loginId, "permissions");
        if (log.isDebugEnabled())
        {
            log.debug("admin 权限加载: loginId={}, loginType={}, size={}", loginId, loginType, permissions.size());
        }
        return new ArrayList<>(permissions);
    }

    private Set<String> resolveLoginUserField(Object loginId, String fieldName)
    {
        Object sessionUser = getSessionValue(loginId, SecurityConstants.LOGIN_USER);
        if (sessionUser == null)
        {
            sessionUser = getSessionValue(loginId, "loginUser");
        }

        if (sessionUser instanceof SysLoginUser)
        {
            SysLoginUser loginUser = (SysLoginUser) sessionUser;
            if ("roles".equals(fieldName) && loginUser.getRoles() != null)
            {
                return new LinkedHashSet<>(loginUser.getRoles());
            }
            if ("permissions".equals(fieldName) && loginUser.getPermissions() != null)
            {
                return new LinkedHashSet<>(loginUser.getPermissions());
            }
            return new LinkedHashSet<>();
        }

        if (sessionUser instanceof Map)
        {
            Object value = ((Map<?, ?>) sessionUser).get(fieldName);
            return toStringSet(value);
        }

        return new LinkedHashSet<>();
    }

    private Set<String> toStringSet(Object value)
    {
        LinkedHashSet<String> values = new LinkedHashSet<>();
        if (value == null)
        {
            return values;
        }
        if (value instanceof Collection)
        {
            for (Object item : (Collection<?>) value)
            {
                if (item != null)
                {
                    values.add(String.valueOf(item));
                }
            }
            return values;
        }
        values.add(String.valueOf(value));
        return values;
    }

    protected Object getSessionValue(Object loginId, String key)
    {
        try
        {
            SaSession session = StpAdminUtil.stpLogic.getSessionByLoginId(loginId, false);
            if (session != null)
            {
                return session.get(key);
            }
        }
        catch (Exception e)
        {
            if (log.isDebugEnabled())
            {
                log.debug("读取 admin 会话失败: loginId={}, key={}", loginId, key, e);
            }
        }
        return null;
    }
}
