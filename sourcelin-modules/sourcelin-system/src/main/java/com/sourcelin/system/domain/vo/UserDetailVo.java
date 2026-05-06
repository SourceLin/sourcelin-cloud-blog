package com.sourcelin.system.domain.vo;

import com.sourcelin.system.api.domain.SysRole;
import com.sourcelin.system.api.domain.SysUser;
import com.sourcelin.system.domain.SysPost;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户编辑页详情数据。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private SysUser user;

    private List<SysRole> roles;

    private List<SysPost> posts;

    private List<Long> postIds;

    private List<Long> roleIds;
}

