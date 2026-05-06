package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.Announcement;
import com.sourcelin.blog.service.IAnnouncementService;
import com.sourcelin.blog.vo.AnnouncementUnreadVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.web.controller.BaseController;
import com.sourcelin.common.core.web.domain.response.PageResult;
import com.sourcelin.common.security.accessor.BlogLoginAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/front/messages")
public class FrontMessageController extends BaseController
{
    @Autowired
    private IAnnouncementService announcementService;

    @Autowired
    private BlogLoginAccessor blogLoginAccessor;

    /**
     * 前台消息分页（基础版仅系统公告频道）
     */
    @GetMapping
    public PageResult<Announcement> list()
    {
        startPage();
        List<Announcement> list = announcementService.selectFrontAnnouncementList(blogLoginAccessor.getCurrentUserId());
        return toPageResult(list);
    }

    @GetMapping("/{id}")
    public Announcement detail(@PathVariable("id") Long id)
    {
        Announcement announcement = announcementService.selectFrontAnnouncementById(id, blogLoginAccessor.getCurrentUserId());
        if (announcement == null)
        {
            throw new BusinessException(ResultCode.NOT_FOUND, "公告不存在或不可见");
        }
        return announcement;
    }

    @PostMapping("/{id}/read")
    public Boolean read(@PathVariable("id") Long id)
    {
        Long userId = requireLoginUserId();
        if (!announcementService.markRead(id, userId))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "标记已读失败");
        }
        return true;
    }

    @PostMapping("/read-all")
    public Boolean readAll()
    {
        Long userId = requireLoginUserId();
        if (!announcementService.markAllRead(userId))
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "全部已读失败");
        }
        return true;
    }

    @GetMapping("/unread-count")
    public AnnouncementUnreadVO unreadCount()
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        long total = userId == null ? 0L : announcementService.countUnread(userId);
        return new AnnouncementUnreadVO(total);
    }

    private Long requireLoginUserId()
    {
        Long userId = blogLoginAccessor.getCurrentUserId();
        if (userId == null)
        {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "请先登录");
        }
        return userId;
    }
}
