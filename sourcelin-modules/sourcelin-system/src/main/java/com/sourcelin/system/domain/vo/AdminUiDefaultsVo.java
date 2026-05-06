package com.sourcelin.system.domain.vo;

import lombok.Data;

/**
 * 管理后台 UI 默认配置。
 */
@Data
public class AdminUiDefaultsVo
{
    private String themePreference;
    private String layout;
    private String sidebarAppearance;
    private String themeColor;
    private Boolean showTagsView;
    private Boolean showAppLogo;
    private String pageSwitchingAnimation;
    private String componentSize;
}
