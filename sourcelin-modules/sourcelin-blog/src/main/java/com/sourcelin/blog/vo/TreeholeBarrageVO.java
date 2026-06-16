package com.sourcelin.blog.vo;

import java.io.Serializable;

/**
 * 树洞弹幕极简传输对象 VO
 */
public class TreeholeBarrageVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;
    private String msg;
    private String avatar;
    private String nickname;

    public TreeholeBarrageVO()
    {
    }

    public TreeholeBarrageVO(Long id, String msg, String avatar, String nickname)
    {
        this.id = id;
        this.msg = msg;
        this.avatar = avatar;
        this.nickname = nickname;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }
}
