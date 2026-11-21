package com.sourcelin.ai.domain;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class AiTaskEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String taskId;
    private String eventType;
    private String eventMessage;
    private String eventPayload;
    private Date createTime;
}
