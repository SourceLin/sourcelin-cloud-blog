package com.sourcelin.auth.service;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.api.model.LoginUser;
import com.sourcelin.blog.api.service.RemoteUserService;
import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.core.constant.Constants;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.constant.UserConstants;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.core.enums.UserStatus;
import com.sourcelin.common.core.exception.ServiceException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.ip.IpUtils;
import com.sourcelin.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogLoginService {

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private SysRecordLogService sysRecordLogService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private RedisService redisService;

    public LoginUser login(String username, String password) {
        // 用户名或密码为空
        if (StringUtils.isAnyBlank(username, password)) {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户/密码必须填写");
            throw new ServiceException("用户/密码必须填写");
        }

        // 密码长度校验
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度应在 5-20 位之间");
        }

        boolean isEmail = username.contains("@");

        // 用户名长度校验（邮箱输入不受用户名长度限制）
        if (!isEmail && (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)) {
            throw new ServiceException("用户名长度应在 2-20 位之间");
        }

        // 查询用户信息（支持邮箱登录）
        R<LoginUser> userResult;
        if (isEmail) {
            userResult = remoteUserService.getUserInfoByEmail(username, SecurityConstants.INNER);
        } else {
            userResult = remoteUserService.getUserInfo(username, SecurityConstants.INNER);
        }
        if (StringUtils.isNull(userResult)) {
            String failMsg = isEmail ? "邮箱未注册" : "用户不存在";
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, failMsg);
            throw new ServiceException(failMsg);
        }
        // 须先于 getData() 判断：Feign 超时等降级会返回 R.fail(msg) 且 data 为空，否则会误报「用户不存在」
        if (!R.isSuccess(userResult)) {
            String failMsg = StringUtils.isNotEmpty(userResult.getMsg())
                    ? userResult.getMsg()
                    : (isEmail ? "邮箱未注册" : "用户不存在");
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, failMsg);
            throw new ServiceException(failMsg);
        }
        if (StringUtils.isNull(userResult.getData())) {
            String failMsg = isEmail ? "邮箱未注册" : "用户不存在";
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, failMsg);
            throw new ServiceException(failMsg);
        }

        LoginUser userInfo = userResult.getData();
        User user = userInfo.getUser();

        // 检查删除状态
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            throw new ServiceException("账号已被删除");
        }

        // 检查停用状态
        if (UserStatus.DISABLE.getCode().equals(user.getUserStatus())) {
            throw new ServiceException("账号已被停用");
        }

        // 密码校验
        passwordService.validate(user, password);

        // 更新登录信息
        user.setLoginIp(IpUtils.getIpAddr());
        user.setLoginDate(new java.util.Date());
        remoteUserService.updateUserInfo(user, SecurityConstants.INNER);

        sysRecordLogService.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");
        return userInfo;
    }

    public LoginUser loginWithPhone(String phone, String code) {
        // 验证码校验
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + phone;
        String captcha = redisService.getCacheObject(verifyKey);
        
        if (StringUtils.isNull(captcha)) {
            throw new ServiceException("验证码已过期");
        }
        if (!code.equals(captcha)) {
            throw new ServiceException("验证码错误");
        }
        
        // 删除验证码
        redisService.deleteObject(verifyKey);

        // 查询用户信息
        R<LoginUser> userResult = remoteUserService.getUserInfo(phone, SecurityConstants.INNER);
        if (StringUtils.isNull(userResult)) {
            throw new ServiceException("手机号未注册");
        }
        if (!R.isSuccess(userResult)) {
            throw new ServiceException(StringUtils.isNotEmpty(userResult.getMsg())
                    ? userResult.getMsg()
                    : "手机号未注册");
        }
        if (StringUtils.isNull(userResult.getData())) {
            throw new ServiceException("手机号未注册");
        }

        LoginUser userInfo = userResult.getData();
        User user = userInfo.getUser();

        // 检查删除状态
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            throw new ServiceException("账号已被删除");
        }

        // 检查停用状态
        if (UserStatus.DISABLE.getCode().equals(user.getUserStatus())) {
            throw new ServiceException("账号已被停用");
        }

        sysRecordLogService.recordLogininfor(phone, Constants.LOGIN_SUCCESS, "手机号登录成功");
        return userInfo;
    }
    
    public R<LoginUser> getUserInfoByEmail(String email) {
        return remoteUserService.getUserInfoByEmail(email, SecurityConstants.INNER);
    }
}
