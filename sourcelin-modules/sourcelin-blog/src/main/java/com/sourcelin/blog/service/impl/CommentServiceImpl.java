package com.sourcelin.blog.service.impl;

import com.alibaba.fastjson2.JSON;
import com.sourcelin.blog.config.ModerationProperties;
import com.sourcelin.blog.domain.Comment;
import com.sourcelin.blog.domain.CommentModeration;
import com.sourcelin.blog.dto.CommentDTO;
import com.sourcelin.blog.mapper.CommentMapper;
import com.sourcelin.blog.mapper.CommentModerationMapper;
import com.sourcelin.blog.service.ICommentService;
import com.sourcelin.blog.vo.CommentVO;
import com.sourcelin.blog.vo.FrontMyCommentVO;
import com.sourcelin.blog.vo.ModerationOutcome;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论管理Service业务层处理
 * 
 * @author sourcelin
 * @date 2026-01-16
 */
@Service
public class CommentServiceImpl implements ICommentService 
{
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentModerationMapper commentModerationMapper;

    @Autowired
    private ModerationProperties moderationProperties;

    /**
     * 查询文章评论
     * 
     * @param id 文章评论主键
     * @return 文章评论
     */
    @Override
    public CommentVO selectCommentById(Long id)
    {
        return commentMapper.selectCommentById(id);
    }

    /**
     * 查询文章评论列表
     * 
     * @param dto 文章评论
     * @return 文章评论
     */
    @Override
    public List<CommentVO> selectCommentList(CommentDTO dto)
    {
        return commentMapper.selectCommentList(dto);
    }

    @Override
    public List<CommentVO> selectFrontCommentList(CommentDTO dto)
    {
        return commentMapper.selectFrontCommentList(dto);
    }

    @Override
    public List<FrontMyCommentVO> selectMyCommentList(Long userId, String source)
    {
        return commentMapper.selectMyCommentList(userId, source);
    }

    /**
     * 新增文章评论
     * 
     * @param comment 文章评论
     * @return 结果
     */
    @Override
    public int insertComment(Comment comment)
    {
        comment.setCreateTime(DateUtils.getNowDate());
        return commentMapper.insertComment(comment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertCommentWithModeration(Comment comment, ModerationOutcome outcome)
    {
        comment.setStatus(outcome.getStatus());
        int rows = insertComment(comment);
        if (rows <= 0 || !outcome.isPersistModeration())
        {
            return rows;
        }
        CommentModeration m = new CommentModeration();
        m.setCommentId(comment.getId());
        m.setDecision(outcome.getDecision().name());
        m.setLexiconVersion(moderationProperties.getKeyword().getVersion());
        m.setHitKeywords(JSON.toJSONString(outcome.getHitKeywords()));
        m.setCreateTime(DateUtils.getNowDate());
        commentModerationMapper.insertCommentModeration(m);
        return rows;
    }

    /**
     * 修改文章评论
     * 
     * @param comment 文章评论
     * @return 结果
     */
    @Override
    public int updateComment(Comment comment)
    {
        comment.setUpdateTime(DateUtils.getNowDate());
        return commentMapper.updateComment(comment);
    }

    /**
     * 批量删除文章评论
     * 
     * @param ids 需要删除的文章评论主键
     * @return 结果
     */
    @Override
    public int deleteCommentByIds(Long[] ids)
    {
        return commentMapper.deleteCommentByIds(ids);
    }

    /**
     * 删除文章评论信息
     * 
     * @param id 文章评论主键
     * @return 结果
     */
    @Override
    public int deleteCommentById(Long id)
    {
        return commentMapper.deleteCommentById(id);
    }

    @Override
    public int updateLikeCount(Long id)
    {
        return commentMapper.updateLikeCount(id);
    }

    @Override
    public int countByArticleId(Long articleId)
    {
        return commentMapper.countByArticleId(articleId);
    }
}
