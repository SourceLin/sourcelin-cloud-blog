package com.sourcelin.auth.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailLoginDTO {

    @NotBlank(message = "邮箱地址不能为空")
    private String email;

    private String password;

    private String emailCode;

    private String loginType;

    private String captchaCode;

    private String captchaUuid;
}
