package com.sourcelin.common.security.session;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class AdminLoginSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String userName;

    private String nickName;

    private Long deptId;

    private String deptName;

    private Set<String> roles;

    private Set<String> permissions;

    private String loginType = "admin";
}
