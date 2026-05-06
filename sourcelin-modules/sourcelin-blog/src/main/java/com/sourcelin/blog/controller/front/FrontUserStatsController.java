package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.service.IArticleService;
import com.sourcelin.blog.service.ICollectService;
import com.sourcelin.blog.service.IFollowService;
import com.sourcelin.blog.vo.FrontUserCenterStatsVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Front user stats endpoints.
 */
@RestController
@RequestMapping("/front/user")
public class FrontUserStatsController
{
    @Autowired
    private IArticleService articleService;
    @Autowired
    private ICollectService collectService;
    @Autowired
    private IFollowService followService;
    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    @GetMapping("/stats")
    public FrontUserCenterStatsVO getStats()
    {
        Long currentUserId = blogLoginAccessor.getCurrentUserId();
        if (currentUserId == null || currentUserId <= 0)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        FrontUserCenterStatsVO payload = new FrontUserCenterStatsVO();
        payload.setArticleCount((long) articleService.countArticleByUserId(currentUserId));
        payload.setCollectCount((long) collectService.countCollectByUserId(currentUserId));
        payload.setFollowCount((long) followService.countFollowByUserId(currentUserId));
        payload.setFansCount((long) followService.countFansByTargetUserId(currentUserId));
        return payload;
    }
}
