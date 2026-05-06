package com.sourcelin.system.domain.vo;

import lombok.Data;

/**
 * 管理后台 UI 策略配置。
 */
@Data
public class AdminUiPolicyVo
{
    private Boolean allowCustomThemeColor;
    private Boolean allowUserLayoutSwitch;
    private Boolean allowUserSidebarAppearanceSwitch;
}
