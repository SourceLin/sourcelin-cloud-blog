package com.sourcelin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(description = "微信小程序绑定已有账号请求")
public class MiniProgramBindDTO {

    @NotBlank(message = "绑定票据不能为空")
    @Schema(description = "微信登录返回的绑定票据")
    private String bindToken;

    @NotBlank(message = "账号不能为空")
    @Schema(description = "账号或邮箱")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "RSA 加密后的密码")
    private String password;

    @Schema(description = "图形验证码")
    private String captchaCode;

    @Schema(description = "图形验证码 UUID")
    private String captchaUuid;

    public String getBindToken() {
        return bindToken;
    }

    public void setBindToken(String bindToken) {
        this.bindToken = bindToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public String getCaptchaUuid() {
        return captchaUuid;
    }

    public void setCaptchaUuid(String captchaUuid) {
        this.captchaUuid = captchaUuid;
    }
}
