package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.MobileCapability;
import com.sourcelin.blog.mapper.MobileCapabilityMapper;
import com.sourcelin.blog.service.IMobileCapabilityService;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 移动端能力配置Service业务层处理
 * 
 * @author sourcelin
 * @date 2026-06-05
 */
@Service
public class MobileCapabilityServiceImpl implements IMobileCapabilityService 
{
    @Autowired
    private MobileCapabilityMapper mobileCapabilityMapper;

    /**
     * 根据客户端查询能力配置
     * 
     * @param client 客户端(mini/h5/app)
     * @return 移动端能力配置
     */
    @Override
    public MobileCapability selectMobileCapabilityByClient(String client)
    {
        return mobileCapabilityMapper.selectMobileCapabilityByClient(client);
    }

    /**
     * 根据主键查询能力配置
     * 
     * @param id 主键
     * @return 能力配置
     */
    @Override
    public MobileCapability selectMobileCapabilityById(Long id)
    {
        return mobileCapabilityMapper.selectMobileCapabilityById(id);
    }

    /**
     * 查询能力配置列表
     * 
     * @param mobileCapability 过滤参数
     * @return 能力配置列表
     */
    @Override
    public List<MobileCapability> selectMobileCapabilityList(MobileCapability mobileCapability)
    {
        return mobileCapabilityMapper.selectMobileCapabilityList(mobileCapability);
    }

    /**
     * 新增能力配置
     * 
     * @param mobileCapability 能力配置
     * @return 结果
     */
    @Override
    public int insertMobileCapability(MobileCapability mobileCapability)
    {
        mobileCapability.setCreateTime(DateUtils.getNowDate());
        return mobileCapabilityMapper.insertMobileCapability(mobileCapability);
    }

    /**
     * 修改能力配置
     * 
     * @param mobileCapability 能力配置
     * @return 结果
     */
    @Override
    public int updateMobileCapability(MobileCapability mobileCapability)
    {
        mobileCapability.setUpdateTime(DateUtils.getNowDate());
        return mobileCapabilityMapper.updateMobileCapability(mobileCapability);
    }

    /**
     * 删除能力配置
     *
     * @param id 主键
     * @return 结果
     */
    @Override
    public int deleteMobileCapability(Long id)
    {
        return mobileCapabilityMapper.deleteMobileCapability(id);
    }
}
