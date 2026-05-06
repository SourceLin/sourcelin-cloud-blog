package com.sourcelin.blog.mapper;

import com.sourcelin.blog.vo.BlogArticleStatusCount;
import com.sourcelin.blog.vo.BlogTreeholeStatusCount;
import com.sourcelin.blog.vo.BlogTrendCount;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 博客统计Mapper接口
 */
public interface BlogStatsMapper
{
    public Long countArticles();

    public Long countComments();

    public Long countUsers();

    public Long sumArticleViews();

    public List<BlogTrendCount> countArticleByDate(@Param("start") Date start, @Param("end") Date end);

    public List<BlogTrendCount> countCommentByDate(@Param("start") Date start, @Param("end") Date end);

    public List<BlogTrendCount> countUserByDate(@Param("start") Date start, @Param("end") Date end);

    public List<BlogArticleStatusCount> countArticleGroupByStatus();

    /** b_comment 按审核状态聚合（0 待审核 1 通过 2 拒绝） */
    public List<BlogArticleStatusCount> countCommentGroupByStatus();

    public Long countFriendLinkPending();

    public Long countTreeholes();

    public Long countTreeholeComments();

    public List<BlogTreeholeStatusCount> countTreeholeGroupByStatus();

    public List<BlogTrendCount> countTreeholeByDate(@Param("start") Date start, @Param("end") Date end);
}
