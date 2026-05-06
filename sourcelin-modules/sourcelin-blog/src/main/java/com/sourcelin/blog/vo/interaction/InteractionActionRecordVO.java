package com.sourcelin.blog.vo.interaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 互动行为记录。
 */
@Data
public class InteractionActionRecordVO
{
    private Long id;

    private String actionType;

    private Long userId;

    private String targetType;

    private Long targetId;

    private Boolean active;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
