package com.sourcelin.system.domain.vo;

import com.sourcelin.system.api.domain.SysUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 个人中心资料。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private SysUser user;

    private String roleGroup;

    private String postGroup;
}

