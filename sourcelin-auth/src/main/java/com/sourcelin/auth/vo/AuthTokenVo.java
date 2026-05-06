package com.sourcelin.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 认证令牌响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthTokenVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String token;

    private String tokenName;

    private String tokenPrefix;

    private Integer expiresIn;

    private String loginType;
}
