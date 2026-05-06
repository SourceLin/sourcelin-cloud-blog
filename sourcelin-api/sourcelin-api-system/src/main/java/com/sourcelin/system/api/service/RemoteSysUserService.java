package com.sourcelin.system.api.service;

import com.sourcelin.system.api.factory.RemoteSysUserFallbackFactory;
import com.sourcelin.system.api.domain.SysUser;
import com.sourcelin.system.api.model.SysLoginUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.constant.ServiceNameConstants;
import com.sourcelin.common.core.domain.R;

/**
 * 用户服务
 * 
 * @author sourcelin
 */
@FeignClient(contextId = "remoteSysUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteSysUserFallbackFactory.class)
public interface RemoteSysUserService
{
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public R<SysLoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    public R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
