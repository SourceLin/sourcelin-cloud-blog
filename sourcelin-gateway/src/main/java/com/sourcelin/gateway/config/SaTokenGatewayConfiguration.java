package com.sourcelin.gateway.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.stp.StpLogic;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.gateway.config.properties.IgnoreWhiteProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 网关配置
 * 
 * 职责：
 * 1. 路由级别登录验证
 * 2. admin/blog 域分发
 * 3. 白名单放行
 * 
 * @author sourcelin
 */
@Configuration
public class SaTokenGatewayConfiguration
{
    private static final StpLogic ADMIN_LOGIC = new StpLogic("admin");
    private static final StpLogic BLOG_LOGIC = new StpLogic("blog");

    private final IgnoreWhiteProperties ignoreWhiteProperties;

    public SaTokenGatewayConfiguration(IgnoreWhiteProperties ignoreWhiteProperties)
    {
        this.ignoreWhiteProperties = ignoreWhiteProperties;
    }

    @Bean
    public SaReactorFilter saReactorFilter()
    {
        return new SaReactorFilter()
            .addInclude("/**")
            .setAuth(obj -> {
                String path = SaHolder.getRequest().getRequestPath();
                String method = SaHolder.getRequest().getMethod();
                if (isWhitePath(path) || isPublicFileRoute(path))
                {
                    return;
                }
                if (isAdminOrBlogFileRoute(path, method))
                {
                    checkAdminOrBlogLogin();
                    return;
                }
                if (isBlogRoute(path))
                {
                    BLOG_LOGIC.checkLogin();
                    return;
                }
                if (isAdminRoute(path, method))
                {
                    ADMIN_LOGIC.checkLogin();
                }
            });
    }

    private boolean isBlogRoute(String path)
    {
        return isPathPrefix(path, "/blog/front");
    }

    private boolean isAdminRoute(String path, String method)
    {
        if (isPublicFileRoute(path) || isAdminOrBlogFileRoute(path, method))
        {
            return false;
        }
        return isPathPrefix(path, "/system")
            || isPathPrefix(path, "/code")
            || isPathPrefix(path, "/schedule")
            || isPathPrefix(path, "/blog/admin")
            || isPathPrefix(path, "/file");
    }

    private boolean isWhitePath(String path)
    {
        return StringUtils.matches(path, ignoreWhiteProperties.getWhites());
    }

    private boolean isPublicFileRoute(String path)
    {
        return isPathPrefix(path, "/file/statics")
            || StringUtils.startsWith(path, "/file/download/");
    }

    /**
     * 文件上传/确认支持 blog 与 admin 双登录态：
     * 1. 博客前台发布说说、上传正文图片可直接调用文件中心
     * 2. 管理后台上传附件保持兼容
     */
    private boolean isAdminOrBlogFileRoute(String path, String method)
    {
        String upperMethod = method == null ? "" : method.toUpperCase();
        boolean isSingleFileIdRoute = path != null && path.matches("^/file/\\d+$");
        return isPathPrefix(path, "/file/upload")
            || isPathPrefix(path, "/file/confirm")
            || (isSingleFileIdRoute && (StringUtils.isEmpty(upperMethod)
                || StringUtils.inStringIgnoreCase(upperMethod, "GET", "DELETE")));
    }

    private void checkAdminOrBlogLogin()
    {
        try
        {
            ADMIN_LOGIC.checkLogin();
            return;
        }
        catch (NotLoginException ignore)
        {
            // fallback to blog realm
        }
        BLOG_LOGIC.checkLogin();
    }

    private boolean isPathPrefix(String path, String prefix)
    {
        return StringUtils.equals(path, prefix) || StringUtils.startsWith(path, prefix + "/");
    }
}
