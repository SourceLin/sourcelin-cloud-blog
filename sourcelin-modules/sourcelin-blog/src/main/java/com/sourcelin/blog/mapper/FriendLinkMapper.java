package com.sourcelin.blog.mapper;

import com.sourcelin.blog.domain.FriendLink;

import java.util.List;

/**
 * 友链Mapper接口
 * 
 * @author sourcelin
 * @date 2026-01-18
 */
public interface FriendLinkMapper 
{
    /**
     * 查询友链
     * 
     * @param id 友链主键
     * @return 友链
     */
    public FriendLink selectFriendLinkById(Long id);

    /**
     * 查询友链列表
     * 
     * @param friendLink 友链
     * @return 友链集合
     */
    public List<FriendLink> selectFriendLinkList(FriendLink friendLink);

    /**
     * 新增友链
     * 
     * @param friendLink 友链
     * @return 结果
     */
    public int insertFriendLink(FriendLink friendLink);

    /**
     * 修改友链
     * 
     * @param friendLink 友链
     * @return 结果
     */
    public int updateFriendLink(FriendLink friendLink);

    /**
     * 删除友链
     * 
     * @param id 友链主键
     * @return 结果
     */
    public int deleteFriendLinkById(Long id);

    /**
     * 批量删除友链
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFriendLinkByIds(Long[] ids);
}
