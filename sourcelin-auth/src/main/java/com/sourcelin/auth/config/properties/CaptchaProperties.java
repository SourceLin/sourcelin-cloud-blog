package com.sourcelin.auth.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sourcelin.captcha")
public class CaptchaProperties {

    /**
     * 后台(admin)是否启用图形验证码，默认启用。
     */
    private Boolean adminEnabled = true;

    public Boolean getAdminEnabled() {
        return adminEnabled;
    }

    public void setAdminEnabled(Boolean adminEnabled) {
        this.adminEnabled = adminEnabled;
    }
}
