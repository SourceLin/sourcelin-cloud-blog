package com.sourcelin.system.controller;

import java.util.List;

import com.sourcelin.system.service.ISysDeptService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sourcelin.common.core.constant.UserConstants;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.system.api.domain.SysDept;

/**
 * 部门信息
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/dept")
public class SysDeptController extends BaseController
{
    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @SaCheckPermission(type = "admin", value = "system:dept:list")
    @GetMapping("/list")
    public List<SysDept> list(SysDept dept)
    {
        List<SysDept> deptList = deptService.selectDeptList(dept);
        return deptService.buildDeptTree(deptList);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @SaCheckPermission(type = "admin", value = "system:dept:list")
    @GetMapping("/list/exclude/{deptId}")
    public List<SysDept> excludeChild(@PathVariable(value = "deptId", required = false) Long deptId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        depts.removeIf(d -> d.getDeptId().intValue() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""));
        return depts;
    }

    /**
     * 根据部门编号获取详细信息
     */
    @SaCheckPermission(type = "admin", value = "system:dept:query")
    @GetMapping(value = "/{deptId}")
    public SysDept getInfo(@PathVariable Long deptId)
    {
        deptService.checkDeptDataScope(deptId);
        return deptService.selectDeptById(deptId);
    }

    /**
     * 新增部门
     */
    @SaCheckPermission(type = "admin", value = "system:dept:add")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Boolean add(@Validated @RequestBody SysDept dept)
    {
        if (!deptService.checkDeptNameUnique(dept))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        dept.setCreateBy(adminLoginAccessor.getUsername());
        if (deptService.insertDept(dept) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增部门失败");
        }
        return true;
    }

    /**
     * 修改部门
     */
    @SaCheckPermission(type = "admin", value = "system:dept:edit")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean edit(@Validated @RequestBody SysDept dept)
    {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (!deptService.checkDeptNameUnique(dept))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        else if (dept.getParentId().equals(deptId))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "该部门包含未停用的子部门");
        }
        dept.setUpdateBy(adminLoginAccessor.getUsername());
        if (deptService.updateDept(dept) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改部门失败");
        }
        return true;
    }

    /**
     * 删除部门
     */
    @SaCheckPermission(type = "admin", value = "system:dept:remove")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public Boolean remove(@PathVariable Long deptId)
    {
        if (deptService.hasChildByDeptId(deptId))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "部门存在用户,不允许删除");
        }
        deptService.checkDeptDataScope(deptId);
        if (deptService.deleteDeptById(deptId) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "删除部门失败");
        }
        return true;
    }
}
