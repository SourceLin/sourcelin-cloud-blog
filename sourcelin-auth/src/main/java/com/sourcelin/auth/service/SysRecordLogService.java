package com.sourcelin.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sourcelin.common.core.constant.Constants;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.ip.IpUtils;
import com.sourcelin.system.api.service.RemoteSysLogService;
import com.sourcelin.system.api.domain.SysLogininfor;

/**
 * 记录日志方法
 * 
 * @author sourcelin
 */
@Component
public class SysRecordLogService
{
    @Autowired
    private RemoteSysLogService remoteSysLogService;

    /**
     * 记录登录信息
     * 
     * @param username 用户名
     * @param status 状态
     * @param message 消息内容
     * @return
     */
    public void recordLogininfor(String username, String status, String message)
    {
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(username);
        logininfor.setIpaddr(IpUtils.getIpAddr());
        logininfor.setMsg(message);
        // 日志状态
        if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER))
        {
            logininfor.setStatus(Constants.LOGIN_SUCCESS_STATUS);
        }
        else if (Constants.LOGIN_FAIL.equals(status))
        {
            logininfor.setStatus(Constants.LOGIN_FAIL_STATUS);
        }
        remoteSysLogService.saveLogininfor(logininfor, SecurityConstants.INNER);
    }
}
