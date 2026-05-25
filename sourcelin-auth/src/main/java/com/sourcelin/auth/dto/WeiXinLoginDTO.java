package com.sourcelin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 微信登录
 *
 * @author sourcelin
 */
@Data
@Schema(description = "微信授权登录请求参数")
public class WeiXinLoginDTO {

    @Schema(description = "code", example = "dac071e4f2604729ac676ed4c782dc8d")
    @NotBlank(message = "微信登录 code 不能为空")
    private String code;

    @Schema(description = "手机号", example = "13800138000")
    private String encryptedData;

    @Schema(description = "iv", example = "iv")
    private String iv;

    @Schema(description = "微信用户昵称", example = "小可爱")
    private String nickName;

    @Schema(description = "微信用户头像", example = "http")
    private String avatarUrl;

    @Schema(description = "性别 0：未知、1：男、2：女", example = "1")
    private Integer gender;

}
