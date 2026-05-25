package com.sourcelin.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sourcelin.mini-program")
public class MiniProgramProperties {

    /**
     * 小程序 AppID，由配置中心注入。
     */
    private String appId;

    /**
     * 小程序 AppSecret，由配置中心注入。
     */
    private String appSecret;

    /**
     * 微信 code 换 session 接口地址。
     */
    private String code2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 未绑定账号时短票据有效期，单位秒。
     */
    private Long bindTokenTtlSeconds = 600L;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getCode2SessionUrl() {
        return code2SessionUrl;
    }

    public void setCode2SessionUrl(String code2SessionUrl) {
        this.code2SessionUrl = code2SessionUrl;
    }

    public Long getBindTokenTtlSeconds() {
        return bindTokenTtlSeconds;
    }

    public void setBindTokenTtlSeconds(Long bindTokenTtlSeconds) {
        this.bindTokenTtlSeconds = bindTokenTtlSeconds;
    }
}
