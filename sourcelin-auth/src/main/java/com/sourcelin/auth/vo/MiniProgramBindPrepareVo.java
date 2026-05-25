package com.sourcelin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "微信小程序绑定准备结果")
public class MiniProgramBindPrepareVo {

    @Schema(description = "短期绑定票据")
    private String bindToken;

    @Schema(description = "绑定引导文案")
    private String bindMessage;

    public String getBindToken() {
        return bindToken;
    }

    public void setBindToken(String bindToken) {
        this.bindToken = bindToken;
    }

    public String getBindMessage() {
        return bindMessage;
    }

    public void setBindMessage(String bindMessage) {
        this.bindMessage = bindMessage;
    }
}
