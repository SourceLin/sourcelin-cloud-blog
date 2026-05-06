package com.sourcelin.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.sourcelin.system.service.ISysLogininforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sourcelin.common.core.domain.R;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.constant.CacheConstants;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import com.sourcelin.common.redis.service.RedisService;
import com.sourcelin.common.security.annotation.InnerAuth;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.system.api.domain.SysLogininfor;

/**
 * 系统访问记录
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/logininfor")
public class SysLogininforController extends BaseController
{
    @Autowired
    private ISysLogininforService logininforService;

    @Autowired
    private RedisService redisService;

    @SaCheckPermission(type = "admin", value = "system:logininfor:list")
    @GetMapping("/list")
    public PageResult<SysLogininfor> list(SysLogininfor logininfor)
    {
        startPage();
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        return toPageResult(list);
    }

    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission(type = "admin", value = "system:logininfor:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysLogininfor logininfor)
    {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
        util.exportExcel(response, list, "登录日志");
    }

    @SaCheckPermission(type = "admin", value = "system:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public Boolean remove(@PathVariable Long[] infoIds)
    {
        if (logininforService.deleteLogininforByIds(infoIds) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除登录日志失败");
        }
        return true;
    }

    @SaCheckPermission(type = "admin", value = "system:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/clean")
    public Boolean clean()
    {
        logininforService.cleanLogininfor();
        return true;
    }

    @SaCheckPermission(type = "admin", value = "system:logininfor:unlock")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public Boolean unlock(@PathVariable("userName") String userName)
    {
        redisService.deleteObject(CacheConstants.PWD_ERR_CNT_KEY + userName);
        return true;
    }

    @InnerAuth
    @PostMapping
    public R<Boolean> add(@RequestBody SysLogininfor logininfor)
    {
        if (logininforService.insertLogininfor(logininfor) <= 0)
        {
            return R.fail("保存登录日志失败");
        }
        return R.ok(Boolean.TRUE);
    }
}
