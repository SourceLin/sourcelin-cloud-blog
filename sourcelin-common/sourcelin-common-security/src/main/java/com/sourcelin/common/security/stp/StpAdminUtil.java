package com.sourcelin.common.security.stp;

import cn.dev33.satoken.stp.StpLogic;

public class StpAdminUtil {

    public static final StpLogic stpLogic = new StpLogic("admin");

    public static String getTokenValue() {
        return stpLogic.getTokenValue();
    }

    public static void setTokenValue(String token) {
        stpLogic.setTokenValue(token);
    }

    public static void logout() {
        stpLogic.logout();
    }

    public static Object getLoginId() {
        return stpLogic.getLoginId();
    }

    public static long getLoginIdAsLong() {
        return stpLogic.getLoginIdAsLong();
    }

    public static String getLoginIdAsString() {
        return stpLogic.getLoginIdAsString();
    }

    public static void login(Object loginId) {
        stpLogic.login(loginId);
    }

    public static void login(Object loginId, long timeout) {
        stpLogic.login(loginId, timeout);
    }

    public static boolean isLogin() {
        return stpLogic.isLogin();
    }

    public static void kickout(Object loginId) {
        stpLogic.kickout(loginId);
    }

    public static long getSessionTimeout() {
        return stpLogic.getSessionTimeout();
    }

    public static void renewTimeout(long timeout) {
        stpLogic.renewTimeout(timeout);
    }

    public static Object getSession() {
        return stpLogic.getSession();
    }

    public static String getTokenName() {
        return stpLogic.getTokenName();
    }
}
