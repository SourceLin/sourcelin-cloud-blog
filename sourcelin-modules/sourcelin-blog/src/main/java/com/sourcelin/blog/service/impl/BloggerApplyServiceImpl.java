package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.api.domain.User;
import com.sourcelin.blog.constant.UserTypeEnum;
import com.sourcelin.blog.domain.BloggerApply;
import com.sourcelin.blog.mapper.BloggerApplyMapper;
import com.sourcelin.blog.service.IBloggerApplyService;
import com.sourcelin.blog.service.IUserService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 博主申请Service业务层处理
 * 
 * @author sourcelin
 * @date 2026-06-16
 */
@Service
public class BloggerApplyServiceImpl implements IBloggerApplyService {

    @Autowired
    private BloggerApplyMapper bloggerApplyMapper;

    @Autowired
    private IUserService userService;

    /**
     * 查询博主申请
     * 
     * @param id 博主申请主键
     * @return 博主申请
     */
    @Override
    public BloggerApply selectBloggerApplyById(Long id) {
        return bloggerApplyMapper.selectBloggerApplyById(id);
    }

    /**
     * 查询博主申请列表
     * 
     * @param bloggerApply 博主申请
     * @return 博主申请
     */
    @Override
    public List<BloggerApply> selectBloggerApplyList(BloggerApply bloggerApply) {
        return bloggerApplyMapper.selectBloggerApplyList(bloggerApply);
    }

    /**
     * 提交申请
     *
     * @param userId 用户id
     * @param reason 申请理由
     * @param contact 联系方式
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitApply(Long userId, String reason, String contact) {
        // 1. 校验用户是否存在
        User user = userService.selectUserById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 2. 校验用户是否已是博主
        if (user.getUserType() != null && user.getUserType() == UserTypeEnum.BLOGGER.getCode()) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "您已经是博主，无需申请");
        }

        // 3. 校验是否存在待审核的申请
        int pendingCount = bloggerApplyMapper.countPendingByUserId(userId);
        if (pendingCount > 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "您已有待审核的博主申请，请勿重复提交");
        }

        // 4. 新增申请记录
        BloggerApply apply = new BloggerApply();
        apply.setUserId(userId);
        apply.setReason(reason);
        apply.setContact(contact);
        apply.setStatus(0); // 待审核
        apply.setDeleted(0);
        apply.setCreateTime(DateUtils.getNowDate());
        apply.setUpdateTime(DateUtils.getNowDate());
        apply.setCreateBy(user.getUsername());
        apply.setUpdateBy(user.getUsername());

        return bloggerApplyMapper.insertBloggerApply(apply);
    }

    /**
     * 审批申请
     *
     * @param id 申请id
     * @param status 状态 (1=已通过, 2=已拒绝)
     * @param opinion 审批意见
     * @param auditBy 审批人
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int auditApply(Long id, Integer status, String opinion, String auditBy) {
        // 1. 获取申请记录
        BloggerApply apply = bloggerApplyMapper.selectBloggerApplyById(id);
        if (apply == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "申请记录不存在");
        }

        // 2. 校验状态是否已处理
        if (apply.getStatus() != null && apply.getStatus() != 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "该申请已处理，请勿重复操作");
        }

        // 3. 校验目标状态合法性
        if (status == null || (status != 1 && status != 2)) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "审核状态参数不合法");
        }

        // 4. 更新申请记录状态
        apply.setStatus(status);
        apply.setAuditOpinion(opinion);
        apply.setAuditTime(DateUtils.getNowDate());
        apply.setAuditBy(auditBy);
        apply.setUpdateTime(DateUtils.getNowDate());
        apply.setUpdateBy(auditBy);

        int rows = bloggerApplyMapper.updateBloggerApply(apply);

        // 5. 若通过，修改用户类型为博主
        if (rows > 0 && status == 1) {
            User user = userService.selectUserById(apply.getUserId());
            if (user == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "申请关联的用户已不存在");
            }
            user.setUserType(UserTypeEnum.BLOGGER.getCode());
            user.setUpdateTime(DateUtils.getNowDate());
            user.setUpdateBy(auditBy);
            userService.updateUser(user);
        }

        return rows;
    }

    /**
     * 获取用户最新申请
     *
     * @param userId 用户id
     * @return 最新博主申请
     */
    @Override
    public BloggerApply getLatestByUserId(Long userId) {
        return bloggerApplyMapper.selectLatestByUserId(userId);
    }
}
