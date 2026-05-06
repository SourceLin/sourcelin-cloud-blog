package com.sourcelin.blog.service;

import com.sourcelin.blog.domain.Comment;
import com.sourcelin.blog.dto.CommentDTO;
import com.sourcelin.blog.vo.CommentVO;
import com.sourcelin.blog.vo.ModerationOutcome;

import java.util.List;

/**
 * 评论管理Service接口
 * 
 * @author sourcelin
 * @date 2023-11-16
 */
public interface ICommentService 
{
    /**
     * 查询文章评论
     * 
     * @param id 文章评论主键
     * @return 文章评论
     */
    CommentVO selectCommentById(Long id);

    /**
     * 查询文章评论列表
     * 
     * @param dto 文章评论
     * @return 文章评论集合
     */
    List<CommentVO> selectCommentList(CommentDTO dto);

    /**
     * 前台评论列表（含本人待审/拒绝合并）
     */
    List<CommentVO> selectFrontCommentList(CommentDTO dto);

    /**
     * 新增文章评论
     * 
     * @param comment 文章评论
     * @return 结果
     */
    public int insertComment(Comment comment);

    /**
     * 插入评论并写机审记录（同事务）
     */
    int insertCommentWithModeration(Comment comment, ModerationOutcome outcome);

    /**
     * 修改文章评论
     * 
     * @param comment 文章评论
     * @return 结果
     */
    public int updateComment(Comment comment);

    /**
     * 批量删除文章评论
     * 
     * @param ids 需要删除的文章评论主键集合
     * @return 结果
     */
    public int deleteCommentByIds(Long[] ids);

    /**
     * 删除文章评论信息
     * 
     * @param id 文章评论主键
     * @return 结果
     */
    public int deleteCommentById(Long id);

    /**
     * 增加评论点赞数
     *
     * @param id 评论主键
     * @return 结果
     */
    public int updateLikeCount(Long id);

    int countByArticleId(Long articleId);
}
