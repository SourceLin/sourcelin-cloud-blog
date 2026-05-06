package com.sourcelin.system.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.sourcelin.system.service.ISysDictDataService;
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
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.utils.poi.ExcelUtil;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.log.annotation.Log;
import com.sourcelin.common.log.enums.BusinessType;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.sourcelin.common.security.accessor.AdminLoginAccessor;
import com.sourcelin.system.api.domain.SysDictData;

/**
 * 数据字典信息
 * 
 * @author sourcelin
 */
@RestController
@RequestMapping("/dict/data")
public class SysDictDataController extends BaseController
{
    @Autowired
    private AdminLoginAccessor adminLoginAccessor;

    @Autowired
    private ISysDictDataService dictDataService;
    
    @Autowired
    private ISysDictTypeService dictTypeService;

    @SaCheckPermission(type = "admin", value = "system:dict:list")
    @GetMapping("/list")
    public PageResult<SysDictData> list(SysDictData dictData)
    {
        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return toPageResult(list);
    }

    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @SaCheckPermission(type = "admin", value = "system:dict:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictData dictData)
    {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
        util.exportExcel(response, list, "字典数据");
    }

    /**
     * 查询字典数据详细
     */
    @SaCheckPermission(type = "admin", value = "system:dict:query")
    @GetMapping(value = "/{dictCode}")
    public SysDictData getInfo(@PathVariable Long dictCode)
    {
        return dictDataService.selectDictDataById(dictCode);
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public List<SysDictData> dictType(@PathVariable String dictType)
    {
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        return data;
    }

    /**
     * 新增字典类型
     */
    @SaCheckPermission(type = "admin", value = "system:dict:add")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public Boolean add(@Validated @RequestBody SysDictData dict)
    {
        dict.setCreateBy(adminLoginAccessor.getUsername());
        if (dictDataService.insertDictData(dict) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "新增字典数据失败");
        }
        return true;
    }

    /**
     * 修改保存字典类型
     */
    @SaCheckPermission(type = "admin", value = "system:dict:edit")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public Boolean edit(@Validated @RequestBody SysDictData dict)
    {
        dict.setUpdateBy(adminLoginAccessor.getUsername());
        if (dictDataService.updateDictData(dict) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "修改字典数据失败");
        }
        return true;
    }

    /**
     * 删除字典类型
     */
    @SaCheckPermission(type = "admin", value = "system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public Boolean remove(@PathVariable Long[] dictCodes)
    {
        dictDataService.deleteDictDataByIds(dictCodes);
        return true;
    }
}
