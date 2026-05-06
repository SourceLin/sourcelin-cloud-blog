package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.shared.support.BlogPageResults;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.blog.domain.Navigation;
import com.sourcelin.blog.service.INavigationService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/admin/navigation")
public class NavigationController
{
    @Autowired
    private INavigationService navigationService;

    @SaCheckPermission(type = "admin", value = "blog:navigation:list")
    @GetMapping("/list")
    public PageResult<Navigation> list(Navigation navigation)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        PageHelper.startPage(pageDomain.getPage(), pageDomain.getPageSize());
        List<Navigation> list = navigationService.selectNavigationList(navigation);
        PageInfo<Navigation> pageInfo = new PageInfo<Navigation>(list);
        return BlogPageResults.of(list, pageInfo);
    }

    @SaCheckPermission(type = "admin", value = "blog:navigation:query")
    @GetMapping("/{id}")
    public Navigation getInfo(@PathVariable("id") Long id)
    {
        Navigation navigation = navigationService.selectNavigationById(id);
        if (navigation == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "导航目录不存在");
        }
        return navigation;
    }

    @SaCheckPermission(type = "admin", value = "blog:navigation:add")
    @Log(title = "导航目录", businessType = BusinessType.INSERT)
    @PostMapping
    public Void add(@RequestBody Navigation navigation)
    {
        // 导航名称和链接为必填字段，防止 Jackson 反序列化丢失导致 SQL 报错
        if (navigation.getName() == null || navigation.getName().isEmpty())
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "导航名称不能为空");
        }
        if (navigation.getUrl() == null || navigation.getUrl().isEmpty())
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "导航链接不能为空");
        }
        // category 为数据库 NOT NULL 字段，无默认值时需显式设置
        if (navigation.getCategory() == null || navigation.getCategory().isEmpty())
        {
            navigation.setCategory("默认");
        }
        if (navigationService.insertNavigation(navigation) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "新增导航目录失败");
        }
        return null;
    }

    @SaCheckPermission(type = "admin", value = "blog:navigation:edit")
    @Log(title = "导航目录", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody Navigation navigation)
    {
        if (navigationService.updateNavigation(navigation) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改导航目录失败");
        }
        return null;
    }

    @SaCheckPermission(type = "admin", value = "blog:navigation:remove")
    @Log(title = "导航目录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public Void remove(@PathVariable Long[] ids)
    {
        if (navigationService.deleteNavigationByIds(ids) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "删除导航目录失败");
        }
        return null;
    }

    @SaCheckPermission(type = "admin", value = "blog:navigation:export")
    @Log(title = "导航目录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Navigation navigation)
    {
        List<Navigation> list = navigationService.selectNavigationList(navigation);
        ExcelUtil<Navigation> util = new ExcelUtil<>(Navigation.class);
        util.exportExcel(response, list, "导航目录");
    }
}
