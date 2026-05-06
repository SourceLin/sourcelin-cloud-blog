package com.sourcelin.common.core.utils;

import com.github.pagehelper.PageHelper;
import com.sourcelin.common.core.utils.sql.SqlUtil;
import com.sourcelin.common.core.web.page.PageDomain;
import com.sourcelin.common.core.web.page.TableSupport;

/**
 * 分页工具类
 * 
 * @author sourcelin
 */
public class PageUtils extends PageHelper
{
    /** 分页参数下限 */
    private static final int PAGE_MIN = 1;
    /** 每页条数上限 */
    private static final int PAGE_SIZE_MAX = 100;

    /**
     * 将分页参数约束在安全范围内：page >= 1, pageSize ∈ [1, 100]
     */
    public static int clampPage(int page)
    {
        return Math.max(PAGE_MIN, page);
    }

    public static int clampPageSize(int pageSize)
    {
        if (pageSize < 1)
        {
            return 10;
        }
        return Math.min(pageSize, PAGE_SIZE_MAX);
    }
    /**
     * 设置请求分页数据
     */
    public static void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer page = pageDomain.getPage();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(page, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage()
    {
        PageHelper.clearPage();
    }
}
