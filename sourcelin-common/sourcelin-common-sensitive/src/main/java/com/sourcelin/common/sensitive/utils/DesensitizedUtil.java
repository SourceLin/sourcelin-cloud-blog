package com.sourcelin.common.sensitive.utils;

import com.sourcelin.common.core.utils.StringUtils;

/**
 * 脱敏工具类
 *
 * @author sourcelin
 */
public class DesensitizedUtil
{
    public static String password(String password)
    {
        if (StringUtils.isBlank(password))
        {
            return StringUtils.EMPTY;
        }
        return StringUtils.repeat('*', password.length());
    }

    public static String carLicense(String carLicense)
    {
        if (StringUtils.isBlank(carLicense))
        {
            return StringUtils.EMPTY;
        }
        if (carLicense.length() == 7)
        {
            carLicense = StringUtils.hide(carLicense, 3, 6);
        }
        else if (carLicense.length() == 8)
        {
            carLicense = StringUtils.hide(carLicense, 3, 7);
        }
        return carLicense;
    }
}
