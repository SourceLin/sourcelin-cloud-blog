package com.sourcelin.system.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sourcelin.system.service.ISysMenuService;
import com.sourcelin.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.sourcelin.system.api.domain.SysRole;
import com.sourcelin.system.api.domain.SysUser;
import com.sourcelin.system.service.ISysPermissionService;

/**
 * 用户权限处理
 * 
 * @author sourcelin
 */
@Service
public class SysPermissionServiceImpl implements ISysPermissionService
{
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取角色数据权限
     * 
     * @param userId 用户Id
     * @return 角色权限信息
     */
    @Override
    public Set<String> getRolePermission(SysUser user)
    {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            roles.add("admin");
        }
        else
        {
            roles.addAll(roleService.selectRolePermissionByUserId(user.getUserId()));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     * 
     * @param userId 用户Id
     * @return 菜单权限信息
     */
    @Override
    public Set<String> getMenuPermission(SysUser user)
    {
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (user.isAdmin())
        {
            perms.add("*:*:*");
        }
        else
        {
            List<SysRole> roles = user.getRoles();
            if (!CollectionUtils.isEmpty(roles))
            {
                // 多角色设置permissions属性，以便数据权限匹配权限
                for (SysRole role : roles)
                {
                    Set<String> rolePerms = menuService.selectMenuPermsByRoleId(role.getRoleId());
                    role.setPermissions(rolePerms);
                    perms.addAll(rolePerms);
                }
            }
            else
            {
                perms.addAll(menuService.selectMenuPermsByUserId(user.getUserId()));
            }
        }
        return perms;
    }
}
