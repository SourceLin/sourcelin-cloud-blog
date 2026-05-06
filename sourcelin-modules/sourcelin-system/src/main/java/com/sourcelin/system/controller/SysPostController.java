package com.sourcelin.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.sourcelin.system.service.ISysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.system.domain.SysPost;

/**
 * 岗位信息操作处理
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/post")
public class SysPostController extends BaseController
{
    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @Autowired
    private ISysPostService postService;

    /**
     * 获取岗位列表
     */
    @SaCheckPermission(type = "admin", value = "system:post:list")
    @GetMapping("/list")
    public PageResult<SysPost> list(SysPost post)
    {
        startPage();
        List<SysPost> list = postService.selectPostList(post);
        return toPageResult(list);
    }

    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission(type = "admin", value = "system:post:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPost post)
    {
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        util.exportExcel(response, list, "岗位数据");
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @SaCheckPermission(type = "admin", value = "system:post:query")
    @GetMapping(value = "/{postId}")
    public SysPost getInfo(@PathVariable Long postId)
    {
        return postService.selectPostById(postId);
    }

    /**
     * 新增岗位
     */
    @SaCheckPermission(type = "admin", value = "system:post:add")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Boolean add(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            throw new BusinessException(ResultCode.CONFLICT, "新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            throw new BusinessException(ResultCode.CONFLICT, "新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreateBy(adminLoginAccessor.getUsername());
        if (postService.insertPost(post) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增岗位失败");
        }
        return true;
    }

    /**
     * 修改岗位
     */
    @SaCheckPermission(type = "admin", value = "system:post:edit")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean edit(@Validated @RequestBody SysPost post)
    {
        if (!postService.checkPostNameUnique(post))
        {
            throw new BusinessException(ResultCode.CONFLICT, "修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (!postService.checkPostCodeUnique(post))
        {
            throw new BusinessException(ResultCode.CONFLICT, "修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setUpdateBy(adminLoginAccessor.getUsername());
        if (postService.updatePost(post) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改岗位失败");
        }
        return true;
    }

    /**
     * 删除岗位
     */
    @SaCheckPermission(type = "admin", value = "system:post:remove")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public Boolean remove(@PathVariable Long[] postIds)
    {
        if (postService.deletePostByIds(postIds) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除岗位失败");
        }
        return true;
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public List<SysPost> optionselect()
    {
        return postService.selectPostAll();
    }
}
