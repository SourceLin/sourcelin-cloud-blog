package com.sourcelin.blog.controller.admin;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.BloggerApply;
import com.sourcelin.blog.service.IBloggerApplyService;
import com.sourcelin.blog.shared.support.BlogPageResults;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台博主申请管理Controller
 *
 * @author sourcelin
 * @date 2026-06-16
 */
@RestController
@RequestMapping("/admin/blogger/apply")
public class BloggerApplyController extends BaseController {

    @Autowired
    private IBloggerApplyService bloggerApplyService;

    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    /**
     * 查询博主申请列表
     */
    @SaCheckPermission(type = "admin", value = "blog:blogger:list")
    @GetMapping("/list")
    public PageResult<BloggerApply> list(BloggerApply bloggerApply) {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize());
        List<BloggerApply> list = bloggerApplyService.selectBloggerApplyList(bloggerApply);
        PageInfo<BloggerApply> pageInfo = new PageInfo<>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    /**
     * 获取博主申请详细信息
     */
    @SaCheckPermission(type = "admin", value = "blog:blogger:query")
    @GetMapping("/{id}")
    public BloggerApply getInfo(@PathVariable("id") Long id) {
        BloggerApply bloggerApply = bloggerApplyService.selectBloggerApplyById(id);
        if (bloggerApply == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "申请记录不存在");
        }
        return bloggerApply;
    }

    /**
     * 审核博主申请
     */
    @SaCheckPermission(type = "admin", value = "blog:blogger:audit")
    @Log(title = "博主申请审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public Void audit(@RequestBody BloggerApply bloggerApply) {
        if (bloggerApply.getId() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "申请记录ID不能为空");
        }
        if (bloggerApply.getStatus() == null) {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "审核状态不能为空");
        }

        String auditBy = adminLoginAccessor.getUsername();
        bloggerApplyService.auditApply(
                bloggerApply.getId(), 
                bloggerApply.getStatus(), 
                bloggerApply.getAuditOpinion(), 
                auditBy
        );
        return null;
    }
}
