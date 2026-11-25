package com.sourcelin.blog.vo;

import lombok.Data;
import java.util.Date;

@Data
public class InboxMessageVO {
    private Long id;
    private String channel;
    private String title;
    private String content;
    private String targetType;
    private Long targetId;
    private Long commentId;
    private Boolean sourceAvailable;
    private Integer isRead;
    private Date publishTime;
}
