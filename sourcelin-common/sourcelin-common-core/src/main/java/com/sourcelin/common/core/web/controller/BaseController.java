package com.sourcelin.common.core.web.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

import com.sourcelin.common.core.utils.DateUtils;
import com.sourcelin.common.core.utils.PageUtils;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.github.pagehelper.PageInfo;

/**
 * web层通用数据处理
 *
 * @author sourcelin
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageUtils.startPage();
    }

    /**
     * 清理分页的线程变量
     */
    protected void clearPage()
    {
        PageUtils.clearPage();
    }

    /**
     * 响应请求分页数据（新协议）。
     */
    protected <T> PageResult<T> toPageResult(List<T> list)
    {
        PageInfo<T> pageInfo = new PageInfo<T>(list);
        PageDomain pageDomain = TableSupport.getPageDomain();
        return PageResult.of(list, pageInfo.getTotal(), pageDomain.getPage(), pageDomain.getPageSize());
    }
}
