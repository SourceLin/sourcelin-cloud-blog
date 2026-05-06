package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.WebConfig;
import com.sourcelin.blog.service.IWebConfigService;
import com.sourcelin.blog.support.FrontBlogProjectionSupport;
import com.sourcelin.blog.vo.FrontAboutInfoVO;
import com.sourcelin.blog.vo.FrontSiteInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/front/config")
public class FrontConfigController
{
    @Autowired
    private IWebConfigService webConfigService;

    @GetMapping
    public WebConfig getConfig()
    {
        return webConfigService.getWebConfig();
    }

    @GetMapping("/siteInfo")
    public FrontSiteInfoVO getSiteInfo()
    {
        return FrontBlogProjectionSupport.buildSiteInfo(webConfigService.getWebConfig());
    }

    @GetMapping("/about")
    public FrontAboutInfoVO getAboutInfo()
    {
        return FrontBlogProjectionSupport.buildAboutInfo(webConfigService.getWebConfig());
    }
}
