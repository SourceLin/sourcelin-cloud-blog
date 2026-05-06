package com.sourcelin.system.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 管理后台 UI 聚合配置。
 */
@Data
public class AdminUiConfigAggregateVo
{
    private AdminUiDefaultsVo defaults;
    private AdminUiPolicyVo policy;
    private List<String> presets;
}
