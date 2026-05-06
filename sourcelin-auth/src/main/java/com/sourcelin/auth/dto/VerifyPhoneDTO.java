package com.sourcelin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
/**
 * 一键登录请求参数
 *
 * @author sourcelin
 */

@Data
@Schema(description = "一键登录请求参数")
public class VerifyPhoneDTO{

    @Schema(description = "客户端的token", example = "e1e4c24876514fc999d043ae0f496752")
    private String token;

    @Schema(description = "客户端返回的运营商token", example = "e1e4c24876514fc999d043ae0f496752")
    private String opToken;

    @Schema(description = "客户端返回的运营商，CMCC:中国移动通信, CUCC:中国联通通讯, CTCC:中国电信", example = "CMCC")
    private String operator;

    @Schema(description = "android必须要填写", example = "e4caa1a08ba0570b5c1290b1a0bc9252")
    private String md5;

}
