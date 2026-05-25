package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.mapper.UserMapper;
import com.sourcelin.blog.service.IUserService;
import com.sourcelin.common.core.utils.DateUtils;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户信息Service业务层处理
 * 
 * @author sourcelin
 * @date 2026-01-02
 */
@Service
public class UserServiceImpl implements IUserService 
{
    @Autowired
    private UserMapper userMapper;

    /**
     * 查询用户信息
     * 
     * @param id 用户信息主键
     * @return 用户信息
     */
    @Override
    public User selectUserById(Long id)
    {
        return userMapper.selectUserById(id);
    }

    /**
     * 查询用户信息列表
     * 
     * @param user 用户信息
     * @return 用户信息
     */
    @Override
    public List<User> selectUserList(User user)
    {
        return userMapper.selectUserList(user);
    }

    /**
     * 新增用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int insertUser(User user)
    {
        user.setCreateTime(DateUtils.getNowDate());
        // 兼容两种来源：
        // 1) 前台注册链路可能已提前加密；2) 后台新增通常传明文密码。
        if (StringUtils.isNotEmpty(user.getPassword()) && !user.getPassword().startsWith("$2a$"))
        {
            user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        }
        return userMapper.insertUser(user);
    }

    /**
     * 修改用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUser(User user)
    {
        if (StringUtils.isNotEmpty(user.getPassword()) && !user.getPassword().startsWith("$2a$"))
        {
            user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        }
        return userMapper.updateUser(user);
    }

    /**
     * 批量删除用户信息
     * 
     * @param ids 需要删除的用户信息主键
     * @return 结果
     */
    @Override
    public int deleteUserByIds(Long[] ids)
    {
        return userMapper.deleteUserByIds(ids);
    }

    /**
     * 删除用户信息信息
     * 
     * @param id 用户信息主键
     * @return 结果
     */
    @Override
    public int deleteUserById(Long id)
    {
        return userMapper.deleteUserById(id);
    }

    /**
     * 重置用户信息密码
     *
     * @param userId 用户ID
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetPassword(Long userId, String password)
    {
        User user = this.selectUserById(userId);
        user.setPassword(SecurityUtils.encryptPassword(password));
        return userMapper.updateUser(user);
    }

    @Override
    public User selectUserByUsername(String username)
    {
        return userMapper.selectUserByUsername(username);
    }

    @Override
    public User selectUserByEmail(String email)
    {
        return userMapper.selectUserByEmail(email);
    }

    @Override
    public User selectUserByOpenId(String openId)
    {
        return userMapper.selectUserByOpenId(openId);
    }
}
