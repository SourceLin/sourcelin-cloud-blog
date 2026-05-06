package com.sourcelin.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.sourcelin.blog.domain.Article;
import com.sourcelin.blog.dto.ArticleInsertDTO;
import com.sourcelin.blog.mapper.ArticleMapper;
import com.sourcelin.blog.mapper.TagMapper;
import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.support.BlogFileCenterSupport;
import com.sourcelin.blog.support.BlogRichTextSanitizer;
import com.sourcelin.blog.vo.ArticleVO;
import com.sourcelin.common.core.utils.bean.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 博客文章Service业务层处理
 *
 * @author sourcelin
 * @date 2023-11-09
 */
@Service
public class ArticleServiceImpl implements IArticleService
{
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private BlogRichTextSanitizer blogRichTextSanitizer;
    @Autowired
    private BlogFileCenterSupport blogFileCenterSupport;

    /**
     * 查询博客文章
     *
     * @param id 博客文章主键
     * @return 博客文章
     */
    @Override
    public Article selectArticleById(Long id)
    {
        return sanitizeArticle(articleMapper.selectArticleById(id));
    }

    /**
     * 查询博客文章列表
     *
     * @param article 博客文章
     * @return 博客文章
     */
    @Override
    public List<Article> selectArticleList(Article article)
    {
        return sanitizeArticles(articleMapper.selectArticleList(article));
    }

    /**
     * 查询博客文章列表
     *
     * @param article 博客文章
     * @return 博客文章
     */
    @Override
    public List<ArticleVO> selectArticleVoList(Article article)
    {
        return sanitizeArticleVos(articleMapper.selectArticleVoList(article));
    }

    @Override
    public int countArticleByUserId(Long userId)
    {
        return articleMapper.countArticleByUserId(userId);
    }

    /**
     * 新增博客文章
     *
     * @param dto 博客文章DTO
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertArticle(ArticleInsertDTO dto)
    {
        blogRichTextSanitizer.sanitizeArticleInput(dto);
        Article article = new Article();
        BeanUtils.copyProperties(dto , article);
        int insert = articleMapper.insertArticle(article);
        if (insert > 0)
        {
            dto.setId(article.getId());
        }
        // 添加标签
        if (insert > 0 && CollUtil.isNotEmpty(dto.getTagIds()))
        {
            tagMapper.saveArticleTags(article.getId(), dto.getTagIds());
        }
        if (insert > 0)
        {
            confirmArticleFiles(article.getId(), dto);
        }
        return insert;
    }

    /**
     * 修改博客文章
     * <p>当 {@code tagIds != null} 时同步标签：空集合表示清空全部标签。</p>
     *
     * @param dto 博客文章
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateArticle(ArticleInsertDTO dto)
    {
        blogRichTextSanitizer.sanitizeArticleInput(dto);
        Article article = new Article();
        BeanUtils.copyProperties(dto , article);
        int update = articleMapper.updateArticle(article);
        if (update > 0 && dto.getTagIds() != null)
        {
            tagMapper.deleteByArticleIds(Collections.singletonList(article.getId()));
            if (CollUtil.isNotEmpty(dto.getTagIds()))
            {
                tagMapper.saveArticleTags(article.getId(), dto.getTagIds());
            }
        }
        if (update > 0)
        {
            confirmArticleFiles(article.getId(), dto);
        }
        return update;
    }

    /**
     * 批量删除博客文章
     *
     * @param ids 需要删除的博客文章主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteArticleByIds(Long[] ids)
    {
        if (ids != null && ids.length > 0)
        {
            tagMapper.deleteByArticleIds(Arrays.asList(ids));
        }
        return articleMapper.deleteArticleByIds(ids);
    }

    /**
     * 删除博客文章信息
     *
     * @param id 博客文章主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteArticleById(Long id)
    {
        tagMapper.deleteByArticleIds(Collections.singletonList(id));
        return articleMapper.deleteArticleById(id);
    }

    /**
     * 增加文章点赞数
     *
     * @param id 文章主键
     * @return 结果
     */
    @Override
    public int updateLikeCount(Long id)
    {
        return articleMapper.updateLikeCount(id);
    }

    @Override
    public int updateLikeCountDecrease(Long id)
    {
        return articleMapper.updateLikeCountDecrease(id);
    }

    /**
     * 增加文章浏览量
     *
     * @param id 文章主键
     * @return 结果
     */
    @Override
    public int updateViewCount(Long id)
    {
        return articleMapper.updateViewCount(id);
    }

    @Override
    public int updateCollectCountIncrease(Long id)
    {
        return articleMapper.updateCollectCountIncrease(id);
    }

    @Override
    public int updateCollectCountDecrease(Long id)
    {
        return articleMapper.updateCollectCountDecrease(id);
    }

    private Article sanitizeArticle(Article article)
    {
        if (article == null)
        {
            return null;
        }
        article.setContent(blogRichTextSanitizer.sanitizeContent(article.getContent()));
        article.setOriginalUrl(blogRichTextSanitizer.sanitizeExternalUrl(article.getOriginalUrl()));
        return article;
    }

    private List<Article> sanitizeArticles(List<Article> articles)
    {
        if (articles == null)
        {
            return null;
        }
        for (Article article : articles)
        {
            sanitizeArticle(article);
        }
        return articles;
    }

    private List<ArticleVO> sanitizeArticleVos(List<ArticleVO> articleVos)
    {
        if (articleVos == null)
        {
            return null;
        }
        for (ArticleVO articleVo : articleVos)
        {
            articleVo.setContent(blogRichTextSanitizer.sanitizeContent(articleVo.getContent()));
            articleVo.setOriginalUrl(blogRichTextSanitizer.sanitizeExternalUrl(articleVo.getOriginalUrl()));
        }
        return articleVos;
    }

    private void confirmArticleFiles(Long articleId, ArticleInsertDTO dto)
    {
        if (articleId == null || dto == null)
        {
            return;
        }
        String bizId = String.valueOf(articleId);
        Set<Long> fileIds = new LinkedHashSet<>();
        if (dto.getAvatarFileId() != null && dto.getAvatarFileId() > 0)
        {
            fileIds.add(dto.getAvatarFileId());
        }
        fileIds.addAll(blogRichTextSanitizer.collectFileIds(dto.getContent()));
        for (Long fileId : fileIds)
        {
            blogFileCenterSupport.confirmFile(fileId, "article", bizId);
        }
    }
}
