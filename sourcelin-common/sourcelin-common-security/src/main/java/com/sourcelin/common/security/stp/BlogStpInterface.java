package com.sourcelin.common.security.stp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 博客权限接口实现
 * 博客用户通常不需要角色权限控制
 * 
 * @author sourcelin
 */
@Component
@ConditionalOnProperty(name = "spring.application.name", havingValue = "sourcelin-blog")
public class BlogStpInterface
{
    public List<String> getRoleList(Object loginId, String loginType)
    {
        return new ArrayList<>();
    }

    public List<String> getPermissionList(Object loginId, String loginType)
    {
        return new ArrayList<>();
    }
}
