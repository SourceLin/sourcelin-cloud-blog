package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.shared.support.BlogPageResults;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.Say;
import com.sourcelin.blog.service.ISayService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 说说管理
 */
@RestController
@RequestMapping("/admin/say")
public class SayController
{
    @Autowired
    private ISayService sayService;

    @SaCheckPermission(type = "admin", value = "blog:say:list")
    @GetMapping("/list")
    public PageResult<Say> list(Say say)
    {
        // 后台管理列表默认不包含已删除记录（如需查看已删除记录，可单独提供筛选选项）
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize(), pageDomain.getOrderBy());
        List<Say> list = sayService.selectSayList(say);
        PageInfo<Say> pageInfo = new PageInfo<Say>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    @SaCheckPermission(type = "admin", value = "blog:say:query")
    @GetMapping(value = "/{id}")
    public Say getInfo(@PathVariable("id") Long id)
    {
        Say say = sayService.selectSayById(id);
        if (say == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "说说不存在");
        }
        return say;
    }

    @SaCheckPermission(type = "admin", value = "blog:say:add")
    @Log(title = "说说", businessType = BusinessType.INSERT)
    @PostMapping
    public Void add(@RequestBody Say say)
    {
        if (say.getDeleted() == null)
        {
            say.setDeleted(0L);
        }
        if (say.getStatus() == null)
        {
            say.setStatus(0L);
        }
        if (sayService.insertSay(say) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增失败");
        }
        return null;
    }

    @SaCheckPermission(type = "admin", value = "blog:say:edit")
    @Log(title = "说说", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody Say say)
    {
        if (sayService.updateSay(say) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改失败");
        }
        return null;
    }

    @SaCheckPermission(type = "admin", value = "blog:say:remove")
    @Log(title = "说说", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (sayService.deleteSayByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除失败");
        }
        return null;
    }
}
