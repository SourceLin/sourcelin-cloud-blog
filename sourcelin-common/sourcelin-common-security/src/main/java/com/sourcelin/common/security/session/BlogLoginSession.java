package com.sourcelin.common.security.session;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class BlogLoginSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String userName;

    private String nickName;

    private String avatar;

    private Set<String> roles;

    private Set<String> permissions;

    private String loginType = "blog";
}
