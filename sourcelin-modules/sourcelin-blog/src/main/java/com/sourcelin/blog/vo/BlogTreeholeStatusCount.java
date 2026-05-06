package com.sourcelin.blog.vo;

import lombok.Data;

/**
 * 树洞按 status 聚合（b_treehole.status：0 正常 1 置顶 2 删除）
 */
@Data
public class BlogTreeholeStatusCount
{
    private Long status;
    private Long cnt;
}
