package com.sourcelin.file.api.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 文件信息
 * 
 * @author sourcelin
 */
public class SysFile
{
    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 文件地址
     */
    private String url;

    /**
     * 文件大小
     */
    private Long size;

    /**
     * 文件内容类型
     */
    private String contentType;

    public Long getFileId()
    {
        return fileId;
    }

    public void setFileId(Long fileId)
    {
        this.fileId = fileId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getOriginalName()
    {
        return originalName;
    }

    public void setOriginalName(String originalName)
    {
        this.originalName = originalName;
    }

    public Long getSize()
    {
        return size;
    }

    public void setSize(Long size)
    {
        this.size = size;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("fileId", getFileId())
            .append("name", getName())
            .append("originalName", getOriginalName())
            .append("url", getUrl())
            .append("size", getSize())
            .append("contentType", getContentType())
            .toString();
    }
}
