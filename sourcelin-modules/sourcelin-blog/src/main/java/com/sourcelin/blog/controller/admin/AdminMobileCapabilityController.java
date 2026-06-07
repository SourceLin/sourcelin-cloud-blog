package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.domain.MobileCapability;
import com.sourcelin.blog.service.IMobileCapabilityService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.common.core.web.domain.response.ListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 移动端能力配置后台管理Controller
 * 
 * @author sourcelin
 * @date 2026-06-05
 */
@RestController
@RequestMapping("/admin/mobile/capability")
public class AdminMobileCapabilityController extends BaseController
{
    @Autowired
    private IMobileCapabilityService mobileCapabilityService;

    /**
     * 查询能力配置列表
     */
    @SaCheckPermission(type = "admin", value = "blog:mobile:list")
    @GetMapping("/list")
    public ListResult<MobileCapability> list(MobileCapability mobileCapability)
    {
        List<MobileCapability> list = mobileCapabilityService.selectMobileCapabilityList(mobileCapability);
        return ListResult.<MobileCapability>builder().items(list).build();
    }

    /**
     * 获取能力配置详细信息
     */
    @SaCheckPermission(type = "admin", value = "blog:mobile:query")
    @GetMapping(value = "/{id}")
    public MobileCapability getInfo(@PathVariable("id") Long id)
    {
        MobileCapability capability = mobileCapabilityService.selectMobileCapabilityById(id);
        if (capability == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "配置信息不存在");
        }
        return capability;
    }

    /**
     * 修改能力配置
     */
    @SaCheckPermission(type = "admin", value = "blog:mobile:edit")
    @Log(title = "移动端配置修改", businessType = BusinessType.UPDATE)
    @PutMapping
    public Void edit(@RequestBody MobileCapability mobileCapability)
    {
        if (mobileCapabilityService.updateMobileCapability(mobileCapability) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "修改能力配置失败");
        }
        return null;
    }
}
