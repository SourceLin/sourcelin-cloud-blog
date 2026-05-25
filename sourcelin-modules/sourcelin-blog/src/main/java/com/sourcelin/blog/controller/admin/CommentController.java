package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.shared.support.BlogPageResults;

import com.sourcelin.blog.domain.Comment;
import com.sourcelin.blog.dto.CommentDTO;
import com.sourcelin.blog.service.ICommentService;
import com.sourcelin.blog.vo.CommentVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论管理Controller
 * 
 * @author sourcelin
 * @date 2026-01-16
 */
@RestController
@RequestMapping("/admin/comment")
public class CommentController
{
    @Autowired
    private ICommentService commentService;

    /**
     * 查询文章评论列表
     */
    @SaCheckPermission(type = "admin", value = "blog:comment:list")
    @GetMapping("/list")
    public PageResult<CommentVO> list(CommentDTO dto)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize());
        List<CommentVO> list = commentService.selectCommentList(dto);
        PageInfo<CommentVO> pageInfo = new PageInfo<CommentVO>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    /**
     * 获取文章评论详细信息
     */
    @SaCheckPermission(type = "admin", value = "blog:comment:query")
    @GetMapping(value = "/{id}")
    public CommentVO getInfo(@PathVariable("id") Long id)
    {
        CommentVO comment = commentService.selectCommentById(id);
        if (comment == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "评论不存在");
        }
        return comment;
    }

    /**
     * 新增文章评论
     */
    @SaCheckPermission(type = "admin", value = "blog:comment:add")
    @Log(title = "文章评论", businessType = BusinessType.INSERT)
    @PostMapping
    public Void add(@RequestBody Comment comment)
    {
        if (commentService.insertComment(comment) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "新增评论失败");
        }
        return null;
    }

    /**
     * 修改文章评论
     */
    @SaCheckPermission(type = "admin", value = "blog:comment:edit")
    @Log(title = "文章评论", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody Comment comment)
    {
        if (commentService.updateComment(comment) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改评论失败");
        }
        return null;
    }

    /**
     * 删除文章评论
     */
    @SaCheckPermission(type = "admin", value = "blog:comment:remove")
    @Log(title = "文章评论", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (commentService.deleteCommentByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "删除评论失败");
        }
        return null;
    }
}
