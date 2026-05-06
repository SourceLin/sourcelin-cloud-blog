package com.sourcelin.blog.service;

import com.sourcelin.blog.vo.BlogStatsSummaryVO;
import com.sourcelin.blog.vo.BlogStatsTrendVO;

/**
 * 博客统计Service接口
 */
public interface IBlogStatsService
{
    /**
     * 统计概览
     *
     * @return 概览数据
     */
    public BlogStatsSummaryVO getSummary();

    /**
     * 统计趋势
     *
     * @param days 天数
     * @return 趋势数据
     */
    public BlogStatsTrendVO getTrend(int days);
}
