package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.Comment;
import com.sourcelin.blog.dto.CommentDTO;
import com.sourcelin.blog.vo.CommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论管理Mapper接口
 * 
 * @author sourcelin
 * @date 2026-01-16
 */
public interface CommentMapper 
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
     * 前台评论列表（合并本人 0/2）
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
     * 修改文章评论
     * 
     * @param comment 文章评论
     * @return 结果
     */
    public int updateComment(Comment comment);

    /**
     * 删除文章评论
     * 
     * @param id 文章评论主键
     * @return 结果
     */
    public int deleteCommentById(Long id);

    /**
     * 批量删除文章评论
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCommentByIds(Long[] ids);

    /**
     * 增加评论点赞数
     *
     * @param id 评论主键
     * @return 结果
     */
    public int updateLikeCount(Long id);

    /**
     * 统计某用户在某文章下的有效评论数（未删除）
     */
    int countByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);

    int countByArticleId(@Param("articleId") Long articleId);
}
