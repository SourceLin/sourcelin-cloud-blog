package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.shared.support.BlogPageResults;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sourcelin.blog.domain.Collect;
import com.sourcelin.blog.service.ICollectService;
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

@RestController
@RequestMapping("/admin/collect")
public class CollectController
{
    @Autowired
    private ICollectService collectService;

    @SaCheckPermission(type = "admin", value = "blog:collect:list")
    @GetMapping("/list")
    public PageResult<Collect> list(Collect collect)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize(), pageDomain.getOrderBy());
        List<Collect> list = collectService.selectCollectListWithArticle(collect);
        PageInfo<Collect> pageInfo = new PageInfo<Collect>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    @SaCheckPermission(type = "admin", value = "blog:collect:query")
    @GetMapping("/{id}")
    public Collect getInfo(@PathVariable("id") Long id)
    {
        Collect collect = collectService.selectCollectById(id);
        if (collect == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "收藏不存在");
        }
        return collect;
    }

    @SaCheckPermission(type = "admin", value = "blog:collect:remove")
    @Log(title = "收藏管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (collectService.deleteCollectByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除失败");
        }
        return null;
    }
}
