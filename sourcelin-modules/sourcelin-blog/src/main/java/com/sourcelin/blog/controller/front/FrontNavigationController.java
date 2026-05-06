package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.Navigation;
import com.sourcelin.blog.service.INavigationService;
import com.sourcelin.blog.vo.FrontNavigationVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.domain.response.ListResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/front/navigation")
public class FrontNavigationController
{
    @Autowired
    private INavigationService navigationService;

    @GetMapping
    public ListResult<FrontNavigationVO> list(@RequestParam(value = "category", required = false) String category,
                                              @RequestParam(value = "keyword", required = false) String keyword,
                                              @RequestParam(value = "isRecommend", required = false) Integer isRecommend)
    {
        Navigation filter = new Navigation();
        filter.setCategory(category);
        filter.setName(keyword);
        filter.setStatus(1);
        filter.setIsRecommend(isRecommend);
        List<Navigation> list = navigationService.selectNavigationList(filter);
        List<FrontNavigationVO> result = new ArrayList<FrontNavigationVO>(list.size());
        for (Navigation item : list)
        {
            FrontNavigationVO vo = new FrontNavigationVO();
            BeanUtils.copyProperties(item, vo);
            result.add(vo);
        }
        return ListResult.of(result);
    }

    @PostMapping("/{id}/click")
    public Void reportClick(@PathVariable("id") Long id)
    {
        if (navigationService.updateClickCount(id) <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "点击上报失败");
        }
        return null;
    }
}
