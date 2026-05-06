package com.sourcelin.blog.controller.internal;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.api.model.LoginUser;
import com.sourcelin.blog.constant.UserTypeEnum;
import com.sourcelin.blog.service.IUserService;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 博客用户远程内部接口
 *
 * 仅供内部 Feign 调用，要求 FROM_SOURCE=INNER
 */
@RestController
@RequestMapping("/user")
public class RemoteUserController {

    @Autowired
    private IUserService userService;

    private boolean isInner(String source) {
        return StringUtils.equals(SecurityConstants.INNER, source);
    }

    /**
     * 通过用户名 / 手机号 / 其他标识查询登录用户
     */
    @GetMapping("/info/{param}")
    public R<LoginUser> getUserInfo(@PathVariable("param") String param,
                                    @RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
        if (!isInner(source)) {
            return R.fail("非法访问来源");
        }
        if (StringUtils.isBlank(param)) {
            return R.fail("参数不能为空");
        }

        // 目前简单按用户名查询，后续可扩展为手机号 / openId 等
        User query = new User();
        query.setUsername(param);
        User user = userService.selectUserList(query).stream().findFirst().orElse(null);
        if (user == null) {
            return R.fail("用户不存在");
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        // 角色、权限等后续可以在此补充

        return R.ok(loginUser);
    }

    /**
     * 注册用户信息
     */
    @PostMapping("/register")
    public R<Long> registerUserInfo(@RequestBody User user,
                                    @RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
        if (!isInner(source)) {
            return R.fail("非法访问来源");
        }
        if (user == null || StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return R.fail("用户名和密码不能为空");
        }

        if (userService.selectUserByUsername(user.getUsername()) != null) {
            return R.fail("用户名'" + user.getUsername() + "'已被注册");
        }
        if (StringUtils.isNotBlank(user.getEmail()) && userService.selectUserByEmail(user.getEmail()) != null) {
            return R.fail("邮箱'" + user.getEmail() + "'已被注册");
        }

        if (StringUtils.isBlank(user.getNickname())) {
            user.setNickname(user.getUsername());
        }
        if (user.getUserType() == null) {
            user.setUserType(UserTypeEnum.NORMAL.getCode());
        }
        if (user.getUserStatus() == null) {
            user.setUserStatus(0L);
        }
        if (StringUtils.isBlank(user.getDelFlag())) {
            user.setDelFlag("0");
        }
        int rows = userService.insertUser(user);
        return rows > 0 ? R.ok(user.getId()) : R.fail("注册用户失败");
    }

    /**
     * 更新用户信息
     */
    @PostMapping("/update")
    public R<Integer> updateUserInfo(@RequestBody User user,
                                     @RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
        if (!isInner(source)) {
            return R.fail("非法访问来源");
        }
        if (user == null || user.getId() == null) {
            return R.fail("用户ID不能为空");
        }
        int rows = userService.updateUser(user);
        return rows >= 0 ? R.ok(rows) : R.fail("更新用户失败");
    }

    /**
     * 通过邮箱查询用户
     */
    @GetMapping("/info/email/{email:.+}")
    public R<LoginUser> getUserInfoByEmail(@PathVariable("email") String email,
                                           @RequestHeader(SecurityConstants.FROM_SOURCE) String source) {
        if (!isInner(source)) {
            return R.fail("非法访问来源");
        }
        if (StringUtils.isBlank(email)) {
            return R.fail("邮箱不能为空");
        }

        User user = userService.selectUserByEmail(email);
        if (user == null) {
            return R.fail("用户不存在");
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);

        return R.ok(loginUser);
    }
}

