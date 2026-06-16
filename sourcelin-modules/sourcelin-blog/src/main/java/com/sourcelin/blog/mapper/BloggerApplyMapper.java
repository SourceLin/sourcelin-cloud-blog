package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.BloggerApply;
import java.util.List;

/**
 * 博主申请Mapper接口
 * 
 * @author sourcelin
 * @date 2026-06-16
 */
public interface BloggerApplyMapper {
    /**
     * 查询博主申请
     * 
     * @param id 博主申请主键
     * @return 博主申请
     */
    public BloggerApply selectBloggerApplyById(Long id);

    /**
     * 查询博主申请列表
     * 
     * @param bloggerApply 博主申请
     * @return 博主申请集合
     */
    public List<BloggerApply> selectBloggerApplyList(BloggerApply bloggerApply);

    /**
     * 新增博主申请
     * 
     * @param bloggerApply 博主申请
     * @return 结果
     */
    public int insertBloggerApply(BloggerApply bloggerApply);

    /**
     * 修改博主申请
     * 
     * @param bloggerApply 博主申请
     * @return 结果
     */
    public int updateBloggerApply(BloggerApply bloggerApply);

    /**
     * 查询用户最新的申请记录
     * 
     * @param userId 用户id
     * @return 博主申请
     */
    public BloggerApply selectLatestByUserId(Long userId);

    /**
     * 查询用户待审核的申请数量
     * 
     * @param userId 用户id
     * @return 数量
     */
    public int countPendingByUserId(Long userId);
}
