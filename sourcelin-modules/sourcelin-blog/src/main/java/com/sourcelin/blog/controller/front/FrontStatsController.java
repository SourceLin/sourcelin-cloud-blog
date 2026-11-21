package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.service.IBlogStatsService;
import com.sourcelin.blog.vo.FrontStatsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/front/stats")
public class FrontStatsController
{
    @Autowired
    private IBlogStatsService blogStatsService;

    @GetMapping
    public FrontStatsVO getStats()
    {
        return blogStatsService.getFrontStats();
    }
}
