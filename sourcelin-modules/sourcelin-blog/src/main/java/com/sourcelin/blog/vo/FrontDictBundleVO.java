package com.sourcelin.blog.vo;

import java.util.List;

/**
 * 前台字典聚合分组
 */
public class FrontDictBundleVO
{
    private String dictType;
    private List<FrontDictItemVO> items;

    public String getDictType()
    {
        return dictType;
    }

    public void setDictType(String dictType)
    {
        this.dictType = dictType;
    }

    public List<FrontDictItemVO> getItems()
    {
        return items;
    }

    public void setItems(List<FrontDictItemVO> items)
    {
        this.items = items;
    }
}

