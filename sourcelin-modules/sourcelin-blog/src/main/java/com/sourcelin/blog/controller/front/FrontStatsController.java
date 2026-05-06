package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.service.IBlogStatsService;
import com.sourcelin.blog.vo.BlogStatsSummaryVO;
import com.sourcelin.blog.vo.BlogStatsTrendVO;
import com.sourcelin.blog.vo.FrontStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front/stats")
public class FrontStatsController
{
    @Autowired
    private IBlogStatsService blogStatsService;

    @GetMapping
    public FrontStatsVO getStats()
    {
        BlogStatsSummaryVO summary = blogStatsService.getSummary();
        BlogStatsTrendVO trend = blogStatsService.getTrend(14);
        FrontStatsVO result = new FrontStatsVO();
        result.setArticleCount(summary.getArticleCount());
        result.setArticleTrend(calcTrend(trend.getArticleCounts()));
        result.setCommentCount(summary.getCommentCount());
        result.setCommentTrend(calcTrend(trend.getCommentCounts()));
        result.setViewCount(summary.getViewCount());
        result.setViewTrend(0L);
        result.setUserCount(summary.getUserCount());
        result.setUserTrend(calcTrend(trend.getUserCounts()));
        return result;
    }

    private long calcTrend(List<Long> values)
    {
        if (values == null || values.size() < 2)
        {
            return 0L;
        }
        int half = values.size() / 2;
        long previous = 0L;
        long current = 0L;
        for (int i = 0; i < values.size(); i++)
        {
            long value = values.get(i) == null ? 0L : values.get(i);
            if (i < half)
            {
                previous += value;
            }
            else
            {
                current += value;
            }
        }
        if (previous == 0L)
        {
            return current > 0L ? 100L : 0L;
        }
        return Math.round((current - previous) * 100.0D / previous);
    }
}
