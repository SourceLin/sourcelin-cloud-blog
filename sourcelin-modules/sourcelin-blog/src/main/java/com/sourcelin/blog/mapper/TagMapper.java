package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.Tag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 博客标签Mapper接口
 * 
 * @author sourcelin
 * @date 2023-11-07
 */
public interface TagMapper 
{
    /**
     * 查询博客标签
     * 
     * @param id 博客标签主键
     * @return 博客标签
     */
    public Tag selectTagById(Long id);

    /**
     * 查询所有博客标签
     *
     * @return 所有博客标签
     */
    public List<Tag> selectTagAll();

    /**
     * 查询博客标签列表
     * 
     * @param tag 博客标签
     * @return 博客标签集合
     */
    public List<Tag> selectTagList(Tag tag);

    /**
     * 新增博客标签
     * 
     * @param tag 博客标签
     * @return 结果
     */
    public int insertTag(Tag tag);

    /**
     * 修改博客标签
     * 
     * @param tag 博客标签
     * @return 结果
     */
    public int updateTag(Tag tag);

    /**
     * 删除博客标签
     * 
     * @param id 博客标签主键
     * @return 结果
     */
    public int deleteTagById(Long id);

    /**
     * 批量删除博客标签
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTagByIds(Long[] ids);

    /**
     * 添加标签
     * @param articleId
     * @param tagIds
     */
    void saveArticleTags(@Param("articleId") Long articleId, @Param("tagIds") List<Long> tagIds);

    /**
     * 根据id删除文章对应中间表数据
     * @param ids
     */
    void deleteByArticleIds(@Param("ids") List<Long> ids);
}
