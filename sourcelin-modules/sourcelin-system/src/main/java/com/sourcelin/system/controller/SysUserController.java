package com.sourcelin.system.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
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
import org.springframework.web.multipart.MultipartFile;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.text.Convert;
import com.sourcelin.common.core.utils.DateUtils;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import com.sourcelin.common.security.annotation.InnerAuth;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.common.security.utils.SecurityUtils;
import com.sourcelin.system.api.domain.SysDept;
import com.sourcelin.system.api.domain.SysRole;
import com.sourcelin.system.api.domain.SysUser;
import com.sourcelin.system.api.model.SysLoginUser;
import com.sourcelin.system.domain.vo.CurrentUserInfoVo;
import com.sourcelin.system.domain.vo.TreeSelect;
import com.sourcelin.system.domain.vo.UserAuthRoleVo;
import com.sourcelin.system.domain.vo.UserDetailVo;
import com.sourcelin.system.service.ISysConfigService;
import com.sourcelin.system.service.ISysDeptService;
import com.sourcelin.system.service.ISysPermissionService;
import com.sourcelin.system.service.ISysPostService;
import com.sourcelin.system.service.ISysRoleService;
import com.sourcelin.system.service.ISysUserService;

/**
 * 用户信息
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/user")
public class SysUserController extends BaseController
{
    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysPermissionService permissionService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 获取用户列表
     */
    @SaCheckPermission(type = "admin", value = "system:user:list")
    @GetMapping("/list")
    public PageResult<SysUser> list(SysUser user)
    {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return toPageResult(list);
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission(type = "admin", value = "system:user:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user)
    {
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @SaCheckPermission(type = "admin", value = "system:user:import")
    @PostMapping("/importData")
    public String importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = adminLoginAccessor.getUsername();
        return userService.importUser(userList, updateSupport, operName);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 获取当前用户信息
     */
    @InnerAuth
    @GetMapping("/info/{username}")
    public R<SysLoginUser> info(@PathVariable("username") String username)
    {
        SysUser sysUser = userService.selectUserByUserName(username);
        if (StringUtils.isNull(sysUser))
        {
            return R.fail("用户名或密码错误");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser);
        SysLoginUser sysUserVo = new SysLoginUser();
        sysUserVo.setUserid(sysUser.getUserId());
        sysUserVo.setUsername(sysUser.getUserName());
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }

    /**
     * 注册用户信息
     */
    @InnerAuth
    @PostMapping("/register")
    public R<Boolean> register(@RequestBody SysUser sysUser)
    {
        String username = sysUser.getUserName();
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return R.fail("当前系统没有开启注册功能！");
        }
        if (!userService.checkUserNameUnique(sysUser))
        {
            return R.fail("保存用户'" + username + "'失败，注册账号已存在");
        }
        return R.ok(userService.registerUser(sysUser));
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public CurrentUserInfoVo getInfo()
    {
        SysUser user = userService.selectUserById(adminLoginAccessor.getCurrentUserId());
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        return CurrentUserInfoVo.builder()
            .user(user)
            .roles(roles)
            .permissions(permissions)
            .isDefaultModifyPwd(initPasswordIsModify(user.getPwdUpdateDate()))
            .isPasswordExpired(passwordIsExpiration(user.getPwdUpdateDate()))
            .build();
    }

    /** 初始密码是否需提醒修改（依赖参数 sys.account.initPasswordModify） */
    public boolean initPasswordIsModify(Date pwdUpdateDate)
    {
        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    /** 密码是否超过更新周期（依赖参数 sys.account.passwordValidateDays，0 表示不校验） */
    public boolean passwordIsExpiration(Date pwdUpdateDate)
    {
        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0)
        {
            if (StringUtils.isNull(pwdUpdateDate))
            {
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }

    /**
     * 根据用户编号获取详细信息
     */
    @SaCheckPermission(type = "admin", value = "system:user:query")
    @GetMapping(value = { "/", "/{userId}" })
    public UserDetailVo getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        userService.checkUserDataScope(userId);
        List<SysRole> roles = roleService.selectRoleAll();
        List<SysRole> availableRoles = SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList());
        UserDetailVo.UserDetailVoBuilder builder = UserDetailVo.builder()
            .roles(availableRoles)
            .posts(postService.selectPostAll())
            .postIds(Collections.<Long>emptyList())
            .roleIds(Collections.<Long>emptyList());
        if (StringUtils.isNotNull(userId))
        {
            SysUser sysUser = userService.selectUserById(userId);
            List<Long> roleIds = sysUser.getRoles() == null
                ? Collections.<Long>emptyList()
                : sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList());
            builder.user(sysUser);
            builder.postIds(postService.selectPostListByUserId(userId));
            builder.roleIds(roleIds);
        }
        return builder.build();
    }

    /**
     * 新增用户
     */
    @SaCheckPermission(type = "admin", value = "system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Boolean add(@Validated @RequestBody SysUser user)
    {
        if (!userService.checkUserNameUnique(user))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(adminLoginAccessor.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        if (userService.insertUser(user) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增用户失败");
        }
        return true;
    }

    /**
     * 修改用户
     */
    @SaCheckPermission(type = "admin", value = "system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean edit(@Validated @RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        if (!userService.checkUserNameUnique(user))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(adminLoginAccessor.getUsername());
        if (userService.updateUser(user) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改用户失败");
        }
        return true;
    }

    /**
     * 删除用户
     */
    @SaCheckPermission(type = "admin", value = "system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public Boolean remove(@PathVariable Long[] userIds)
    {
        if (ArrayUtils.contains(userIds, adminLoginAccessor.getCurrentUserId()))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "当前用户不能删除");
        }
        if (userService.deleteUserByIds(userIds) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除用户失败");
        }
        return true;
    }

    /**
     * 重置密码
     */
    @SaCheckPermission(type = "admin", value = "system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public Boolean resetPwd(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(adminLoginAccessor.getUsername());
        if (userService.resetPwd(user) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "重置密码失败");
        }
        return true;
    }

    /**
     * 状态修改
     */
    @SaCheckPermission(type = "admin", value = "system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public Boolean changeStatus(@RequestBody SysUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(adminLoginAccessor.getUsername());
        if (userService.updateUserStatus(user) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改用户状态失败");
        }
        return true;
    }

    /**
     * 根据用户编号获取授权角色
     */
    @SaCheckPermission(type = "admin", value = "system:user:query")
    @GetMapping("/authRole/{userId}")
    public UserAuthRoleVo authRole(@PathVariable("userId") Long userId)
    {
        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        return UserAuthRoleVo.builder()
            .user(user)
            .roles(SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()))
            .build();
    }

    /**
     * 用户授权角色
     */
    @SaCheckPermission(type = "admin", value = "system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public Boolean insertAuthRole(Long userId, Long[] roleIds)
    {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return true;
    }

    /**
     * 获取部门树列表
     */
    @SaCheckPermission(type = "admin", value = "system:user:list")
    @GetMapping("/deptTree")
    public List<TreeSelect> deptTree(SysDept dept)
    {
        return deptService.selectDeptTreeList(dept);
    }
}
