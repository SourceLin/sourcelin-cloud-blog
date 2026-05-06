package com.sourcelin.blog.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 树洞vo
 *
 * @describe 存储Redis
 * @author sourcelin
 * @date 2023-11-09
 */
@Data
public class TreeHoleVO {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 留言
     */
    private String message;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
