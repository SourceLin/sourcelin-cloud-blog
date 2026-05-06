package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.vo.FrontDictBundleVO;
import com.sourcelin.blog.vo.FrontDictItemVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.common.core.web.domain.response.ListResult;
import com.sourcelin.system.api.domain.SysDictData;
import com.sourcelin.system.api.service.RemoteSysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/front/dicts")
public class FrontDictController
{
    @Autowired
    private RemoteSysDictDataService remoteSysDictDataService;

    @GetMapping
    public ListResult<FrontDictBundleVO> list(@RequestParam(value = "types", required = false) String types)
    {
        if (StringUtils.isBlank(types))
        {
            return ListResult.of(Collections.emptyList());
        }

        String[] rawTypes = types.split(",");
        List<FrontDictBundleVO> bundles = new ArrayList<>();
        for (String raw : rawTypes)
        {
            String dictType = StringUtils.trim(raw);
            if (StringUtils.isEmpty(dictType))
            {
                continue;
            }

            ApiResponse<List<SysDictData>> result = remoteSysDictDataService.listByType(dictType, "front-platform");
            if (result == null
                || result.getCode() == null
                || !ResultCode.SUCCESS.getCode().equals(result.getCode())
                || result.getData() == null)
            {
                // 对单个 dictType 失败，不阻断其他 dictType
                bundles.add(buildEmptyBundle(dictType));
                continue;
            }

            FrontDictBundleVO bundle = new FrontDictBundleVO();
            bundle.setDictType(dictType);
            bundle.setItems(toFrontDictItems(result.getData()));
            bundles.add(bundle);
        }
        return ListResult.of(bundles);
    }

    private FrontDictBundleVO buildEmptyBundle(String dictType)
    {
        FrontDictBundleVO bundle = new FrontDictBundleVO();
        bundle.setDictType(dictType);
        bundle.setItems(Collections.emptyList());
        return bundle;
    }

    private List<FrontDictItemVO> toFrontDictItems(List<SysDictData> rows)
    {
        if (rows == null || rows.isEmpty())
        {
            return Collections.emptyList();
        }
        List<FrontDictItemVO> items = new ArrayList<>();
        for (SysDictData row : rows)
        {
            if (row == null)
            {
                continue;
            }
            String label = StringUtils.trim(row.getDictLabel());
            String value = StringUtils.trim(row.getDictValue());
            if (StringUtils.isEmpty(label) || StringUtils.isEmpty(value))
            {
                continue;
            }

            FrontDictItemVO item = new FrontDictItemVO();
            item.setLabel(label);
            item.setValue(value);

            item.setSort(row.getDictSort() == null ? 0 : row.getDictSort().intValue());

            String status = StringUtils.trim(row.getStatus());
            item.setEnabled(status == null ? true : !"1".equals(status));

            item.setTagType(resolveTagType(row.getListClass()));
            items.add(item);
        }
        return items;
    }

    private String resolveTagType(String listClass)
    {
        String code = StringUtils.isNull(listClass) ? "" : listClass.trim().toUpperCase();
        if ("S".equals(code))
        {
            return "success";
        }
        if ("W".equals(code))
        {
            return "warning";
        }
        if ("D".equals(code))
        {
            return "error";
        }
        if ("I".equals(code))
        {
            return "info";
        }
        return "default";
    }
}

