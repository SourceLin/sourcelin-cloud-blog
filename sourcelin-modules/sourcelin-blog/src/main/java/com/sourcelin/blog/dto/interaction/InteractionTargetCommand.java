package com.sourcelin.blog.dto.interaction;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 互动目标命令。
 */
@Data
public class InteractionTargetCommand
{
    @NotBlank(message = "目标类型不能为空")
    private String targetType;

    @NotNull(message = "目标ID不能为空")
    private Long targetId;
}

