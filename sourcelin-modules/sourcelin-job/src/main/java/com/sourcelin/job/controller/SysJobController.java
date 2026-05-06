package com.sourcelin.job.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.sourcelin.job.domain.SysJob;
import com.sourcelin.job.service.ISysJobService;
import com.sourcelin.job.util.CronUtils;
import com.sourcelin.job.util.ScheduleUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sourcelin.common.core.constant.Constants;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.exception.job.TaskException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;

/**
 * 调度任务信息操作处理
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/job")
public class SysJobController extends BaseController
{
    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @Autowired
    private ISysJobService jobService;

    /**
     * 查询定时任务列表
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:list")
    @GetMapping("/list")
    public PageResult<SysJob> list(SysJob sysJob)
    {
        startPage();
        List<SysJob> list = jobService.selectJobList(sysJob);
        return toPageResult(list);
    }

    /**
     * 导出定时任务列表
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:export")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJob sysJob)
    {
        List<SysJob> list = jobService.selectJobList(sysJob);
        ExcelUtil<SysJob> util = new ExcelUtil<SysJob>(SysJob.class);
        util.exportExcel(response, list, "定时任务");
    }

    /**
     * 获取定时任务详细信息
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:query")
    @GetMapping(value = "/{jobId}")
    public SysJob getInfo(@PathVariable("jobId") Long jobId)
    {
        return jobService.selectJobById(jobId);
    }

    /**
     * 新增定时任务
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:add")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public Boolean add(@RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "新增任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        job.setCreateBy(adminLoginAccessor.getUsername());
        if (jobService.insertJob(job) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增任务失败");
        }
        return true;
    }

    /**
     * 修改定时任务
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:edit")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean edit(@RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "修改任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        job.setUpdateBy(adminLoginAccessor.getUsername());
        if (jobService.updateJob(job) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改任务失败");
        }
        return true;
    }

    /**
     * 定时任务状态修改
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:changeStatus")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public Boolean changeStatus(@RequestBody SysJob job) throws SchedulerException
    {
        SysJob newJob = jobService.selectJobById(job.getJobId());
        if (newJob == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "任务不存在");
        }
        newJob.setStatus(job.getStatus());
        if (jobService.changeStatus(newJob) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改任务状态失败");
        }
        return true;
    }

    /**
     * 定时任务立即执行一次
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:changeStatus")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public Boolean run(@RequestBody SysJob job) throws SchedulerException
    {
        boolean result = jobService.run(job);
        if (!result)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "任务不存在或已过期");
        }
        return true;
    }

    /**
     * 删除定时任务
     */
    @SaCheckPermission(type = "admin", value = "monitor:job:remove")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobIds}")
    public Boolean remove(@PathVariable Long[] jobIds) throws SchedulerException, TaskException
    {
        jobService.deleteJobByIds(jobIds);
        return true;
    }
}
