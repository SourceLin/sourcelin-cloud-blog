package com.sourcelin.system.controller;

import java.util.List;

import com.sourcelin.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sourcelin.common.core.constant.UserConstants;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.system.domain.vo.RouterVo;
import com.sourcelin.system.domain.SysMenu;
import com.sourcelin.system.domain.vo.RoleMenuTreeVo;
import com.sourcelin.system.domain.vo.TreeSelect;

/**
 * 菜单信息
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/menu")
public class SysMenuController extends BaseController
{
    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @SaCheckPermission(type = "admin", value = "system:menu:list")
    @GetMapping("/list")
    public List<SysMenu> list(SysMenu menu)
    {
        Long userId = adminLoginAccessor.getCurrentUserId();
        List<SysMenu> menuList = menuService.selectMenuList(menu, userId);
        return menuService.buildMenuTree(menuList);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @SaCheckPermission(type = "admin", value = "system:menu:query")
    @GetMapping(value = "/{menuId}")
    public SysMenu getInfo(@PathVariable Long menuId)
    {
        return menuService.selectMenuById(menuId);
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public List<TreeSelect> treeselect(SysMenu menu)
    {
        Long userId = adminLoginAccessor.getCurrentUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return menuService.buildMenuTreeSelect(menus);
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public RoleMenuTreeVo roleMenuTreeselect(@PathVariable("roleId") Long roleId)
    {
        Long userId = adminLoginAccessor.getCurrentUserId();
        List<SysMenu> menus = menuService.selectMenuList(userId);
        return RoleMenuTreeVo.builder()
            .checkedKeys(menuService.selectMenuListByRoleId(roleId))
            .menus(menuService.buildMenuTreeSelect(menus))
            .build();
    }

    /**
     * 新增菜单
     */
    @SaCheckPermission(type = "admin", value = "system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Boolean add(@Validated @RequestBody SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        else if (!menuService.checkRouteConfigUnique(menu))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增菜单'" + menu.getMenuName() + "'失败，路由名称或地址已存在");
        }
        menu.setCreateBy(adminLoginAccessor.getUsername());
        if (menuService.insertMenu(menu) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增菜单失败");
        }
        return true;
    }

    /**
     * 修改菜单
     */
    @SaCheckPermission(type = "admin", value = "system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean edit(@Validated @RequestBody SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        else if (!menuService.checkRouteConfigUnique(menu))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改菜单'" + menu.getMenuName() + "'失败，路由名称或地址已存在");
        }
        menu.setUpdateBy(adminLoginAccessor.getUsername());
        if (menuService.updateMenu(menu) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改菜单失败");
        }
        return true;
    }

    /**
     * 删除菜单
     */
    @SaCheckPermission(type = "admin", value = "system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public Boolean remove(@PathVariable("menuId") Long menuId)
    {
        if (menuService.hasChildByMenuId(menuId))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "菜单已分配,不允许删除");
        }
        if (menuService.deleteMenuById(menuId) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除菜单失败");
        }
        return true;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public List<RouterVo> getRouters()
    {
        Long userId = adminLoginAccessor.getCurrentUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return menuService.buildMenus(menus);
    }
}
