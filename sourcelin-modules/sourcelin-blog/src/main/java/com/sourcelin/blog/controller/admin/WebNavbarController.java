package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.domain.WebNavbar;
import com.sourcelin.blog.service.IWebNavbarService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.ListResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 门户导航栏Controller
 * 
 * @author sourcelin
 * @date 2026-01-06
 */
@RestController
@RequestMapping("/admin/navbar")
public class WebNavbarController
{
    @Autowired
    private IWebNavbarService webNavbarService;

    /**
     * 查询门户导航栏列表
     */
    @SaCheckPermission(type = "admin", value = "blog:navbar:list")
    @GetMapping("/list")
    public ListResult<WebNavbar> list(WebNavbar webNavbar)
    {
        List<WebNavbar> list = webNavbarService.selectWebNavbarList(webNavbar);
        return ListResult.<WebNavbar>builder().items(list).build();
    }

    /**
     * 获取门户导航栏详细信息
     */
    @SaCheckPermission(type = "admin", value = "blog:navbar:query")
    @GetMapping(value = "/{id}")
    public WebNavbar getInfo(@PathVariable("id") Long id)
    {
        WebNavbar webNavbar = webNavbarService.selectWebNavbarById(id);
        if (webNavbar == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "导航栏不存在");
        }
        return webNavbar;
    }

    /**
     * 新增门户导航栏
     */
    @SaCheckPermission(type = "admin", value = "blog:navbar:add")
    @Log(title = "门户导航栏", businessType = BusinessType.INSERT)
    @PostMapping
    public Void add(@RequestBody WebNavbar webNavbar)
    {
        if (webNavbarService.insertWebNavbar(webNavbar) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "新增导航栏失败");
        }
        return null;
    }

    /**
     * 修改门户导航栏
     */
    @SaCheckPermission(type = "admin", value = "blog:navbar:edit")
    @Log(title = "门户导航栏", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody WebNavbar webNavbar)
    {
        if (webNavbarService.updateWebNavbar(webNavbar) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改导航栏失败");
        }
        return null;
    }

    /**
     * 删除门户导航栏
     */
    @SaCheckPermission(type = "admin", value = "blog:navbar:remove")
    @Log(title = "门户导航栏", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (webNavbarService.deleteWebNavbarByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "删除导航栏失败");
        }
        return null;
    }
}
