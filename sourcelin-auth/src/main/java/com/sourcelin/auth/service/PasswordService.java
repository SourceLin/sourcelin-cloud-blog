package com.sourcelin.auth.service;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.core.constant.Constants;
import com.sourcelin.common.core.exception.ServiceException;
import com.sourcelin.common.redis.service.RedisService;
import com.sourcelin.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 登录密码方法
 *
 * @author sourcelin
 */
@Component
public class PasswordService {
    @Autowired
    private RedisService redisService;

    @Autowired
    private SysRecordLogService sysRecordLogService;

    private int maxRetryCount = CacheConstants.PASSWORD_MAX_RETRY_COUNT;

    private Long lockTime = CacheConstants.PASSWORD_LOCK_TIME;

    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    private String getCacheKey(String username) {
        return CacheConstants.PWD_ERR_CNT_KEY + username;
    }

    public void validate(User user, String password) {
        String username = user.getUsername();

        Integer retryCount = redisService.getCacheObject(getCacheKey(username));

        if (retryCount == null) {
            retryCount = 0;
        }

        if (retryCount >= Integer.valueOf(maxRetryCount).intValue()) {
            String errMsg = String.format("密码输入错误%s次，帐户锁定%s分钟", maxRetryCount, lockTime);
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, errMsg);
            throw new ServiceException(errMsg);
        }

        if (!matches(user, password)) {
            retryCount = retryCount + 1;
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, String.format("密码输入错误%s次", retryCount));
            redisService.setCacheObject(getCacheKey(username), retryCount, lockTime, TimeUnit.MINUTES);
            throw new ServiceException("用户不存在/密码错误");
        } else {
            clearLoginRecordCache(username);
        }
    }

    public boolean matches(User user, String rawPassword) {
        return SecurityUtils.matchesPassword(rawPassword, user.getPassword());
    }

    public void clearLoginRecordCache(String loginName) {
        if (redisService.hasKey(getCacheKey(loginName))) {
            redisService.deleteObject(getCacheKey(loginName));
        }
    }
}

