package com.sourcelin.common.security.accessor;

import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.security.stp.StpAdminUtil;
import com.sourcelin.system.api.model.SysLoginUser;
import org.springframework.stereotype.Component;

@Component
public class AdminLoginAccessor {

    public Long getCurrentUserId() {
        if (!isLogin()) {
            return null;
        }
        try {
            return getLoginIdAsLong();
        } catch (Exception ignored) {
            return null;
        }
    }

    public boolean isLogin() {
        return StpAdminUtil.stpLogic.isLogin();
    }

    public SysLoginUser getCurrentLoginUser() {
        if (!isLogin()) {
            return null;
        }
        try {
            Object loginUser = getSessionValue(SecurityConstants.LOGIN_USER);
            if (loginUser instanceof SysLoginUser) {
                return (SysLoginUser) loginUser;
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    public String getUsername() {
        SysLoginUser loginUser = getCurrentLoginUser();
        if (loginUser != null && StringUtils.isNotEmpty(loginUser.getUsername())) {
            return loginUser.getUsername();
        }
        if (!isLogin()) {
            return null;
        }
        try {
            return getLoginIdAsString();
        } catch (Exception ignored) {
            return null;
        }
    }

    public String getTokenValue() {
        return StpAdminUtil.stpLogic.getTokenValue();
    }

    protected Long getLoginIdAsLong() {
        return StpAdminUtil.stpLogic.getLoginIdAsLong();
    }

    protected String getLoginIdAsString() {
        return StpAdminUtil.stpLogic.getLoginIdAsString();
    }

    protected Object getSessionValue(String key) {
        return StpAdminUtil.stpLogic.getSession().get(key);
    }
}
