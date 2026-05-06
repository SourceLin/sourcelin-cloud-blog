package com.sourcelin.blog.vo;

import lombok.Data;

import java.util.List;

/**
 * 博客统计趋势
 */
@Data
public class BlogStatsTrendVO
{
    private List<String> dates;
    private List<Long> articleCounts;
    private List<Long> commentCounts;
    private List<Long> userCounts;
    /** 每日新增树洞条数（b_treehole.create_time） */
    private List<Long> treeholeCounts;
}
