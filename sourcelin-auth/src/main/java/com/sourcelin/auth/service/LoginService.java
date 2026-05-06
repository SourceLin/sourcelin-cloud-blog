package com.sourcelin.auth.service;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.api.model.LoginUser;
import com.sourcelin.blog.api.service.RemoteUserService;
import com.sourcelin.auth.dto.VerifyCodeDTO;
import com.sourcelin.auth.dto.VerifyPhoneDTO;
import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.core.constant.Constants;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.constant.UserConstants;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.core.enums.UserStatus;
import com.sourcelin.common.core.exception.ServiceException;
import com.sourcelin.common.core.text.Convert;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.ip.IpUtils;
import com.sourcelin.common.core.utils.uuid.IdUtils;
import com.sourcelin.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 登录校验方法
 *
 * @author sourcelin
 */
@Component
public class LoginService {

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private SysRecordLogService sysRecordLogService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private RedisService redisService;

    /**
     * 账号密码登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    public LoginUser login(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户/密码必须填写");
            throw new ServiceException("用户/密码必须填写");
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户密码不在指定范围");
            throw new ServiceException("用户密码不在指定范围");
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户名不在指定范围");
            throw new ServiceException("用户名不在指定范围");
        }
        // IP黑名单校验
        String blackStr = Convert.toStr(redisService.getCacheObject(CacheConstants.SYS_LOGIN_BLACKIPLIST));
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "很遗憾，访问IP已被列入系统黑名单");
            throw new ServiceException("很遗憾，访问IP已被列入系统黑名单");
        }
        // 查询用户信息
        R<LoginUser> userResult = remoteUserService.getUserInfo(username, SecurityConstants.INNER);
        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData())) {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "登录用户不存在");
            throw new ServiceException("登录用户：" + username + " 不存在");
        }
        if (R.FAIL == userResult.getCode()) {
            throw new ServiceException(userResult.getMsg());
        }
        LoginUser userInfo = userResult.getData();
        // 用户信息
        User user = userResult.getData().getUser();
        // 是否删除
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "对不起，您的账号已被删除");
            throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        }
        // 是否停用
        if (UserStatus.DISABLE.getCode().equals(user.getUserStatus())) {
            sysRecordLogService.recordLogininfor(username, Constants.LOGIN_FAIL, "用户已停用，请联系管理员");
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        passwordService.validate(user, password);
        sysRecordLogService.recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");
        return userInfo;
    }

    /**
     * 验证码登录
     *
     * @param param    登录参数
     * @return 登录结果
     */
    public LoginUser loginPhone(VerifyCodeDTO param) {
        LoginUser userInfo = null;
        // 从缓存中获取验证码
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + param.getPhone();
        String codeKey = redisService.getCacheObject(verifyKey);
        // 校验验证码是否过期
        if (StringUtils.isNull(codeKey)) {
            throw new ServiceException("验证码已过期");
        }
        // 校验验证码是否正确
        if (!param.getCode().equals(codeKey)) {
            throw new ServiceException("验证码错误");
        }
        // 根据手机号查询是否存在这个用户
        R<LoginUser> userResult = remoteUserService.getUserInfo(param.getPhone(), SecurityConstants.INNER);
        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData().getUser())) {
            // 不存在就创建新用户
            User user = new User();
            user.setUsername(IdUtils.fastSimpleUUID());
            user.setPhone(param.getPhone());
            R<?> registerResult = remoteUserService.registerUserInfo(user, SecurityConstants.INNER);
            if (R.FAIL == registerResult.getCode()) {
                throw new ServiceException(registerResult.getMsg());
            }
            sysRecordLogService.recordLogininfor(param.getPhone(), Constants.REGISTER, "注册成功");
        } else {
            // 存在就更新用户信息
            User user = userResult.getData().getUser();
            R<?> updateResult = remoteUserService.updateUserInfo(user, SecurityConstants.INNER);
            if (R.FAIL == updateResult.getCode()) {
                throw new ServiceException(updateResult.getMsg());
            }
            sysRecordLogService.recordLogininfor(param.getPhone(), Constants.REGISTER, "更新成功");
        }
        // 根据手机号查询查询用户
        R<LoginUser> userResults = remoteUserService.getUserInfo(param.getPhone(), SecurityConstants.INNER);
        userInfo = userResults.getData();
        // 用户信息
        User user = userResults.getData().getUser();
        // 是否删除
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            sysRecordLogService.recordLogininfor(param.getPhone(), Constants.LOGIN_FAIL, "删除成功");
            throw new ServiceException("对不起，您的账号:" + param.getPhone() + "已被删除");
        }
        // 是否停用
        if (UserStatus.DISABLE.getCode().equals(user.getUserStatus())) {
            sysRecordLogService.recordLogininfor(param.getPhone(), Constants.LOGIN_FAIL, "用户已停用，请联系管理员");
            throw new ServiceException("对不起，您的账号:" +  param.getPhone() + "已被停用");
        }
        sysRecordLogService.recordLogininfor(param.getPhone(), Constants.LOGIN_SUCCESS, "登录成功");
        // 删除验证码
        redisService.deleteObject(verifyKey);
        return userInfo;
    }

    /**
     * 一键登录
     *
     * @param param    登录参数
     * @return 登录结果
     */
    public LoginUser loginOnesTep(VerifyPhoneDTO param) {
        LoginUser userInfo = new LoginUser();
        return userInfo;
    }

    /**
     * 微信授权登录
     *
     * @param param    授权参数
     * @return 登录结果
     */
//    public LoginUser loginWeiXin(WeiXinLoginDTO param) throws WxErrorException {
//        // 获取微信用户session
//        WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(param.getCode());
//        LoginUser userInfo = null;
//        if (StringUtils.isNotEmpty(session.getOpenid())) {
//            // 解析电话号码
//            String phoneNumber;
//            byte[] byEncrypdata = Base64.decodeBase64(param.getEncryptedData().getBytes());
//            byte[] byIvdata = Base64.decodeBase64(param.getIv().getBytes());
//            byte[] bySessionkey = Base64.decodeBase64(session.getSessionKey().getBytes());
//            AlgorithmParameterSpec ivSpec = new IvParameterSpec(byIvdata);
//            try {
//                SecretKeySpec keySpec = new SecretKeySpec(bySessionkey, "AES");
//                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
//                String phoneResult = new String(cipher.doFinal(byEncrypdata), StandardCharsets.UTF_8);
//                JSONObject phoneObject = JSONObject.parseObject(phoneResult);
//                phoneNumber = phoneObject.getString("phoneNumber");
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw new ServiceException(MessageUtils.message("user.phone.number"));
//            }
//            // 根据openId查询是否存在这个用户
//            R<LoginUser> userResult = remoteUserService.getUserInfo(session.getOpenid(), language, SecurityConstants.INNER);
//            if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData().getUser())) {
//                // 创建新用户
//                User user = new User();
//                user.setUserName(IdUtils.fastSimpleUUID());
//                user.setNickName(param.getNickName());
//                user.setPhone(phoneNumber);
//                user.setHeadImg(param.getAvatarUrl());
//                user.setOpenId(session.getOpenid());
//                user.setSessionKey(session.getSessionKey());
//                if (Objects.equals(param.getGender(), 0)) {
//                    user.setSex("2");
//                } else if (Objects.equals(param.getGender(), 1)) {
//                    user.setSex("0");
//                } else if (Objects.equals(param.getGender(), 2)) {
//                    user.setSex("1");
//                }
//                R<?> registerResult = remoteUserService.registerUserInfo(user, language, SecurityConstants.INNER);
//                if (R.FAIL == registerResult.getCode()) {
//                    throw new ServiceException(registerResult.getMsg());
//                }
//                sysRecordLogService.recordLogininfor(session.getOpenid(), Constants.REGISTER, MessageUtils.message("user.register.success"));
//            } else {
//                // 存在就更新用户信息
//                User user = userResult.getData().getUser();
//                R<?> updateResult = remoteUserService.updateUserInfo(user, language, SecurityConstants.INNER);
//                if (R.FAIL == updateResult.getCode()) {
//                    throw new ServiceException(updateResult.getMsg());
//                }
//                sysRecordLogService.recordLogininfor(session.getOpenid(), Constants.REGISTER, MessageUtils.message("user.update.success"));
//            }
//            // 根据openId查询用户
//            R<LoginUser> userResults = remoteUserService.getUserInfo(session.getOpenid(), language, SecurityConstants.INNER);
//            userInfo = userResults.getData();
//            // 用户信息
//            User user = userResults.getData().getUser();
//            if (StringUtils.isNotNull(user)) {
//                // 是否删除
//                if (UserStatus.DELETED.getCode().equals(user.getDeleteFlag())) {
//                    sysRecordLogService.recordLogininfor(phoneNumber, Constants.LOGIN_FAIL, MessageUtils.message("user.delete"));
//                    throw new ServiceException(MessageUtils.message("user.login.sorry") + phoneNumber + MessageUtils.message("user.login.sorry.delete"));
//                }
//                // 是否停用
//                if (UserStatus.DISABLE.getCode().equals(user.getUserStatus())) {
//                    sysRecordLogService.recordLogininfor(phoneNumber, Constants.LOGIN_FAIL, MessageUtils.message("user.blocked"));
//                    throw new ServiceException(MessageUtils.message("user.login.sorry") + phoneNumber + MessageUtils.message("user.login.sorry.blocked"));
//                }
//            }
//            sysRecordLogService.recordLogininfor(session.getOpenid(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
//        }
//        return userInfo;
//    }

    /**
     * 退出登录
     *
     * @param loginName 请求对象
     * @return 结果
     */
    public void logout(String loginName) {
        sysRecordLogService.recordLogininfor(loginName, Constants.LOGOUT, "退出成功");
    }

}
