package com.sourcelin.common.sensitive.enums;

import java.util.function.Function;
import com.sourcelin.common.sensitive.utils.DesensitizedUtil;

/**
 * 脱敏类型
 *
 * @author sourcelin
 */
public enum DesensitizedType
{
    USERNAME(s -> s.replaceAll("(\\S)\\S(\\S*)", "$1*$2")),
    PASSWORD(DesensitizedUtil::password),
    ID_CARD(s -> s.replaceAll("(\\d{4})\\d{10}(\\d{3}[Xx]|\\d{4})", "$1** **** ****$2")),
    PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),
    EMAIL(s -> s.replaceAll("(^.)[^@]*(@.*$)", "$1****$2")),
    BANK_CARD(s -> s.replaceAll("\\d{15}(\\d{3})", "**** **** **** **** $1")),
    CAR_LICENSE(DesensitizedUtil::carLicense);

    private final Function<String, String> desensitizer;

    DesensitizedType(Function<String, String> desensitizer)
    {
        this.desensitizer = desensitizer;
    }

    public Function<String, String> desensitizer()
    {
        return desensitizer;
    }
}
