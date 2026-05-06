package com.sourcelin.system.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.sourcelin.system.service.ISysDictTypeService;
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
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.system.api.domain.SysDictType;

/**
 * 数据字典信息
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/dict/type")
public class SysDictTypeController extends BaseController
{
    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @SaCheckPermission(type = "admin", value = "system:dict:list")
    @GetMapping("/list")
    public PageResult<SysDictType> list(SysDictType dictType)
    {
        startPage();
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return toPageResult(list);
    }

    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @SaCheckPermission(type = "admin", value = "system:dict:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictType dictType)
    {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        util.exportExcel(response, list, "字典类型");
    }

    /**
     * 查询字典类型详细
     */
    @SaCheckPermission(type = "admin", value = "system:dict:query")
    @GetMapping(value = "/{dictId}")
    public SysDictType getInfo(@PathVariable Long dictId)
    {
        return dictTypeService.selectDictTypeById(dictId);
    }

    /**
     * 新增字典类型
     */
    @SaCheckPermission(type = "admin", value = "system:dict:add")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public Boolean add(@Validated @RequestBody SysDictType dict)
    {
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            throw new BusinessException(ResultCode.CONFLICT, "新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setCreateBy(adminLoginAccessor.getUsername());
        if (dictTypeService.insertDictType(dict) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增字典类型失败");
        }
        return true;
    }

    /**
     * 修改字典类型
     */
    @SaCheckPermission(type = "admin", value = "system:dict:edit")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean edit(@Validated @RequestBody SysDictType dict)
    {
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            throw new BusinessException(ResultCode.CONFLICT, "修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        dict.setUpdateBy(adminLoginAccessor.getUsername());
        if (dictTypeService.updateDictType(dict) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改字典类型失败");
        }
        return true;
    }

    /**
     * 删除字典类型
     */
    @SaCheckPermission(type = "admin", value = "system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public Boolean remove(@PathVariable Long[] dictIds)
    {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return true;
    }

    /**
     * 刷新字典缓存
     */
    @SaCheckPermission(type = "admin", value = "system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public Boolean refreshCache()
    {
        dictTypeService.resetDictCache();
        return true;
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public List<SysDictType> optionselect()
    {
        return dictTypeService.selectDictTypeAll();
    }
}
