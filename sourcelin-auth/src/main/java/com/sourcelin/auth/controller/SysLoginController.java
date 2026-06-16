package com.sourcelin.auth.controller;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import com.sourcelin.auth.dto.MiniProgramBindDTO;
import com.sourcelin.auth.dto.LoginDTO;
import com.sourcelin.auth.dto.RegisterDTO;
import com.sourcelin.auth.dto.EmailCodeRequestDTO;
import com.sourcelin.auth.dto.EmailLoginDTO;
import com.sourcelin.auth.dto.WeiXinLoginDTO;
import com.sourcelin.auth.vo.AuthTokenVo;
import com.sourcelin.auth.vo.CaptchaVo;
import com.sourcelin.auth.service.BlogLoginService;
import com.sourcelin.auth.service.MiniProgramAuthService;
import com.sourcelin.auth.service.RegisterService;
import com.sourcelin.auth.service.SysLoginService;
import com.sourcelin.auth.service.EmailCodeService;
import com.sourcelin.auth.service.ShearCaptchaService;
import com.sourcelin.auth.vo.MiniProgramBindPrepareVo;
import com.sourcelin.auth.vo.MiniProgramLoginVo;
import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.api.model.LoginUser;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.core.enums.UserStatus;
import com.sourcelin.common.core.exception.ServiceException;
import com.sourcelin.common.core.utils.ServletUtils;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.ip.IpUtils;
import com.sourcelin.common.core.utils.sign.RsaUtils;
import com.sourcelin.common.security.stp.StpAdminUtil;
import com.sourcelin.common.security.stp.StpBlogUtil;
import com.sourcelin.system.api.model.SysLoginUser;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

@Tag(name = "统一认证接口")
@Validated
@RestController
public class SysLoginController {
    private static final String LOGIN_TYPE_BLOG = "blog";
    private static final String LOGIN_TYPE_MINI = "mini";
    private static final String LOGIN_TYPE_ADMIN = "admin";

    @Autowired
    private SysLoginService sysLoginService;

    @Autowired
    private BlogLoginService blogLoginService;

    @Autowired
    private RegisterService registerService;

    @Autowired
    private EmailCodeService emailCodeService;

    @Autowired
    private ShearCaptchaService shearCaptchaService;

    @Autowired
    private MiniProgramAuthService miniProgramAuthService;

    @GetMapping("/captcha")
    public CaptchaVo getCaptcha(@RequestParam(value = "loginType", required = false) String loginType) {
        return shearCaptchaService.createCaptcha(normalizeLoginType(loginType));
    }

    @PostMapping("login")
    public AuthTokenVo login(@Valid @RequestBody LoginDTO param) {
        String loginType = normalizeLoginType(param.getLoginType());
        shearCaptchaService.verifyCaptcha(param.getCaptchaCode(), param.getCaptchaUuid(), loginType);
        param.setPassword(decryptPasswordIfNeeded(param.getPassword()));
        if (isBlogClientLogin(loginType)) {
            return blogLogin(param, loginType);
        }
        return adminLogin(param);
    }

    private AuthTokenVo adminLogin(LoginDTO param) {
        SysLoginUser userInfo = sysLoginService.login(param.getUsername(), param.getPassword());
        Long userId = userInfo.getSysUser().getUserId();
        StpAdminUtil.login(userId);
        saveLoginContext(StpAdminUtil.stpLogic, userInfo);
        return buildTokenPayload(StpAdminUtil.stpLogic, LOGIN_TYPE_ADMIN);
    }

    private AuthTokenVo blogLogin(LoginDTO param, String loginType) {
        LoginUser userInfo = blogLoginService.login(param.getUsername(), param.getPassword());
        StpBlogUtil.login(userInfo.getUser().getId());
        saveLoginContext(StpBlogUtil.stpLogic, userInfo);
        return buildTokenPayload(StpBlogUtil.stpLogic, loginType);
    }

    @DeleteMapping("logout")
    public Boolean logout() {
        if (StpAdminUtil.isLogin()) {
            Object sessionUser = StpAdminUtil.stpLogic.getSession().get("loginUser");
            if (sessionUser instanceof SysLoginUser) {
                sysLoginService.logout(((SysLoginUser) sessionUser).getUsername());
            }
            StpAdminUtil.logout();
        }
        if (StpBlogUtil.isLogin()) {
            StpBlogUtil.logout();
        }
        return true;
    }

    @PostMapping("refresh")
    public Integer refresh() {
        if (StpAdminUtil.isLogin()) {
            StpAdminUtil.renewTimeout(2592000);
        }
        if (StpBlogUtil.isLogin()) {
            StpBlogUtil.renewTimeout(2592000);
        }
        return 2592000;
    }

    @PostMapping("register")
    public Object register(@Valid @RequestBody RegisterDTO registerParam) {
        String loginType = normalizeLoginType(registerParam.getLoginType());
        shearCaptchaService.verifyCaptcha(registerParam.getCaptchaCode(), registerParam.getCaptchaUuid(), loginType);
        registerParam.setPassword(decryptPasswordIfNeeded(registerParam.getPassword()));
        if (isBlogClientLogin(loginType)) {
            Long userId = registerService.registerUser(
                registerParam.getUsername(),
                registerParam.getPassword(),
                registerParam.getPhone(),
                registerParam.getEmail(),
                registerParam.getEmailCode(),
                registerParam.getRegisterRequestId()
            );
            StpBlogUtil.login(userId);
            LoginUser loginUser = new LoginUser();
            loginUser.setUserid(userId);
            loginUser.setUsername(registerParam.getUsername());
            saveLoginContext(StpBlogUtil.stpLogic, loginUser);
            return buildTokenPayload(StpBlogUtil.stpLogic, loginType);
        }
        sysLoginService.register(registerParam.getUsername(), registerParam.getPassword());
        return true;
    }

    @PostMapping("/email/code")
    public Boolean sendEmailCode(@Valid @RequestBody EmailCodeRequestDTO request) {
        if (!StpBlogUtil.isLogin()) {
            shearCaptchaService.verifyCaptchaWithoutConsume(request.getCaptchaCode(), request.getCaptchaUuid(), LOGIN_TYPE_BLOG);
        }
        emailCodeService.sendEmailCode(request.getEmail());
        return true;
    }

    @PostMapping("/email/login")
    public AuthTokenVo emailLogin(@Valid @RequestBody EmailLoginDTO loginDTO) {
        shearCaptchaService.verifyCaptcha(loginDTO.getCaptchaCode(), loginDTO.getCaptchaUuid(), LOGIN_TYPE_BLOG);
        String loginType = normalizeEmailLoginType(loginDTO.getLoginType());

        if ("code".equals(loginType)) {
            if (StringUtils.isEmpty(loginDTO.getEmailCode())) {
                throw new ServiceException("邮箱验证码不能为空");
            }
            if (!emailCodeService.verifyCode(loginDTO.getEmail(), loginDTO.getEmailCode())) {
                throw new ServiceException("邮箱验证码错误");
            }
            R<LoginUser> userResult = blogLoginService.getUserInfoByEmail(loginDTO.getEmail());
            if (userResult == null || userResult.getData() == null) {
                throw new ServiceException("用户不存在");
            }
            LoginUser userInfo = userResult.getData();
            // 校验用户状态：已删除或已停用的账号不允许登录
            User user = userInfo.getUser();
            if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
                throw new ServiceException("账号已被删除");
            }
            if (UserStatus.DISABLE.getCode().equals(user.getUserStatus())) {
                throw new ServiceException("账号已被停用");
            }
            StpBlogUtil.login(userInfo.getUser().getId());
            saveLoginContext(StpBlogUtil.stpLogic, userInfo);
            return buildTokenPayload(StpBlogUtil.stpLogic, LOGIN_TYPE_BLOG);
        }
        if (StringUtils.isEmpty(loginDTO.getPassword())) {
            throw new ServiceException("密码不能为空");
        }
        loginDTO.setPassword(decryptPasswordIfNeeded(loginDTO.getPassword()));
        LoginUser userInfo = blogLoginService.login(loginDTO.getEmail(), loginDTO.getPassword());
        StpBlogUtil.login(userInfo.getUser().getId());
        saveLoginContext(StpBlogUtil.stpLogic, userInfo);
        return buildTokenPayload(StpBlogUtil.stpLogic, LOGIN_TYPE_BLOG);
    }

    @PostMapping("/wechat/mini/login")
    public MiniProgramLoginVo miniProgramLogin(@Valid @RequestBody WeiXinLoginDTO loginDTO) {
        MiniProgramAuthService.MiniProgramLoginAttempt attempt = miniProgramAuthService.login(loginDTO.getCode());
        MiniProgramLoginVo result = new MiniProgramLoginVo();
        result.setBound(attempt.isBound());
        result.setNewUser(attempt.isNewUser());
        LoginUser userInfo = attempt.getLoginUser();
        StpBlogUtil.login(userInfo.getUser().getId());
        saveLoginContext(StpBlogUtil.stpLogic, userInfo);
        result.setToken(buildTokenPayload(StpBlogUtil.stpLogic, LOGIN_TYPE_MINI));
        return result;
    }

    @PostMapping("/wechat/mini/prepare-bind")
    public MiniProgramBindPrepareVo miniProgramPrepareBind(@Valid @RequestBody WeiXinLoginDTO loginDTO) {
        MiniProgramAuthService.MiniProgramBindContext bindContext = miniProgramAuthService.prepareBind(loginDTO.getCode());
        MiniProgramBindPrepareVo result = new MiniProgramBindPrepareVo();
        result.setBindToken(bindContext.getBindToken());
        result.setBindMessage("请登录已有账号完成绑定，后续可直接使用微信登录");
        return result;
    }

    @PostMapping("/wechat/mini/bind")
    public AuthTokenVo miniProgramBind(@Valid @RequestBody MiniProgramBindDTO bindDTO) {
        shearCaptchaService.verifyCaptcha(
            bindDTO.getCaptchaCode(),
            bindDTO.getCaptchaUuid(),
            LOGIN_TYPE_MINI
        );
        LoginUser userInfo = miniProgramAuthService.bindExistingAccount(
            bindDTO.getBindToken(),
            bindDTO.getUsername(),
            decryptPasswordIfNeeded(bindDTO.getPassword())
        );
        StpBlogUtil.login(userInfo.getUser().getId());
        saveLoginContext(StpBlogUtil.stpLogic, userInfo);
        return buildTokenPayload(StpBlogUtil.stpLogic, LOGIN_TYPE_MINI);
    }

    private String decryptPasswordIfNeeded(String password) {
        if (StringUtils.isEmpty(password)) {
            return password;
        }
        try {
            return RsaUtils.decryptByPrivateKey(password);
        } catch (Exception ex) {
            throw new ServiceException("密码格式错误，请升级客户端后重试");
        }
    }

    private AuthTokenVo buildTokenPayload(StpLogic stpLogic, String loginType) {
        String tokenPrefix = SaManager.getConfig() == null ? null : SaManager.getConfig().getTokenPrefix();
        return AuthTokenVo.builder()
            .token(stpLogic.getTokenValue())
            .tokenName(stpLogic.getTokenName())
            .tokenPrefix(StringUtils.isEmpty(tokenPrefix) ? "Bearer" : tokenPrefix)
            .expiresIn(2592000)
            .loginType(loginType)
            .build();
    }

    private void saveLoginContext(StpLogic stpLogic, Object loginUser) {
        if (stpLogic == null) {
            return;
        }
        String ipaddr = IpUtils.getIpAddr();
        long loginTime = System.currentTimeMillis();
        String userAgent = getUserAgent();
        String browser = resolveBrowser(userAgent);
        String os = resolveOs(userAgent);
        String loginLocation = resolveLoginLocation(ipaddr);

        if (loginUser instanceof SysLoginUser) {
            SysLoginUser user = (SysLoginUser) loginUser;
            user.setIpaddr(ipaddr);
            user.setLoginTime(loginTime);
            user.setLoginLocation(loginLocation);
            user.setBrowser(browser);
            user.setOs(os);
        }
        if (loginUser instanceof LoginUser) {
            LoginUser user = (LoginUser) loginUser;
            user.setIpaddr(ipaddr);
            user.setLoginTime(loginTime);
        }

        SaSession session = stpLogic.getSession();
        if (loginUser != null) {
            session.set("loginUser", loginUser);
            session.set(SecurityConstants.LOGIN_USER, loginUser);
        }
        session.set(SecurityConstants.LOGIN_IP, ipaddr);
        session.set(SecurityConstants.LOGIN_LOCATION, loginLocation);
        session.set(SecurityConstants.LOGIN_BROWSER, browser);
        session.set(SecurityConstants.LOGIN_OS, os);
        session.set(SecurityConstants.LOGIN_TIME, loginTime);

        session.set("ipaddr", ipaddr);
        session.set("loginLocation", loginLocation);
        session.set("browser", browser);
        session.set("os", os);
        session.set("loginTime", loginTime);
    }

    private String getUserAgent() {
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return "";
        }
        String userAgent = request.getHeader("User-Agent");
        return StringUtils.isEmpty(userAgent) ? "" : userAgent;
    }

    private String resolveBrowser(String userAgent) {
        if (StringUtils.isEmpty(userAgent)) {
            return "未知";
        }
        if (StringUtils.containsIgnoreCase(userAgent, "Edg/")) {
            return "Edge";
        }
        if (StringUtils.containsIgnoreCase(userAgent, "OPR/") || StringUtils.containsIgnoreCase(userAgent, "Opera")) {
            return "Opera";
        }
        if (StringUtils.containsIgnoreCase(userAgent, "Chrome/")) {
            return "Chrome";
        }
        if (StringUtils.containsIgnoreCase(userAgent, "Firefox/")) {
            return "Firefox";
        }
        if (StringUtils.containsIgnoreCase(userAgent, "Safari/")) {
            return "Safari";
        }
        if (StringUtils.containsIgnoreCase(userAgent, "MSIE") || StringUtils.containsIgnoreCase(userAgent, "Trident/")) {
            return "IE";
        }
        return "其他";
    }

    private String resolveOs(String userAgent) {
        if (StringUtils.isEmpty(userAgent)) {
            return "未知";
        }
        if (StringUtils.containsIgnoreCase(userAgent, "Windows")) {
            return "Windows";
        }
        if (StringUtils.containsIgnoreCase(userAgent, "Android")) {
            return "Android";
        }
        if (
            StringUtils.containsIgnoreCase(userAgent, "iPhone")
                || StringUtils.containsIgnoreCase(userAgent, "iPad")
                || StringUtils.containsIgnoreCase(userAgent, "iPod")
        ) {
            return "iOS";
        }
        if (StringUtils.containsIgnoreCase(userAgent, "Mac OS") || StringUtils.containsIgnoreCase(userAgent, "Macintosh")) {
            return "Mac OS";
        }
        if (StringUtils.containsIgnoreCase(userAgent, "Linux")) {
            return "Linux";
        }
        return "其他";
    }

    private String resolveLoginLocation(String ipaddr) {
        if (StringUtils.isEmpty(ipaddr) || StringUtils.equalsAnyIgnoreCase(ipaddr, "unknown", "null")) {
            return "未知";
        }
        if (IpUtils.internalIp(ipaddr)) {
            return "内网IP";
        }
        return "外网IP";
    }

    private String normalizeLoginType(String loginType) {
        if (StringUtils.isEmpty(loginType)) {
            return LOGIN_TYPE_BLOG;
        }
        if (!LOGIN_TYPE_BLOG.equals(loginType) && !LOGIN_TYPE_MINI.equals(loginType) && !LOGIN_TYPE_ADMIN.equals(loginType)) {
            throw new ServiceException("loginType 仅支持 blog/mini/admin");
        }
        return loginType;
    }

    private boolean isBlogClientLogin(String loginType) {
        return LOGIN_TYPE_BLOG.equals(loginType) || LOGIN_TYPE_MINI.equals(loginType);
    }

    private String normalizeEmailLoginType(String loginType) {
        if (StringUtils.isEmpty(loginType)) {
            return "password";
        }
        if (!"code".equals(loginType) && !"password".equals(loginType)) {
            throw new ServiceException("emailLoginType 仅支持 code/password");
        }
        return loginType;
    }
}
