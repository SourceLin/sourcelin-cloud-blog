package com.sourcelin.common.core.web.page;

import com.sourcelin.common.core.text.Convert;
import com.sourcelin.common.core.utils.ServletUtils;

/**
 * 表格数据处理
 * 
 * @author sourcelin
 */
public class TableSupport
{
    /** 统一协议页码参数。 */
    public static final String PAGE = "page";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 统一协议排序字段。
     */
    public static final String SORT_BY = "sortBy";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 统一协议排序方向。
     */
    public static final String SORT_ORDER = "sortOrder";

    /**
     * 分页参数合理化
     */
    public static final String REASONABLE = "reasonable";

    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain()
    {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPage(Convert.toInt(ServletUtils.getParameter(PAGE), 1));
        pageDomain.setPageSize(Convert.toInt(ServletUtils.getParameter(PAGE_SIZE), 10));
        pageDomain.setOrderByColumn(resolveSortBy());
        pageDomain.setIsAsc(resolveSortOrder());
        pageDomain.setReasonable(ServletUtils.getParameterToBool(REASONABLE));
        return pageDomain;
    }

    public static PageDomain buildPageRequest()
    {
        return getPageDomain();
    }

    private static String resolveSortBy()
    {
        String sortBy = ServletUtils.getParameter(SORT_BY);
        return sortBy == null ? ServletUtils.getParameter(ORDER_BY_COLUMN) : sortBy;
    }

    private static String resolveSortOrder()
    {
        String sortOrder = ServletUtils.getParameter(SORT_ORDER);
        return sortOrder == null ? ServletUtils.getParameter(IS_ASC) : sortOrder;
    }
}
