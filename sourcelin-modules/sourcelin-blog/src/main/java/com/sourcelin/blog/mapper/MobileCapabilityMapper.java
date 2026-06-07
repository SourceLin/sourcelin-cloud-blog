package com.sourcelin.blog.mapper;

import java.util.List;
import com.sourcelin.blog.domain.MobileCapability;

/**
 * 移动端能力配置Mapper接口
 * 
 * @author sourcelin
 * @date 2026-06-05
 */
public interface MobileCapabilityMapper 
{
    /**
     * 根据客户端查询能力配置
     * 
     * @param client 客户端(mini/h5/app)
     * @return 移动端能力配置
     */
    public MobileCapability selectMobileCapabilityByClient(String client);

    /**
     * 根据主键查询能力配置
     * 
     * @param id 主键
     * @return 能力配置
     */
    public MobileCapability selectMobileCapabilityById(Long id);

    /**
     * 查询能力配置列表
     * 
     * @param mobileCapability 过滤参数
     * @return 能力配置列表
     */
    public List<MobileCapability> selectMobileCapabilityList(MobileCapability mobileCapability);

    /**
     * 新增能力配置
     * 
     * @param mobileCapability 能力配置
     * @return 结果
     */
    public int insertMobileCapability(MobileCapability mobileCapability);

    /**
     * 修改能力配置
     * 
     * @param mobileCapability 能力配置
     * @return 结果
     */
    public int updateMobileCapability(MobileCapability mobileCapability);
}
