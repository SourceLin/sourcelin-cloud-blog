package com.sourcelin.blog.vo;

/**
 * 前台字典项（对齐前台 canonical 契约字段）
 */
public class FrontDictItemVO
{
    private String value;
    private String label;
    private String tagType;
    private Integer sort;
    private Boolean enabled;

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getTagType()
    {
        return tagType;
    }

    public void setTagType(String tagType)
    {
        this.tagType = tagType;
    }

    public Integer getSort()
    {
        return sort;
    }

    public void setSort(Integer sort)
    {
        this.sort = sort;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }
}

