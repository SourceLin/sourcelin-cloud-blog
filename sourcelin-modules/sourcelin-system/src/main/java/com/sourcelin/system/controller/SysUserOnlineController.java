package com.sourcelin.system.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpLogic;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import com.sourcelin.common.security.stp.StpAdminUtil;
import com.sourcelin.common.security.stp.StpBlogUtil;
import com.sourcelin.system.api.model.SysLoginUser;
import com.sourcelin.system.domain.SysUserOnline;
import com.sourcelin.system.service.ISysUserOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 在线用户监控
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/online")
public class SysUserOnlineController extends BaseController
{
    private static final int TOKEN_SEARCH_PAGE_SIZE = 200;
    private static final String LOGIN_TYPE_ALL = "all";
    private static final String LOGIN_TYPE_ADMIN = "admin";
    private static final String LOGIN_TYPE_BLOG = "blog";
    private static final String LOGIN_TYPE_APP = "app";

    @Autowired
    private ISysUserOnlineService userOnlineService;

    @SaCheckLogin(type = "admin")
    @GetMapping("/list")
    public PageResult<SysUserOnline> list(String ipaddr, String userName, @RequestParam(required = false) String loginType)
    {
        String normalizedLoginType = normalizeLoginType(loginType);
        boolean canListAll = hasPermission("monitor:online:list");
        boolean canListAdmin = canListAll || hasPermission("monitor:online:list:admin");
        boolean canListBlog = canListAll || hasPermission("monitor:online:list:blog");
        boolean canListApp = canListAll || hasPermission("monitor:online:list:app");

        if (LOGIN_TYPE_ADMIN.equals(normalizedLoginType) && !canListAdmin)
        {
            StpAdminUtil.stpLogic.checkPermission("monitor:online:list:admin");
        }
        if (LOGIN_TYPE_BLOG.equals(normalizedLoginType) && !canListBlog)
        {
            StpAdminUtil.stpLogic.checkPermission("monitor:online:list:blog");
        }
        if (LOGIN_TYPE_APP.equals(normalizedLoginType) && !canListApp)
        {
            StpAdminUtil.stpLogic.checkPermission("monitor:online:list:app");
        }
        if (LOGIN_TYPE_ALL.equals(normalizedLoginType) && !canListAdmin && !canListBlog && !canListApp)
        {
            StpAdminUtil.stpLogic.checkPermission("monitor:online:list");
        }

        PageDomain pageDomain = TableSupport.getPageDomain();
        int page = Math.max(pageDomain.getPage(), 1);
        int pageSize = Math.max(pageDomain.getPageSize(), 1);
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();

        if ((LOGIN_TYPE_ALL.equals(normalizedLoginType) || LOGIN_TYPE_ADMIN.equals(normalizedLoginType)) && canListAdmin)
        {
            collectOnlineUsers(StpAdminUtil.stpLogic, LOGIN_TYPE_ADMIN, ipaddr, userName, userOnlineList);
        }
        if ((LOGIN_TYPE_ALL.equals(normalizedLoginType) || LOGIN_TYPE_BLOG.equals(normalizedLoginType)) && canListBlog)
        {
            collectOnlineUsers(StpBlogUtil.stpLogic, LOGIN_TYPE_BLOG, ipaddr, userName, userOnlineList);
        }

        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));

        int total = userOnlineList.size();
        int fromIndex = Math.min((page - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<SysUserOnline> items = userOnlineList.subList(fromIndex, toIndex);
        return PageResult.of(items, total, page, pageSize);
    }

    /**
     * 强退用户
     */
    @SaCheckLogin(type = "admin")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public Boolean forceLogout(@PathVariable String tokenId, @RequestParam(required = false) String loginType)
    {
        String normalizedLoginType = normalizeLoginType(loginType);
        boolean canForceAll = hasPermission("monitor:online:forceLogout");
        boolean canForceAdmin = canForceAll || hasPermission("monitor:online:forceLogout:admin");
        boolean canForceBlog = canForceAll || hasPermission("monitor:online:forceLogout:blog");
        boolean canForceApp = canForceAll || hasPermission("monitor:online:forceLogout:app");

        if (LOGIN_TYPE_ADMIN.equals(normalizedLoginType))
        {
            if (!canForceAdmin)
            {
                StpAdminUtil.stpLogic.checkPermission("monitor:online:forceLogout:admin");
            }
            return forceLogoutByType(LOGIN_TYPE_ADMIN, tokenId);
        }
        if (LOGIN_TYPE_BLOG.equals(normalizedLoginType))
        {
            if (!canForceBlog)
            {
                StpAdminUtil.stpLogic.checkPermission("monitor:online:forceLogout:blog");
            }
            return forceLogoutByType(LOGIN_TYPE_BLOG, tokenId);
        }
        if (LOGIN_TYPE_APP.equals(normalizedLoginType))
        {
            if (!canForceApp)
            {
                StpAdminUtil.stpLogic.checkPermission("monitor:online:forceLogout:app");
            }
            return false;
        }

        if (!canForceAdmin && !canForceBlog && !canForceApp)
        {
            StpAdminUtil.stpLogic.checkPermission("monitor:online:forceLogout");
        }
        if (canForceAdmin && forceLogoutByType(LOGIN_TYPE_ADMIN, tokenId))
        {
            return true;
        }
        if (canForceBlog && forceLogoutByType(LOGIN_TYPE_BLOG, tokenId))
        {
            return true;
        }
        return false;
    }

    private void collectOnlineUsers(
        StpLogic stpLogic,
        String loginType,
        String ipaddr,
        String userName,
        List<SysUserOnline> userOnlineList
    )
    {
        List<String> tokenValues = listTokenValues(stpLogic);
        for (String tokenValue : tokenValues)
        {
            SysLoginUser user = resolveLoginUser(stpLogic, tokenValue);
            if (user == null)
            {
                continue;
            }
            user.setToken(tokenValue);
            SysUserOnline online = filterOnline(ipaddr, userName, user);
            if (online != null)
            {
                online.setLoginType(loginType);
                userOnlineList.add(online);
            }
        }
    }

    private SysUserOnline filterOnline(String ipaddr, String userName, SysLoginUser user)
    {
        if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName))
        {
            return userOnlineService.selectOnlineByInfo(ipaddr, userName, user);
        }
        if (StringUtils.isNotEmpty(ipaddr))
        {
            return userOnlineService.selectOnlineByIpaddr(ipaddr, user);
        }
        if (StringUtils.isNotEmpty(userName))
        {
            return userOnlineService.selectOnlineByUserName(userName, user);
        }
        return userOnlineService.loginUserToUserOnline(user);
    }

    private List<String> listTokenValues(StpLogic stpLogic)
    {
        int start = 0;
        List<String> tokenValues = new ArrayList<>();
        String tokenKeyPrefix = stpLogic.splicingKeyTokenValue("");
        while (true)
        {
            List<String> batch = stpLogic.searchTokenValue("", start, TOKEN_SEARCH_PAGE_SIZE, false);
            if (batch == null || batch.isEmpty())
            {
                break;
            }
            for (String item : batch)
            {
                String tokenValue = normalizeTokenValue(item, tokenKeyPrefix);
                if (StringUtils.isNotEmpty(tokenValue))
                {
                    tokenValues.add(tokenValue);
                }
            }
            if (batch.size() < TOKEN_SEARCH_PAGE_SIZE)
            {
                break;
            }
            start += TOKEN_SEARCH_PAGE_SIZE;
        }
        return tokenValues;
    }

    private SysLoginUser resolveLoginUser(StpLogic stpLogic, String tokenValue)
    {
        try
        {
            if (StringUtils.isEmpty(tokenValue))
            {
                return null;
            }
            Object loginId = stpLogic.getLoginIdByToken(tokenValue);
            if (loginId == null)
            {
                return null;
            }
            SaSession session = stpLogic.getSessionByLoginId(loginId, false);
            if (session == null)
            {
                return null;
            }
            Object sessionUser = session.get(SecurityConstants.LOGIN_USER);
            if (sessionUser == null)
            {
                sessionUser = session.get("loginUser");
            }
            SysLoginUser loginUser = toLoginUser(sessionUser);
            mergeSessionLoginMeta(session, loginUser);
            return loginUser;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private SysLoginUser toLoginUser(Object sessionUser)
    {
        if (sessionUser == null)
        {
            return null;
        }
        if (sessionUser instanceof SysLoginUser)
        {
            return (SysLoginUser) sessionUser;
        }
        SysLoginUser loginUser = new SysLoginUser();
        String username = toStringValue(readProperty(sessionUser, "username", "userName", "nickName", "nickname"));
        if (StringUtils.isEmpty(username))
        {
            Object userObj = readProperty(sessionUser, "sysUser", "user");
            username = toStringValue(readProperty(userObj, "userName", "username", "nickName", "nickname", "id"));
        }
        loginUser.setUsername(username);
        loginUser.setIpaddr(toStringValue(readProperty(sessionUser, "ipaddr", "ipAddr", "loginIp")));
        loginUser.setLoginLocation(toStringValue(readProperty(sessionUser, "loginLocation")));
        loginUser.setBrowser(toStringValue(readProperty(sessionUser, "browser")));
        loginUser.setOs(toStringValue(readProperty(sessionUser, "os")));
        loginUser.setLoginTime(resolveLong(readProperty(sessionUser, "loginTime")));
        if (StringUtils.isEmpty(loginUser.getUsername()))
        {
            return null;
        }
        return loginUser;
    }

    private void mergeSessionLoginMeta(SaSession session, SysLoginUser loginUser)
    {
        if (session == null || loginUser == null)
        {
            return;
        }
        if (StringUtils.isEmpty(loginUser.getIpaddr()))
        {
            loginUser.setIpaddr(toStringValue(session.get(SecurityConstants.LOGIN_IP)));
            if (StringUtils.isEmpty(loginUser.getIpaddr()))
            {
                loginUser.setIpaddr(toStringValue(session.get("ipaddr")));
            }
        }
        if (StringUtils.isEmpty(loginUser.getLoginLocation()))
        {
            loginUser.setLoginLocation(toStringValue(session.get(SecurityConstants.LOGIN_LOCATION)));
            if (StringUtils.isEmpty(loginUser.getLoginLocation()))
            {
                loginUser.setLoginLocation(toStringValue(session.get("loginLocation")));
            }
        }
        if (StringUtils.isEmpty(loginUser.getBrowser()))
        {
            loginUser.setBrowser(toStringValue(session.get(SecurityConstants.LOGIN_BROWSER)));
            if (StringUtils.isEmpty(loginUser.getBrowser()))
            {
                loginUser.setBrowser(toStringValue(session.get("browser")));
            }
        }
        if (StringUtils.isEmpty(loginUser.getOs()))
        {
            loginUser.setOs(toStringValue(session.get(SecurityConstants.LOGIN_OS)));
            if (StringUtils.isEmpty(loginUser.getOs()))
            {
                loginUser.setOs(toStringValue(session.get("os")));
            }
        }
        if (loginUser.getLoginTime() == null)
        {
            loginUser.setLoginTime(resolveLong(session.get(SecurityConstants.LOGIN_TIME)));
            if (loginUser.getLoginTime() == null)
            {
                loginUser.setLoginTime(resolveLong(session.get("loginTime")));
            }
        }
    }

    private String normalizeTokenValue(String item, String tokenKeyPrefix)
    {
        if (StringUtils.isEmpty(item))
        {
            return null;
        }
        if (StringUtils.isNotEmpty(tokenKeyPrefix) && item.startsWith(tokenKeyPrefix))
        {
            return item.substring(tokenKeyPrefix.length());
        }
        return item;
    }

    private Object readProperty(Object source, String... keys)
    {
        if (source == null || keys == null)
        {
            return null;
        }
        if (source instanceof Map)
        {
            Map<?, ?> dataMap = (Map<?, ?>) source;
            for (String key : keys)
            {
                Object value = dataMap.get(key);
                if (value != null)
                {
                    return value;
                }
            }
            return null;
        }
        for (String key : keys)
        {
            Object value = invokeGetter(source, key);
            if (value != null)
            {
                return value;
            }
        }
        return null;
    }

    private Object invokeGetter(Object source, String key)
    {
        if (source == null || StringUtils.isEmpty(key))
        {
            return null;
        }
        String suffix = Character.toUpperCase(key.charAt(0)) + key.substring(1);
        String[] methodNames = new String[] {"get" + suffix, "is" + suffix};
        for (String methodName : methodNames)
        {
            try
            {
                Method method = source.getClass().getMethod(methodName);
                return method.invoke(source);
            }
            catch (Exception ignored)
            {
                // ignore
            }
        }
        return null;
    }

    private String toStringValue(Object value)
    {
        if (value == null)
        {
            return null;
        }
        String text = String.valueOf(value);
        return StringUtils.isEmpty(text) ? null : text;
    }

    private Long resolveLong(Object value)
    {
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        if (value == null)
        {
            return null;
        }
        String text = String.valueOf(value);
        if (StringUtils.isEmpty(text))
        {
            return null;
        }
        try
        {
            return Long.parseLong(text);
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }

    private boolean forceLogoutByType(String loginType, String tokenId)
    {
        if (StringUtils.isEmpty(tokenId))
        {
            return false;
        }
        if (LOGIN_TYPE_ADMIN.equals(loginType))
        {
            if (StpAdminUtil.stpLogic.getLoginIdByToken(tokenId) == null)
            {
                return false;
            }
            StpAdminUtil.stpLogic.logoutByTokenValue(tokenId);
            return true;
        }
        if (LOGIN_TYPE_BLOG.equals(loginType))
        {
            if (StpBlogUtil.stpLogic.getLoginIdByToken(tokenId) == null)
            {
                return false;
            }
            StpBlogUtil.stpLogic.logoutByTokenValue(tokenId);
            return true;
        }
        return false;
    }

    private String normalizeLoginType(String loginType)
    {
        if (StringUtils.isEmpty(loginType))
        {
            return LOGIN_TYPE_ALL;
        }
        loginType = loginType.trim().toLowerCase();
        if (
            LOGIN_TYPE_ALL.equals(loginType)
                || LOGIN_TYPE_ADMIN.equals(loginType)
                || LOGIN_TYPE_BLOG.equals(loginType)
                || LOGIN_TYPE_APP.equals(loginType)
        )
        {
            return loginType;
        }
        throw new BusinessException(ResultCode.VALIDATION_ERROR, "loginType 仅支持 all/admin/blog/app");
    }

    private boolean hasPermission(String permission)
    {
        return StpAdminUtil.stpLogic.hasPermission(permission);
    }
}
