package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.service.IUserService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户信息Controller
 *
 * @author sourcelin
 * @date 2023-11-02
 */
@RestController
@RequestMapping("/admin/user")
public class UserController
{
    @Autowired
    private IUserService userService;

    /**
     * 查询用户信息列表
     */
    @SaCheckPermission(type = "admin", value = "blog:user:list")
    @GetMapping("/list")
    public PageResult<User> list(User user)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize());
        List<User> list = userService.selectUserList(user);
        PageInfo<User> pageInfo = new PageInfo<User>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    /**
     * 导出用户信息列表
     */
    @SaCheckPermission(type = "admin", value = "blog:user:export")
    @Log(title = "用户信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, User user)
    {
        List<User> list = userService.selectUserList(user);
        ExcelUtil<User> util = new ExcelUtil<User>(User.class);
        util.exportExcel(response, list, "用户信息数据");
    }

    /**
     * 获取用户信息详细信息
     */
    @SaCheckPermission(type = "admin", value = "blog:user:query")
    @GetMapping(value = "/{id}")
    public User getInfo(@PathVariable("id") Long id)
    {
        User user = userService.selectUserById(id);
        if (user == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    /**
     * 新增用户信息
     */
    @SaCheckPermission(type = "admin", value = "blog:user:add")
    @Log(title = "用户信息", businessType = BusinessType.INSERT)
    @PostMapping
    public Void add(@RequestBody User user)
    {
        if (userService.insertUser(user) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "新增用户失败");
        }
        return null;
    }

    /**
     * 修改用户信息
     */
    @SaCheckPermission(type = "admin", value = "blog:user:edit")
    @Log(title = "用户信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody User user)
    {
        if (userService.updateUser(user) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改用户失败");
        }
        return null;
    }

    /**
     * 删除用户信息
     */
    @SaCheckPermission(type = "admin", value = "blog:user:remove")
    @Log(title = "用户信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (userService.deleteUserByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "删除用户失败");
        }
        return null;
    }

    /**
     * 重置用户信息密码
     */
    @SaCheckPermission(type = "admin", value = "blog:user:resetPwd")
    @Log(title = "重置用户信息密码", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public Void resetPassword(@RequestParam Long userId, @RequestParam String password)
    {
        if (userService.resetPassword(userId, password) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "重置密码失败");
        }
        return null;
    }
}
