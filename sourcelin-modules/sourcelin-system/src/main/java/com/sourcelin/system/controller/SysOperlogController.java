package com.sourcelin.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import com.sourcelin.common.security.annotation.InnerAuth;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.system.api.domain.SysOperLog;
import com.sourcelin.system.service.ISysOperLogService;

/**
 * 操作日志记录
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/operlog")
public class SysOperlogController extends BaseController
{
    @Autowired
    private ISysOperLogService operLogService;

    @SaCheckPermission(type = "admin", value = "system:operlog:list")
    @GetMapping("/list")
    public PageResult<SysOperLog> list(SysOperLog operLog)
    {
        startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return toPageResult(list);
    }

    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission(type = "admin", value = "system:operlog:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOperLog operLog)
    {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
        util.exportExcel(response, list, "操作日志");
    }

    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @SaCheckPermission(type = "admin", value = "system:operlog:remove")
    @DeleteMapping("/{operIds}")
    public Boolean remove(@PathVariable Long[] operIds)
    {
        if (operLogService.deleteOperLogByIds(operIds) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除操作日志失败");
        }
        return true;
    }

    @SaCheckPermission(type = "admin", value = "system:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public Boolean clean()
    {
        operLogService.cleanOperLog();
        return true;
    }

    @InnerAuth
    @PostMapping
    public R<Boolean> add(@RequestBody SysOperLog operLog)
    {
        if (operLogService.insertOperlog(operLog) <= 0)
        {
            return R.fail("保存操作日志失败");
        }
        return R.ok(Boolean.TRUE);
    }
}
