package com.sourcelin.blog.vo;

import lombok.Data;

/**
 * 前台申请成为博主请求体
 */
@Data
public class BloggerApplyRequest {
    /** 申请理由 */
    private String reason;
    /** 联系方式 */
    private String contact;
}
