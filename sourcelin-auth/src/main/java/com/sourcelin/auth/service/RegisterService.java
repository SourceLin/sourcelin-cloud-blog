package com.sourcelin.auth.service;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.api.service.RemoteUserService;
import com.sourcelin.common.core.constant.Constants;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.constant.UserConstants;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.core.exception.ServiceException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.uuid.IdUtils;
import com.sourcelin.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RegisterService {
    private static final String REGISTER_IDEMPOTENT_PREFIX = "register:idempotent:";
    private static final String IDEMPOTENT_PENDING = "PENDING";
    private static final String IDEMPOTENT_SUCCESS_PREFIX = "SUCCESS:";
    private static final long IDEMPOTENT_EXPIRE_MINUTES = 5;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private SysRecordLogService sysRecordLogService;

    @Autowired
    private EmailCodeService emailCodeService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户账号密码注册
     *
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @param email 邮箱
     * @param emailCode 邮箱验证码（可选）
     * @param registerRequestId 注册请求幂等ID
     * @return 新用户ID
     */
    public Long registerUser(String username, String password, String phone, String email, String emailCode, String registerRequestId) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            throw new ServiceException("用户/密码必须填写");
        }
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("用户名长度必须在2 ~ 20个字符之间");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5 ~ 20个字符之间");
        }
        String idempotentKey = null;
        if (StringUtils.isNotBlank(registerRequestId)) {
            idempotentKey = REGISTER_IDEMPOTENT_PREFIX + registerRequestId;
            String cachedState = stringRedisTemplate.opsForValue().get(idempotentKey);
            if (StringUtils.isNotBlank(cachedState)) {
                if (cachedState.startsWith(IDEMPOTENT_SUCCESS_PREFIX)) {
                    String userIdText = StringUtils.substring(
                        cachedState,
                        IDEMPOTENT_SUCCESS_PREFIX.length()
                    );
                    return Long.valueOf(userIdText);
                }
                throw new ServiceException("注册请求处理中，请勿重复提交");
            }
            Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(
                idempotentKey,
                IDEMPOTENT_PENDING,
                IDEMPOTENT_EXPIRE_MINUTES,
                TimeUnit.MINUTES
            );
            if (Boolean.FALSE.equals(locked)) {
                throw new ServiceException("注册请求处理中，请勿重复提交");
            }
        }

        try {
            // 如果提供了邮箱，验证邮箱验证码
            if (StringUtils.isNotBlank(email) && StringUtils.isNotBlank(emailCode)) {
                if (!emailCodeService.verifyCode(email, emailCode)) {
                    throw new ServiceException("邮箱验证码错误");
                }
            }
            User user = new User();
            user.setUsername(username);
            user.setPhone(phone);
            user.setEmail(email);
            user.setUuid(IdUtils.simpleUUID());
            user.setPassword(SecurityUtils.encryptPassword(password));
            R<Long> registerResult = remoteUserService.registerUserInfo(user, SecurityConstants.INNER);
            if (R.FAIL == registerResult.getCode()) {
                throw new ServiceException(registerResult.getMsg());
            }
            if (registerResult.getData() == null) {
                throw new ServiceException("注册成功但未获取到用户信息");
            }
            Long userId = registerResult.getData();
            if (idempotentKey != null) {
                stringRedisTemplate.opsForValue().set(
                    idempotentKey,
                    IDEMPOTENT_SUCCESS_PREFIX + userId,
                    IDEMPOTENT_EXPIRE_MINUTES,
                    TimeUnit.MINUTES
                );
            }
            sysRecordLogService.recordLogininfor(username, Constants.REGISTER, "注册成功");
            return userId;
        } catch (RuntimeException ex) {
            if (idempotentKey != null) {
                stringRedisTemplate.delete(idempotentKey);
            }
            throw ex;
        }
    }


}
