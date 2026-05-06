package com.sourcelin.blog.dto.interaction;

import lombok.Data;

/**
 * 后台互动记录删除命令。
 */
@Data
public class InteractionActionDeleteCommand
{
    private String actionType;

    private Long[] ids;
}
