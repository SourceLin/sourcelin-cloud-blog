package com.sourcelin.blog.service;

import com.sourcelin.blog.domain.BloggerApply;
import java.util.List;

/**
 * 博主申请Service接口
 * 
 * @author sourcelin
 * @date 2026-06-16
 */
public interface IBloggerApplyService {
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
     * 提交申请
     *
     * @param userId 用户id
     * @param reason 申请理由
     * @param contact 联系方式
     * @return 结果
     */
    public int submitApply(Long userId, String reason, String contact);

    /**
     * 审批申请
     *
     * @param id 申请id
     * @param status 状态 (1=已通过, 2=已拒绝)
     * @param opinion 审批意见
     * @param auditBy 审批人
     * @return 结果
     */
    public int auditApply(Long id, Integer status, String opinion, String auditBy);

    /**
     * 获取用户最新申请
     *
     * @param userId 用户id
     * @return 最新博主申请
     */
    public BloggerApply getLatestByUserId(Long userId);
}
