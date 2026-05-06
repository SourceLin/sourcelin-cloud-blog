package com.sourcelin.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户注册参数")
public class RegisterDTO extends LoginDTO {

    @Schema(description = "用户手机号", example = "15888888888")
    private String phone;

    @Schema(description = "用户邮箱", example = "user@example.com")
    private String email;

    @Schema(description = "注册方式", example = "1")
    private Integer regType;

    @Schema(description = "邮箱验证码")
    private String emailCode;

    @Schema(description = "注册请求幂等ID")
    private String registerRequestId;
}
