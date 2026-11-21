package com.sourcelin.gateway.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.stp.StpLogic;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.gateway.config.properties.IgnoreWhiteProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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
    private static final Set<String> PUBLIC_BLOG_GET_PATHS = new HashSet<>(Arrays.asList(
        "/home", "/config", "/config/siteInfo", "/config/about", "/navbars", "/navigation",
        "/stats", "/articles", "/articles/search", "/categories", "/categories/search",
        "/tags", "/tags/search", "/search/hot", "/search/suggestions", "/hot/articles",
        "/hot/categories", "/hot/searches", "/comments", "/says", "/treehole", "/treeholes",
        "/treeholes/barrage", "/links", "/mobile/capabilities", "/dicts", "/interactions/targets/stat"
    ));
    private static final Pattern PUBLIC_BLOG_GET_DETAIL = Pattern.compile(
        "/(?:(?:articles|categories|tags|says|treeholes|users)"
            + "|(?:categories|tags)/articles|users/(?:articles|followers|followings))/[0-9]+");
    private static final Pattern PUBLIC_BLOG_POST_PATH = Pattern.compile(
        "/(?:analytics/events|articles/view/[0-9]+|navigation/[0-9]+/click)");

    private final IgnoreWhiteProperties ignoreWhiteProperties;

    public SaTokenGatewayConfiguration(IgnoreWhiteProperties ignoreWhiteProperties)
    {
        this.ignoreWhiteProperties = ignoreWhiteProperties;
    }

    @Bean
    public cn.dev33.satoken.config.SaTokenConfig saTokenConfig() {
        cn.dev33.satoken.config.SaTokenConfig config = new cn.dev33.satoken.config.SaTokenConfig();
        config.setTokenName("Authorization"); config.setTokenPrefix("Bearer");
        config.setIsReadHeader(true); config.setIsReadCookie(false); return config;
    }

    @Bean
    public SaReactorFilter saReactorFilter()
    {
        return new SaReactorFilter()
            .addInclude("/**")
            .setAuth(obj -> {
                String path = SaHolder.getRequest().getRequestPath();
                String method = SaHolder.getRequest().getMethod();
                if (isWhitePath(path) || isPublicBlogRoute(path, method) || isPublicFileRoute(path))
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
            })
            .setError(error -> {
                throw error instanceof RuntimeException ? (RuntimeException) error : new IllegalStateException(error);
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
        return java.util.Arrays.asList("/message/stream", "/blog/front/ai/status", "/blog/front/messages/realtime-status", "/blog/front/ai/abilities/ai_agent_chat/invoke/stream").contains(path)
            || StringUtils.matches(path, ignoreWhiteProperties.getWhites());
    }

    private boolean isPublicBlogRoute(String path, String method)
    {
        String prefix = "/blog/front";
        if (!path.startsWith(prefix + "/"))
        {
            return false;
        }
        String endpoint = path.substring(prefix.length());
        if ("GET".equals(method))
        {
            return PUBLIC_BLOG_GET_PATHS.contains(endpoint) || PUBLIC_BLOG_GET_DETAIL.matcher(endpoint).matches();
        }
        return "POST".equals(method) && PUBLIC_BLOG_POST_PATH.matcher(endpoint).matches();
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
