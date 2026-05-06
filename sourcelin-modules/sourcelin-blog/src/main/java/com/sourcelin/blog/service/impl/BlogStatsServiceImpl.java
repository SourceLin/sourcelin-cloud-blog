package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.mapper.BlogStatsMapper;
import com.sourcelin.blog.service.IBlogStatsService;
import com.sourcelin.blog.vo.BlogArticleStatusCount;
import com.sourcelin.blog.vo.BlogStatsSummaryVO;
import com.sourcelin.blog.vo.BlogTreeholeStatusCount;
import com.sourcelin.blog.vo.BlogStatsTrendVO;
import com.sourcelin.blog.vo.BlogTrendCount;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 博客统计Service业务层处理
 */
@Service
public class BlogStatsServiceImpl implements IBlogStatsService
{
    private static final int MIN_DAYS = 1;
    private static final int MAX_DAYS = 30;

    @Autowired
    private BlogStatsMapper blogStatsMapper;

    @Override
    public BlogStatsSummaryVO getSummary()
    {
        BlogStatsSummaryVO summary = new BlogStatsSummaryVO();
        summary.setArticleCount(defaultLong(blogStatsMapper.countArticles()));
        summary.setCommentCount(defaultLong(blogStatsMapper.countComments()));
        summary.setUserCount(defaultLong(blogStatsMapper.countUsers()));
        summary.setViewCount(defaultLong(blogStatsMapper.sumArticleViews()));
        fillStructureMetrics(summary);
        fillTreeholeMetrics(summary);
        return summary;
    }

    private void fillStructureMetrics(BlogStatsSummaryVO summary)
    {
        long review = 0L;
        long published = 0L;
        long draft = 0L;
        long other = 0L;
        List<BlogArticleStatusCount> statusRows = blogStatsMapper.countArticleGroupByStatus();
        if (statusRows != null)
        {
            for (BlogArticleStatusCount row : statusRows)
            {
                if (row == null)
                {
                    continue;
                }
                Long s = row.getStatus();
                long c = defaultLong(row.getCnt());
                if (s == null)
                {
                    other += c;
                }
                else if (s == 1L)
                {
                    review = c;
                }
                else if (s == 2L)
                {
                    published = c;
                }
                else if (s == 3L)
                {
                    draft = c;
                }
                else
                {
                    other += c;
                }
            }
        }
        summary.setArticleReviewCount(review);
        summary.setArticlePublishedCount(published);
        summary.setArticleDraftCount(draft);
        summary.setArticleOtherStatusCount(other);
        fillCommentStatusMetrics(summary);
        summary.setFriendLinkPendingCount(defaultLong(blogStatsMapper.countFriendLinkPending()));
    }

    private void fillCommentStatusMetrics(BlogStatsSummaryVO summary)
    {
        long pending = 0L;
        long approved = 0L;
        long rejected = 0L;
        long other = 0L;
        List<BlogArticleStatusCount> rows = blogStatsMapper.countCommentGroupByStatus();
        if (rows != null)
        {
            for (BlogArticleStatusCount row : rows)
            {
                if (row == null)
                {
                    continue;
                }
                Long s = row.getStatus();
                long c = defaultLong(row.getCnt());
                if (s == null)
                {
                    other += c;
                }
                else if (s == 0L)
                {
                    pending = c;
                }
                else if (s == 1L)
                {
                    approved = c;
                }
                else if (s == 2L)
                {
                    rejected = c;
                }
                else
                {
                    other += c;
                }
            }
        }
        summary.setCommentPendingCount(pending);
        summary.setCommentApprovedCount(approved);
        summary.setCommentRejectedCount(rejected);
        summary.setCommentOtherStatusCount(other);
    }

    private void fillTreeholeMetrics(BlogStatsSummaryVO summary)
    {
        summary.setTreeholeCount(defaultLong(blogStatsMapper.countTreeholes()));
        summary.setTreeholeCommentCount(defaultLong(blogStatsMapper.countTreeholeComments()));
        long normal = 0L;
        long pinned = 0L;
        long other = 0L;
        List<BlogTreeholeStatusCount> rows = blogStatsMapper.countTreeholeGroupByStatus();
        if (rows != null)
        {
            for (BlogTreeholeStatusCount row : rows)
            {
                if (row == null)
                {
                    continue;
                }
                Long s = row.getStatus();
                long c = defaultLong(row.getCnt());
                if (s == null)
                {
                    other += c;
                }
                else if (s == 0L)
                {
                    normal = c;
                }
                else if (s == 1L)
                {
                    pinned = c;
                }
                else
                {
                    other += c;
                }
            }
        }
        summary.setTreeholeNormalCount(normal);
        summary.setTreeholePinnedCount(pinned);
        summary.setTreeholeOtherStatusCount(other);
    }

    @Override
    public BlogStatsTrendVO getTrend(int days)
    {
        int safeDays = days <= 0 ? 7 : Math.min(Math.max(days, MIN_DAYS), MAX_DAYS);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(safeDays - 1L);

        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(23, 59, 59);

        Map<String, Long> articleMap = toCountMap(blogStatsMapper.countArticleByDate(DateUtils.toDate(startTime), DateUtils.toDate(endTime)));
        Map<String, Long> commentMap = toCountMap(blogStatsMapper.countCommentByDate(DateUtils.toDate(startTime), DateUtils.toDate(endTime)));
        Map<String, Long> userMap = toCountMap(blogStatsMapper.countUserByDate(DateUtils.toDate(startTime), DateUtils.toDate(endTime)));
        Map<String, Long> treeholeMap = toCountMap(blogStatsMapper.countTreeholeByDate(DateUtils.toDate(startTime), DateUtils.toDate(endTime)));

        List<String> dates = new ArrayList<>(safeDays);
        List<Long> articleCounts = new ArrayList<>(safeDays);
        List<Long> commentCounts = new ArrayList<>(safeDays);
        List<Long> userCounts = new ArrayList<>(safeDays);
        List<Long> treeholeCounts = new ArrayList<>(safeDays);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        for (int i = 0; i < safeDays; i++)
        {
            LocalDate date = startDate.plusDays(i);
            String dateKey = date.format(formatter);
            dates.add(dateKey);
            articleCounts.add(defaultLong(articleMap.get(dateKey)));
            commentCounts.add(defaultLong(commentMap.get(dateKey)));
            userCounts.add(defaultLong(userMap.get(dateKey)));
            treeholeCounts.add(defaultLong(treeholeMap.get(dateKey)));
        }

        BlogStatsTrendVO trend = new BlogStatsTrendVO();
        trend.setDates(dates);
        trend.setArticleCounts(articleCounts);
        trend.setCommentCounts(commentCounts);
        trend.setUserCounts(userCounts);
        trend.setTreeholeCounts(treeholeCounts);
        return trend;
    }

    private Map<String, Long> toCountMap(List<BlogTrendCount> list)
    {
        Map<String, Long> map = new HashMap<>();
        if (list == null)
        {
            return map;
        }
        for (BlogTrendCount item : list)
        {
            if (item != null && item.getDate() != null)
            {
                map.put(item.getDate(), defaultLong(item.getCount()));
            }
        }
        return map;
    }

    private Long defaultLong(Long value)
    {
        return value == null ? 0L : value;
    }
}
