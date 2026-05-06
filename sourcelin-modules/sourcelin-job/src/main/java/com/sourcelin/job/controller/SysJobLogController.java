package com.sourcelin.job.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.sourcelin.job.domain.SysJobLog;
import com.sourcelin.job.service.ISysJobLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;

/**
 * 调度日志操作处理
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/job/log")
public class SysJobLogController extends BaseController
{
    @Autowired
    private ISysJobLogService jobLogService;

    /**
     * 查询定时任务调度日志列表
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:list")
    @GetMapping("/list")
    public PageResult<SysJobLog> list(SysJobLog sysJobLog)
    {
        startPage();
        List<SysJobLog> list = jobLogService.selectJobLogList(sysJobLog);
        return toPageResult(list);
    }

    /**
     * 导出定时任务调度日志列表
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:export")
    @Log(title = "任务调度日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJobLog sysJobLog)
    {
        List<SysJobLog> list = jobLogService.selectJobLogList(sysJobLog);
        ExcelUtil<SysJobLog> util = new ExcelUtil<SysJobLog>(SysJobLog.class);
        util.exportExcel(response, list, "调度日志");
    }

    /**
     * 根据调度编号获取详细信息
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:query")
    @GetMapping(value = "/{jobLogId}")
    public SysJobLog getInfo(@PathVariable Long jobLogId)
    {
        return jobLogService.selectJobLogById(jobLogId);
    }

    /**
     * 删除定时任务调度日志
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:remove")
    @Log(title = "定时任务调度日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobLogIds}")
    public Boolean remove(@PathVariable Long[] jobLogIds)
    {
        if (jobLogService.deleteJobLogByIds(jobLogIds) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除调度日志失败");
        }
        return true;
    }

    /**
     * 清空定时任务调度日志
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:remove")
    @Log(title = "调度日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public Boolean clean()
    {
        jobLogService.cleanJobLog();
        return true;
    }
}
