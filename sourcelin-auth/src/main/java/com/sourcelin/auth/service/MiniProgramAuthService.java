package com.sourcelin.auth.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcelin.auth.config.properties.MiniProgramProperties;
import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.api.model.LoginUser;
import com.sourcelin.blog.api.service.RemoteUserService;
import com.sourcelin.common.core.constant.Constants;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.core.enums.UserStatus;
import com.sourcelin.common.core.exception.ServiceException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.ip.IpUtils;
import com.sourcelin.common.core.utils.uuid.IdUtils;
import com.sourcelin.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class MiniProgramAuthService {
    private static final String BIND_TOKEN_CACHE_PREFIX = "auth:mini:bind:";
    private static final String MINI_USERNAME_PREFIX = "mini_";
    private static final String MINI_NICKNAME_PREFIX = "微信用户";
    private static final String MINI_INTRODUCTION = "来自微信小程序";

    @Autowired
    private MiniProgramProperties miniProgramProperties;

    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private BlogLoginService blogLoginService;

    @Autowired
    private SysRecordLogService sysRecordLogService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ObjectMapper objectMapper;

    public MiniProgramLoginAttempt login(String code) {
        if (StringUtils.isBlank(code)) {
            throw new ServiceException("微信登录 code 不能为空");
        }
        ensureConfigured();

        MiniProgramSession session = exchangeCode(code);
        LoginUser loginUser = getUserInfoByOpenId(session.getOpenId());
        if (loginUser != null) {
            validateUser(loginUser.getUser(), session.getOpenId());
            updateWechatBinding(loginUser.getUser(), session, false);
            sysRecordLogService.recordLogininfor(session.getOpenId(), Constants.LOGIN_SUCCESS, "微信快捷登录成功");
            return MiniProgramLoginAttempt.bound(loginUser);
        }

        LoginUser createdUser = registerMiniProgramUser(session);
        sysRecordLogService.recordLogininfor(createdUser.getUsername(), Constants.LOGIN_SUCCESS, "微信快捷登录并自动创建账号成功");
        return MiniProgramLoginAttempt.newUser(createdUser);
    }

    public MiniProgramBindContext prepareBind(String code) {
        if (StringUtils.isBlank(code)) {
            throw new ServiceException("微信登录 code 不能为空");
        }
        ensureConfigured();

        MiniProgramSession session = exchangeCode(code);
        LoginUser existing = getUserInfoByOpenId(session.getOpenId());
        if (existing != null && existing.getUser() != null) {
            validateUser(existing.getUser(), session.getOpenId());
            throw new ServiceException("当前微信已绑定账号，可直接使用微信一键登录");
        }

        String bindToken = IdUtils.fastSimpleUUID();
        redisService.setCacheObject(
            BIND_TOKEN_CACHE_PREFIX + bindToken,
            new MiniProgramBindContext(session.getOpenId(), session.getSessionKey()),
            miniProgramProperties.getBindTokenTtlSeconds(),
            TimeUnit.SECONDS
        );
        return new MiniProgramBindContext(bindToken, session.getOpenId(), session.getSessionKey());
    }

    public LoginUser bindExistingAccount(String bindToken, String username, String password) {
        if (StringUtils.isAnyBlank(bindToken, username, password)) {
            throw new ServiceException("绑定参数不能为空");
        }
        MiniProgramBindContext bindContext = redisService.getCacheObject(BIND_TOKEN_CACHE_PREFIX + bindToken);
        if (bindContext == null || StringUtils.isBlank(bindContext.getOpenId())) {
            throw new ServiceException("绑定票据已失效，请重新发起微信登录");
        }

        LoginUser loginUser = blogLoginService.login(username, password);
        User user = loginUser.getUser();
        ensureOpenIdAvailable(bindContext.getOpenId(), user.getId());
        user.setOpenId(bindContext.getOpenId());
        user.setSessionKey(bindContext.getSessionKey());
        if (StringUtils.isBlank(user.getBindWechatId())) {
            user.setBindWechatId(bindContext.getOpenId());
        }
        user.setLoginIp(IpUtils.getIpAddr());
        user.setLoginDate(new Date());
        R<Integer> updateResult = remoteUserService.updateUserInfo(user, SecurityConstants.INNER);
        if (!R.isSuccess(updateResult)) {
            throw new ServiceException(updateResult == null ? "绑定微信失败" : updateResult.getMsg());
        }
        redisService.deleteObject(BIND_TOKEN_CACHE_PREFIX + bindToken);
        sysRecordLogService.recordLogininfor(user.getUsername(), Constants.LOGIN_SUCCESS, "微信绑定并登录成功");
        return loginUser;
    }

    private LoginUser registerMiniProgramUser(MiniProgramSession session) {
        User user = new User();
        user.setUsername(generateMiniUsername());
        user.setNickname(buildMiniNickname(user.getUsername()));
        user.setPassword(IdUtils.fastSimpleUUID());
        user.setUuid(IdUtils.simpleUUID());
        user.setIntroduction(MINI_INTRODUCTION);
        user.setOpenId(session.getOpenId());
        user.setBindWechatId(session.getOpenId());
        user.setSessionKey(session.getSessionKey());
        user.setLoginIp(IpUtils.getIpAddr());
        user.setLoginDate(new Date());

        R<Long> registerResult = remoteUserService.registerUserInfo(user, SecurityConstants.INNER);
        if (!R.isSuccess(registerResult)) {
            throw new ServiceException(registerResult == null ? "微信登录自动注册失败" : registerResult.getMsg());
        }

        LoginUser createdUser = getUserInfoByOpenId(session.getOpenId());
        if (createdUser == null) {
            createdUser = getUserInfoByUsername(user.getUsername());
            if (createdUser != null && createdUser.getUser() != null) {
                updateWechatBinding(createdUser.getUser(), session, true);
                LoginUser refreshedUser = getUserInfoByOpenId(session.getOpenId());
                if (refreshedUser != null && refreshedUser.getUser() != null) {
                    createdUser = refreshedUser;
                } else {
                    createdUser.getUser().setOpenId(session.getOpenId());
                    createdUser.getUser().setBindWechatId(session.getOpenId());
                    createdUser.getUser().setSessionKey(session.getSessionKey());
                }
            }
        }
        if (createdUser == null || createdUser.getUser() == null) {
            throw new ServiceException("自动创建账号成功但未获取到用户信息");
        }
        return createdUser;
    }

    private String generateMiniUsername() {
        return MINI_USERNAME_PREFIX + IdUtils.fastSimpleUUID().substring(0, 12);
    }

    private String buildMiniNickname(String username) {
        if (StringUtils.isBlank(username) || username.length() <= 4) {
            return MINI_NICKNAME_PREFIX;
        }
        return MINI_NICKNAME_PREFIX + username.substring(username.length() - 4);
    }

    protected MiniProgramSession exchangeCode(String code) {
        String url = UriComponentsBuilder.fromHttpUrl(miniProgramProperties.getCode2SessionUrl())
            .queryParam("appid", miniProgramProperties.getAppId())
            .queryParam("secret", miniProgramProperties.getAppSecret())
            .queryParam("js_code", code)
            .queryParam("grant_type", "authorization_code")
            .build(true)
            .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || StringUtils.isBlank(response.getBody())) {
            throw new ServiceException("微信登录服务暂不可用，请稍后重试");
        }
        try {
            WechatCode2SessionResponse body = objectMapper.readValue(response.getBody(), WechatCode2SessionResponse.class);
            if (body.getErrCode() != null && body.getErrCode() != 0) {
                throw new ServiceException(StringUtils.isNotBlank(body.getErrMsg()) ? body.getErrMsg() : "微信登录失败");
            }
            if (StringUtils.isAnyBlank(body.getOpenId(), body.getSessionKey())) {
                throw new ServiceException("微信登录返回数据不完整");
            }
            return new MiniProgramSession(body.getOpenId(), body.getSessionKey());
        } catch (IOException ex) {
            throw new ServiceException("解析微信登录响应失败");
        }
    }

    private void ensureConfigured() {
        if (StringUtils.isAnyBlank(miniProgramProperties.getAppId(), miniProgramProperties.getAppSecret())) {
            throw new ServiceException("小程序登录配置缺失，请先配置 appId/appSecret");
        }
    }

    private LoginUser getUserInfoByOpenId(String openId) {
        R<LoginUser> userResult = remoteUserService.getUserInfoByOpenId(openId, SecurityConstants.INNER);
        if (userResult == null || !R.isSuccess(userResult)) {
            return null;
        }
        return userResult.getData();
    }

    private LoginUser getUserInfoByUsername(String username) {
        R<LoginUser> userResult = remoteUserService.getUserInfo(username, SecurityConstants.INNER);
        if (userResult == null || !R.isSuccess(userResult)) {
            return null;
        }
        return userResult.getData();
    }

    private void ensureOpenIdAvailable(String openId, Long currentUserId) {
        LoginUser existing = getUserInfoByOpenId(openId);
        if (existing != null && existing.getUser() != null && !existing.getUser().getId().equals(currentUserId)) {
            throw new ServiceException("该微信已绑定其他账号");
        }
    }

    private void validateUser(User user, String logKey) {
        if (user == null) {
            throw new ServiceException("绑定账号不存在");
        }
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            sysRecordLogService.recordLogininfor(logKey, Constants.LOGIN_FAIL, "账号已被删除");
            throw new ServiceException("账号已被删除");
        }
        if (UserStatus.DISABLE.getCode().equals(user.getUserStatus())) {
            sysRecordLogService.recordLogininfor(logKey, Constants.LOGIN_FAIL, "账号已被停用");
            throw new ServiceException("账号已被停用");
        }
    }

    private void updateWechatBinding(User user, MiniProgramSession session, boolean forceBindWechatId) {
        user.setOpenId(session.getOpenId());
        user.setSessionKey(session.getSessionKey());
        if (forceBindWechatId || StringUtils.isBlank(user.getBindWechatId())) {
            user.setBindWechatId(session.getOpenId());
        }
        user.setLoginIp(IpUtils.getIpAddr());
        user.setLoginDate(new Date());
        R<Integer> updateResult = remoteUserService.updateUserInfo(user, SecurityConstants.INNER);
        if (!R.isSuccess(updateResult)) {
            throw new ServiceException(updateResult == null ? "更新微信登录态失败" : updateResult.getMsg());
        }
    }

    public static class MiniProgramLoginAttempt {
        private final boolean bound;
        private final boolean newUser;
        private final LoginUser loginUser;

        private MiniProgramLoginAttempt(boolean bound, boolean newUser, LoginUser loginUser) {
            this.bound = bound;
            this.newUser = newUser;
            this.loginUser = loginUser;
        }

        public static MiniProgramLoginAttempt bound(LoginUser loginUser) {
            return new MiniProgramLoginAttempt(true, false, loginUser);
        }

        public static MiniProgramLoginAttempt newUser(LoginUser loginUser) {
            return new MiniProgramLoginAttempt(false, true, loginUser);
        }

        public boolean isBound() {
            return bound;
        }

        public boolean isNewUser() {
            return newUser;
        }

        public LoginUser getLoginUser() {
            return loginUser;
        }
    }

    protected static class MiniProgramSession {
        private final String openId;
        private final String sessionKey;

        protected MiniProgramSession(String openId, String sessionKey) {
            this.openId = openId;
            this.sessionKey = sessionKey;
        }

        public String getOpenId() {
            return openId;
        }

        public String getSessionKey() {
            return sessionKey;
        }
    }

    public static class MiniProgramBindContext implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String bindToken;
        private final String openId;
        private final String sessionKey;

        protected MiniProgramBindContext(String bindToken, String openId, String sessionKey) {
            this.bindToken = bindToken;
            this.openId = openId;
            this.sessionKey = sessionKey;
        }

        protected MiniProgramBindContext(String openId, String sessionKey) {
            this(null, openId, sessionKey);
        }

        public String getBindToken() {
            return bindToken;
        }

        public String getOpenId() {
            return openId;
        }

        public String getSessionKey() {
            return sessionKey;
        }
    }

    protected static class WechatCode2SessionResponse {
        @JsonProperty("openid")
        private String openId;

        @JsonProperty("session_key")
        private String sessionKey;

        @JsonProperty("errcode")
        private Integer errCode;

        @JsonProperty("errmsg")
        private String errMsg;

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }

        public String getSessionKey() {
            return sessionKey;
        }

        public void setSessionKey(String sessionKey) {
            this.sessionKey = sessionKey;
        }

        public Integer getErrCode() {
            return errCode;
        }

        public void setErrCode(Integer errCode) {
            this.errCode = errCode;
        }

        public String getErrMsg() {
            return errMsg;
        }

        public void setErrMsg(String errMsg) {
            this.errMsg = errMsg;
        }
    }
}
