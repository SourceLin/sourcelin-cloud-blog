package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.WebNavbar;
import com.sourcelin.blog.service.IWebNavbarService;
import com.sourcelin.blog.support.FrontBlogProjectionSupport;
import com.sourcelin.blog.vo.FrontNavbarVO;
import com.sourcelin.common.core.web.domain.response.ListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front/navbars")
public class FrontNavbarController
{
    @Autowired
    private IWebNavbarService webNavbarService;

    @GetMapping
    public ListResult<FrontNavbarVO> list()
    {
        List<WebNavbar> navbars = webNavbarService.selectWebNavbarList(new WebNavbar());
        List<FrontNavbarVO> tree = FrontBlogProjectionSupport.buildNavbarTree(navbars);
        return ListResult.of(tree);
    }
}
