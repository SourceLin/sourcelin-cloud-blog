package com.sourcelin.system.service.impl;

import java.util.List;

import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.message.api.service.RemoteMessageService;
import com.sourcelin.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sourcelin.system.domain.SysNotice;
import com.sourcelin.system.mapper.SysNoticeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 公告 服务层实现
 * 
 * @author sourcelin
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService
{
    private static final Logger log = LoggerFactory.getLogger(SysNoticeServiceImpl.class);

    @Autowired
    private SysNoticeMapper noticeMapper;

    @Autowired
    private RemoteMessageService messageService;

    @Value("${blog.message.enabled:false}")
    private boolean realtimeEnabled;

    @Override
    public List<SysNotice> selectPersonalNotices(Long userId, Integer isRead, String title)
    {
        return noticeMapper.selectPersonalNotices(userId, isRead, title);
    }

    @Override
    public void markRead(Long userId, Long noticeId)
    {
        invalidateOnChange(noticeMapper.markRead(userId, noticeId), "admin:" + userId);
    }

    private int invalidateOnChange(int rows, String principal)
    {
        if (rows > 0 && realtimeEnabled)
        {
            try
            {
                ApiResponse<Void> response = messageService.pushMessage(principal,
                    "{\"type\":\"unread_changed\"}", "system", SecurityConstants.INNER);
                if (response == null || !Integer.valueOf(0).equals(response.getCode()))
                {
                    log.warn("后台通知的实时刷新事件未送达");
                }
            }
            catch (RuntimeException e)
            {
                log.warn("后台通知的实时刷新事件发送失败", e);
            }
        }
        return rows;
    }

    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId)
    {
        return noticeMapper.selectNoticeById(noticeId);
    }

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice)
    {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int insertNotice(SysNotice notice)
    {
        return invalidateOnChange(noticeMapper.insertNotice(notice), "admin:*");
    }

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    public int updateNotice(SysNotice notice)
    {
        return invalidateOnChange(noticeMapper.updateNotice(notice), "admin:*");
    }

    /**
     * 删除公告对象
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId)
    {
        return invalidateOnChange(noticeMapper.deleteNoticeById(noticeId), "admin:*");
    }

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds)
    {
        return invalidateOnChange(noticeMapper.deleteNoticeByIds(noticeIds), "admin:*");
    }
}
