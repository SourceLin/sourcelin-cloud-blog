package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.service.IBlogStatsService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博客统计Controller
 */
@RestController
@RequestMapping("/admin/stats")
public class BlogStatsController
{
    @Autowired
    private IBlogStatsService blogStatsService;

    /**
     * 统计概览
     */
    @SaCheckPermission(type = "admin", value = "blog:stats:view")
    @GetMapping("/summary")
    public Object summary()
    {
        return blogStatsService.getSummary();
    }

    /**
     * 统计趋势
     */
    @SaCheckPermission(type = "admin", value = "blog:stats:view")
    @GetMapping("/trend")
    public Object trend(@RequestParam(value = "days", required = false, defaultValue = "7") Integer days)
    {
        int safeDays = days == null ? 7 : days;
        return blogStatsService.getTrend(safeDays);
    }
}
