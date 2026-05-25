package com.sourcelin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "微信小程序登录结果")
public class MiniProgramLoginVo {

    @Schema(description = "是否已绑定账号")
    private boolean bound;

    @Schema(description = "是否为首次自动创建的小程序账号")
    private boolean newUser;

    @Schema(description = "登录令牌")
    private AuthTokenVo token;

    public boolean isBound() {
        return bound;
    }

    public void setBound(boolean bound) {
        this.bound = bound;
    }

    public boolean isNewUser() {
        return newUser;
    }

    public void setNewUser(boolean newUser) {
        this.newUser = newUser;
    }

    public AuthTokenVo getToken() {
        return token;
    }

    public void setToken(AuthTokenVo token) {
        this.token = token;
    }
}
