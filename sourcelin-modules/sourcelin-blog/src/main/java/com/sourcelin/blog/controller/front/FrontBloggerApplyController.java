package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.BloggerApply;
import com.sourcelin.blog.service.IBloggerApplyService;
import com.sourcelin.blog.vo.BloggerApplyRequest;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 前台博主申请Controller
 *
 * @author sourcelin
 * @date 2026-06-16
 */
@RestController
@RequestMapping("/front/blogger")
public class FrontBloggerApplyController extends BaseController {

    @Autowired
    private IBloggerApplyService bloggerApplyService;

    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    /**
     * 提交申请成为博主
     */
    @PostMapping("/apply")
    public Void submitApply(@RequestBody BloggerApplyRequest request) {
        if (!blogLoginAccessor.isLogin()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }

        if (request == null || request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "申请理由不能为空");
        }

        Long userId = blogLoginAccessor.getCurrentUserId();
        bloggerApplyService.submitApply(userId, request.getReason(), request.getContact());
        return null;
    }

    /**
     * 获取当前用户最新的博主申请状态
     */
    @GetMapping("/apply/status")
    public BloggerApply getLatestApplyStatus() {
        if (!blogLoginAccessor.isLogin()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }

        Long userId = blogLoginAccessor.getCurrentUserId();
        return bloggerApplyService.getLatestByUserId(userId);
    }
}
