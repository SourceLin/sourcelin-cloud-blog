package com.sourcelin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 手机验证码请求参数
 *
 * @author sourcelin
 */
@Data
@Schema(description = "手机验证码请求参数")
public class VerifyCodeDTO {

    @Schema(description = "电话号码", example = "13888888888")
    private String phone;

    @Schema(description = "区号", example = "86")
    private String zone;

    @Schema(description = "验证码", example = "124454")
    private String code;

}

