package com.sourcelin.system.service;

import com.sourcelin.system.domain.vo.AdminUiConfigAggregateVo;

/**
 * 管理后台 UI 配置服务。
 */
public interface IAdminUiConfigService
{
    /**
     * 获取聚合配置。
     *
     * @return 聚合配置
     */
    AdminUiConfigAggregateVo getConfig();

    /**
     * 更新聚合配置。
     *
     * @param payload 聚合配置
     * @return 是否成功
     */
    boolean updateConfig(AdminUiConfigAggregateVo payload);
}
