package com.sourcelin.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.sourcelin.system.service.ISysDeptService;
import com.sourcelin.system.service.ISysRoleService;
import com.sourcelin.system.service.ISysUserService;
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
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.system.api.domain.SysDept;
import com.sourcelin.system.api.domain.SysRole;
import com.sourcelin.system.api.domain.SysUser;
import com.sourcelin.system.domain.SysUserRole;
import com.sourcelin.system.domain.vo.RoleDeptTreeVo;
import com.sourcelin.system.domain.vo.TreeSelect;

/**
 * 角色信息
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/role")
public class SysRoleController extends BaseController
{
    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    @SaCheckPermission(type = "admin", value = "system:role:list")
    @GetMapping("/list")
    public PageResult<SysRole> list(SysRole role)
    {
        startPage();
        List<SysRole> list = roleService.selectRoleList(role);
        return toPageResult(list);
    }

    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission(type = "admin", value = "system:role:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRole role)
    {
        List<SysRole> list = roleService.selectRoleList(role);
        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    @SaCheckPermission(type = "admin", value = "system:role:query")
    @GetMapping(value = "/{roleId}")
    public SysRole getInfo(@PathVariable Long roleId)
    {
        roleService.checkRoleDataScope(roleId);
        return roleService.selectRoleById(roleId);
    }

    /**
     * 新增角色
     */
    @SaCheckPermission(type = "admin", value = "system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Boolean add(@Validated @RequestBody SysRole role)
    {
        if (!roleService.checkRoleNameUnique(role))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!roleService.checkRoleKeyUnique(role))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setCreateBy(adminLoginAccessor.getUsername());
        if (roleService.insertRole(role) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增角色失败");
        }
        return true;

    }

    /**
     * 修改保存角色
     */
    @SaCheckPermission(type = "admin", value = "system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean edit(@Validated @RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (!roleService.checkRoleNameUnique(role))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!roleService.checkRoleKeyUnique(role))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        role.setUpdateBy(adminLoginAccessor.getUsername());
        if (roleService.updateRole(role) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改角色失败");
        }
        return true;
    }

    /**
     * 修改保存数据权限
     */
    @SaCheckPermission(type = "admin", value = "system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public Boolean dataScope(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (roleService.authDataScope(role) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改角色数据权限失败");
        }
        return true;
    }

    /**
     * 状态修改
     */
    @SaCheckPermission(type = "admin", value = "system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public Boolean changeStatus(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(adminLoginAccessor.getUsername());
        if (roleService.updateRoleStatus(role) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改角色状态失败");
        }
        return true;
    }

    /**
     * 删除角色
     */
    @SaCheckPermission(type = "admin", value = "system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public Boolean remove(@PathVariable Long[] roleIds)
    {
        if (roleService.deleteRoleByIds(roleIds) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除角色失败");
        }
        return true;
    }

    /**
     * 获取角色选择框列表
     */
    @SaCheckPermission(type = "admin", value = "system:role:query")
    @GetMapping("/optionselect")
    public List<SysRole> optionselect()
    {
        return roleService.selectRoleAll();
    }
    /**
     * 查询已分配用户角色列表
     */
    @SaCheckPermission(type = "admin", value = "system:role:list")
    @GetMapping("/authUser/allocatedList")
    public PageResult<SysUser> allocatedList(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectAllocatedList(user);
        return toPageResult(list);
    }

    /**
     * 查询未分配用户角色列表
     */
    @SaCheckPermission(type = "admin", value = "system:role:list")
    @GetMapping("/authUser/unallocatedList")
    public PageResult<SysUser> unallocatedList(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectUnallocatedList(user);
        return toPageResult(list);
    }

    /**
     * 取消授权用户
     */
    @SaCheckPermission(type = "admin", value = "system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public Boolean cancelAuthUser(@RequestBody SysUserRole userRole)
    {
        if (roleService.deleteAuthUser(userRole) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "取消用户授权失败");
        }
        return true;
    }

    /**
     * 批量取消授权用户
     */
    @SaCheckPermission(type = "admin", value = "system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public Boolean cancelAuthUserAll(Long roleId, Long[] userIds)
    {
        if (roleService.deleteAuthUsers(roleId, userIds) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "批量取消用户授权失败");
        }
        return true;
    }

    /**
     * 批量选择用户授权
     */
    @SaCheckPermission(type = "admin", value = "system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public Boolean selectAuthUserAll(Long roleId, Long[] userIds)
    {
        roleService.checkRoleDataScope(roleId);
        if (roleService.insertAuthUsers(roleId, userIds) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "批量授权用户失败");
        }
        return true;
    }

    /**
     * 获取对应角色部门树列表
     */
    @SaCheckPermission(type = "admin", value = "system:role:query")
    @GetMapping(value = "/deptTree/{roleId}")
    public RoleDeptTreeVo deptTree(@PathVariable("roleId") Long roleId)
    {
        List<TreeSelect> depts = deptService.selectDeptTreeList(new SysDept());
        return RoleDeptTreeVo.builder()
            .checkedKeys(deptService.selectDeptListByRoleId(roleId))
            .depts(depts)
            .build();
    }
}
