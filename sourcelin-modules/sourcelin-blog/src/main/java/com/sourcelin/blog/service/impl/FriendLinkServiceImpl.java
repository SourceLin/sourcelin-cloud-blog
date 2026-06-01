package com.sourcelin.blog.service.impl;

import com.sourcelin.blog.domain.FriendLink;
import com.sourcelin.blog.mapper.FriendLinkMapper;
import com.sourcelin.blog.service.IFriendLinkService;
import com.sourcelin.blog.support.BlogFileCenterSupport;
import com.sourcelin.common.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 友链Service业务层处理
 * 
 * @author sourcelin
 * @date 2026-01-18
 */
@Service
public class FriendLinkServiceImpl implements IFriendLinkService 
{
    @Autowired
    private FriendLinkMapper friendLinkMapper;
    @Autowired
    private BlogFileCenterSupport blogFileCenterSupport;

    private static final String FRIEND_LINK_BIZ_TYPE = "friend_link";

    /**
     * 查询友链
     * 
     * @param id 友链主键
     * @return 友链
     */
    @Override
    public FriendLink selectFriendLinkById(Long id)
    {
        return friendLinkMapper.selectFriendLinkById(id);
    }

    /**
     * 查询友链列表
     * 
     * @param friendLink 友链
     * @return 友链
     */
    @Override
    public List<FriendLink> selectFriendLinkList(FriendLink friendLink)
    {
        return friendLinkMapper.selectFriendLinkList(friendLink);
    }

    /**
     * 新增友链
     * 
     * @param friendLink 友链
     * @return 结果
     */
    @Override
    public int insertFriendLink(FriendLink friendLink)
    {
        friendLink.setCreateTime(DateUtils.getNowDate());
        friendLink.setUpdateTime(DateUtils.getNowDate());
        int rows = friendLinkMapper.insertFriendLink(friendLink);
        if (rows > 0 && friendLink.getId() != null)
        {
            blogFileCenterSupport.confirmFile(friendLink.getAvatarFileId(), FRIEND_LINK_BIZ_TYPE,
                String.valueOf(friendLink.getId()));
        }
        return rows;
    }

    /**
     * 修改友链
     * 
     * @param friendLink 友链
     * @return 结果
     */
    @Override
    public int updateFriendLink(FriendLink friendLink)
    {
        friendLink.setUpdateTime(DateUtils.getNowDate());
        int rows = friendLinkMapper.updateFriendLink(friendLink);
        if (rows > 0 && friendLink.getId() != null)
        {
            blogFileCenterSupport.confirmFile(friendLink.getAvatarFileId(), FRIEND_LINK_BIZ_TYPE,
                String.valueOf(friendLink.getId()));
        }
        return rows;
    }

    /**
     * 批量删除友链
     * 
     * @param ids 需要删除的友链主键
     * @return 结果
     */
    @Override
    public int deleteFriendLinkByIds(Long[] ids)
    {
        return friendLinkMapper.deleteFriendLinkByIds(ids);
    }

    /**
     * 删除友链信息
     * 
     * @param id 友链主键
     * @return 结果
     */
    @Override
    public int deleteFriendLinkById(Long id)
    {
        return friendLinkMapper.deleteFriendLinkById(id);
    }
}
