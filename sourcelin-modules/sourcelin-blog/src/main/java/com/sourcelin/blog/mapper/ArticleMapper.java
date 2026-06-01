package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.vo.ArticleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 博客文章Mapper接口
 * 
 * @author sourcelin
 * @date 2026-01-09
 */
public interface ArticleMapper 
{
    /**
     * 查询博客文章
     * 
     * @param id 博客文章主键
     * @return 博客文章
     */
    public Article selectArticleById(Long id);

    /**
     * 查询博客文章列表
     * 
     * @param article 博客文章
     * @return 博客文章集合
     */
    public List<Article> selectArticleList(Article article);
    /**
     * 查询博客文章列表
     *
     * @param article 博客文章
     * @return 博客文章集合
     */
    public List<ArticleVO> selectArticleVoList(Article article);

    /**
     * 统计用户文章数量
     *
     * @param userId 用户ID
     * @return 数量
     */
    public int countArticleByUserId(Long userId);

    /**
     * 新增博客文章
     * 
     * @param article 博客文章
     * @return 结果
     */
    public int insertArticle(Article article);

    /**
     * 修改博客文章
     * 
     * @param article 博客文章
     * @return 结果
     */
    public int updateArticle(Article article);

    /**
     * 删除博客文章
     * 
     * @param id 博客文章主键
     * @return 结果
     */
    public int deleteArticleById(Long id);

    /**
     * 批量删除博客文章
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteArticleByIds(Long[] ids);

    /**
     * 增加文章点赞数
     *
     * @param id 文章主键
     * @return 结果
     */
    public int updateLikeCount(Long id);

    /**
     * 减少文章点赞数
     *
     * @param id 文章主键
     * @return 结果
     */
    public int updateLikeCountDecrease(Long id);

    /**
     * 增加文章浏览量
     *
     * @param id 文章主键
     * @return 结果
     */
    public int updateViewCount(Long id);

    /**
     * 增加文章收藏数
     *
     * @param id 文章主键
     * @return 结果
     */
    public int updateCollectCountIncrease(Long id);

    /**
     * 减少文章收藏数
     *
     * @param id 文章主键
     * @return 结果
     */
    public int updateCollectCountDecrease(Long id);

    /**
     * 批量查询文章收藏数（仅返回 id 与 collectCount）
     *
     * @param ids 文章ID列表
     * @return 文章列表
     */
    public List<Article> selectArticleCollectCountsByIds(@Param("ids") List<Long> ids);
}
