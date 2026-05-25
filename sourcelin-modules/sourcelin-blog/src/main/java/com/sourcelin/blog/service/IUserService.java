package com.sourcelin.blog.service;

import com.sourcelin.blog.api.domain.User;

import java.util.List;

/**
 * 用户信息Service接口
 * 
 * @author sourcelin
 * @date 2026-01-02
 */
public interface IUserService 
{
    /**
     * 查询用户信息
     * 
     * @param id 用户信息主键
     * @return 用户信息
     */
    public User selectUserById(Long id);

    /**
     * 查询用户信息列表
     * 
     * @param user 用户信息
     * @return 用户信息集合
     */
    public List<User> selectUserList(User user);

    /**
     * 新增用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(User user);

    /**
     * 修改用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(User user);

    /**
     * 批量删除用户信息
     * 
     * @param ids 需要删除的用户信息主键集合
     * @return 结果
     */
    public int deleteUserByIds(Long[] ids);

    /**
     * 删除用户信息信息
     * 
     * @param id 用户信息主键
     * @return 结果
     */
    public int deleteUserById(Long id);

    /**
     * 重置用户信息密码
     *
     * @param userId 用户ID
     * @param password 密码
     * @return 结果
     */
    public int resetPassword(Long userId, String password);

    /**
     * 通过用户名精确查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    public User selectUserByUsername(String username);

    /**
     * 通过邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    public User selectUserByEmail(String email);

    /**
     * 通过微信 openId 查询用户
     *
     * @param openId 微信 openId
     * @return 用户信息
     */
    public User selectUserByOpenId(String openId);
}
