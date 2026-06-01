package com.sourcelin.blog.service;

import com.sourcelin.blog.domain.FriendLink;

import java.util.List;

/**
 * 友链Service接口
 * 
 * @author sourcelin
 * @date 2026-01-18
 */
public interface IFriendLinkService 
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
     * 批量删除友链
     * 
     * @param ids 需要删除的友链主键集合
     * @return 结果
     */
    public int deleteFriendLinkByIds(Long[] ids);

    /**
     * 删除友链信息
     * 
     * @param id 友链主键
     * @return 结果
     */
    public int deleteFriendLinkById(Long id);
}
