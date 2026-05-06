package com.sourcelin.blog.api.service;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.api.factory.RemoteUserFallbackFactory;
import com.sourcelin.blog.api.model.LoginUser;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.sourcelin.common.core.constant.ServiceNameConstants;

/**
 * 用户服务
 *
 * @author sourcelin
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.BLOG_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {

    /**
     * 通过用户名手机号或openId查询用户
     *
     * @param param    用户名手机号或openId
     * @param source   请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{param}")
    public R<LoginUser> getUserInfo(@PathVariable("param") String param,
                                    @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     *
     * @param user     用户信息
     * @param source   请求来源
     * @return 新用户ID
     */
    @PostMapping("/user/register")
    public R<Long> registerUserInfo(@RequestBody User user,
                                    @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 更新用户信息
     *
     * @param user     用户信息
     * @param source   请求来源
     * @return 结果
     */
    @PostMapping("/user/update")
    public R<Integer> updateUserInfo(@RequestBody User user,
                                     @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 通过邮箱查询用户
     *
     * @param email   邮箱地址
     * @param source  请求来源
     * @return 结果
     */
    @GetMapping("/user/info/email/{email}")
    public R<LoginUser> getUserInfoByEmail(@PathVariable("email") String email,
                                           @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

}
