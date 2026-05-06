package com.sourcelin.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 图形验证码响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Boolean captchaEnabled;

    private String uuid;

    private String img;
}
