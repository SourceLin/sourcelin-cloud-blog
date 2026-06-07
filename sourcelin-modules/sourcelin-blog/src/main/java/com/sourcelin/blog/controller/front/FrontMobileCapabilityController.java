package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.MobileCapability;
import com.sourcelin.blog.service.IMobileCapabilityService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 移动端配置前台Controller
 * 
 * @author sourcelin
 * @date 2026-06-05
 */
@RestController
@RequestMapping("/front/mobile")
public class FrontMobileCapabilityController extends BaseController
{
    @Autowired
    private IMobileCapabilityService mobileCapabilityService;

    /**
     * 获取指定端侧的能力配置
     */
    @GetMapping("/capabilities")
    public MobileCapability getCapabilities(@RequestParam(value = "client", defaultValue = "mini") String client)
    {
        MobileCapability capability = mobileCapabilityService.selectMobileCapabilityByClient(client);
        if (capability == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "未找到该客户端的配置信息");
        }
        return capability;
    }
}
