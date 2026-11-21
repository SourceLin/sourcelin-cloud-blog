package com.sourcelin.ai.api.domain;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class AiTaskStatusVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String taskId;
    private String requestId;
    private String abilityCode;
    private String bizId;
    private String userId;
    private String status;
    private Integer progress;
    private String resultSnapshot;
    private String errorMessage;
    private Date startedTime;
    private Date finishedTime;
}
