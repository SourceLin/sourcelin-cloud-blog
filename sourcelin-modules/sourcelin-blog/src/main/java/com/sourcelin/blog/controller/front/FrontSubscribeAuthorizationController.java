package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.SubscribeAuthorization;
import com.sourcelin.blog.dto.FrontSubscribeAuthorizationDTO;
import com.sourcelin.blog.service.ISubscribeAuthorizationService;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.ServletUtils;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.ip.IpUtils;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/front/subscriptions")
public class FrontSubscribeAuthorizationController extends BaseController
{
    @Autowired
    private ISubscribeAuthorizationService subscribeAuthorizationService;

    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    @PostMapping("/authorizations")
    public Void create(@Valid @RequestBody FrontSubscribeAuthorizationDTO body)
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId == null || userId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        SubscribeAuthorization authorization = new SubscribeAuthorization();
        authorization.setUserId(userId);
        authorization.setTemplateId(StringUtils.substring(StringUtils.trim(body.getTemplateId()), 0, 128));
        authorization.setScene(StringUtils.substring(StringUtils.trim(body.getScene()), 0, 64));
        authorization.setAuthorizationStatus(StringUtils.substring(StringUtils.trim(body.getAuthorizationStatus()), 0, 32));
        authorization.setPlatform(StringUtils.substring(StringUtils.trim(body.getPlatform()), 0, 32));
        authorization.setClientIp(IpUtils.getIpAddr(ServletUtils.getRequest()));
        authorization.setUserAgent(StringUtils.substring(ServletUtils.getRequest().getHeader("User-Agent"), 0, 255));
        if (subscribeAuthorizationService.insertSubscribeAuthorization(authorization) <= 0)
        {
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "订阅授权记录失败");
        }
        return null;
    }
}
