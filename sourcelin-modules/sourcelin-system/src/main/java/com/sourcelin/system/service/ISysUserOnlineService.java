package com.sourcelin.system.service;

import com.sourcelin.system.api.model.SysLoginUser;
import com.sourcelin.system.domain.SysUserOnline;

/**
 * 在线用户 服务层
 * 
 * @author sourcelin
 */
public interface ISysUserOnlineService
{
    /**
     * 通过登录地址查询信息
     * 
     * @param ipaddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByIpaddr(String ipaddr, SysLoginUser user);

    /**
     * 通过用户名称查询信息
     * 
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByUserName(String userName, SysLoginUser user);

    /**
     * 通过登录地址/用户名称查询信息
     * 
     * @param ipaddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByInfo(String ipaddr, String userName, SysLoginUser user);

    /**
     * 设置在线用户信息
     * 
     * @param user 用户信息
     * @return 在线用户
     */
    public SysUserOnline loginUserToUserOnline(SysLoginUser user);
}
