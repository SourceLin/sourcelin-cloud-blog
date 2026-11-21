package com.sourcelin.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 博客 AI 功能开关状态。
 */
@Data
@AllArgsConstructor
public class AiStatusVO
{
    private boolean enabled;
}
