package com.sourcelin.common.security.stp;

import cn.dev33.satoken.stp.StpInterface;
import com.sourcelin.common.core.utils.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 统一权限接口，按 loginType 分发到不同业务登录体系。
 *
 * @author sourcelin
 */
@Component
public class CompositeStpInterface implements StpInterface
{
    private final AdminStpInterface adminStpInterface;
    private final BlogStpInterface blogStpInterface;

    public CompositeStpInterface(AdminStpInterface adminStpInterface, ObjectProvider<BlogStpInterface> blogStpInterfaceProvider)
    {
        this.adminStpInterface = adminStpInterface;
        this.blogStpInterface = blogStpInterfaceProvider.getIfAvailable();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType)
    {
        if (isAdminLoginType(loginType))
        {
            return adminStpInterface.getRoleList(loginId, loginType);
        }
        if (isBlogLoginType(loginType) && blogStpInterface != null)
        {
            return blogStpInterface.getRoleList(loginId, loginType);
        }
        return new ArrayList<>();
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType)
    {
        if (isAdminLoginType(loginType))
        {
            return adminStpInterface.getPermissionList(loginId, loginType);
        }
        if (isBlogLoginType(loginType) && blogStpInterface != null)
        {
            return blogStpInterface.getPermissionList(loginId, loginType);
        }
        return new ArrayList<>();
    }

    private boolean isAdminLoginType(String loginType)
    {
        return StringUtils.equals(loginType, StpAdminUtil.stpLogic.getLoginType());
    }

    private boolean isBlogLoginType(String loginType)
    {
        return StringUtils.equals(loginType, StpBlogUtil.stpLogic.getLoginType());
    }
}
