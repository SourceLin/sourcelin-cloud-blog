package com.sourcelin.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EmailCodeRequestDTO {

    @NotBlank(message = "邮箱地址不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;

    private String captchaCode;

    private String captchaUuid;
}
