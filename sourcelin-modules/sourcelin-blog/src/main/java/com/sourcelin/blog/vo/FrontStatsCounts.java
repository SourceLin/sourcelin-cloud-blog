package com.sourcelin.blog.vo;

import lombok.Data;

/** 首页统计总量与两个相邻七天窗口的计数。 */
@Data
public class FrontStatsCounts
{
    private long articleCount;
    private long articlePrevious;
    private long articleCurrent;
    private long commentCount;
    private long commentPrevious;
    private long commentCurrent;
    private long userCount;
    private long userPrevious;
    private long userCurrent;
    private long viewCount;
}
