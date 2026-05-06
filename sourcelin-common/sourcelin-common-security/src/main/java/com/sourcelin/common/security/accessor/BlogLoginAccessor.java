package com.sourcelin.common.security.accessor;

import com.sourcelin.common.security.stp.StpBlogUtil;
import org.springframework.stereotype.Component;

@Component
public class BlogLoginAccessor {

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
        return StpBlogUtil.stpLogic.isLogin();
    }

    public String getTokenValue() {
        return StpBlogUtil.stpLogic.getTokenValue();
    }

    protected Long getLoginIdAsLong() {
        return StpBlogUtil.stpLogic.getLoginIdAsLong();
    }
}
