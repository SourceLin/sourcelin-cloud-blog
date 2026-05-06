package com.sourcelin.system.domain.vo;

import com.sourcelin.system.api.domain.SysUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * 当前登录用户信息。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserInfoVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private SysUser user;

    private Set<String> roles;

    private Set<String> permissions;

    private Boolean isDefaultModifyPwd;

    private Boolean isPasswordExpired;
}

