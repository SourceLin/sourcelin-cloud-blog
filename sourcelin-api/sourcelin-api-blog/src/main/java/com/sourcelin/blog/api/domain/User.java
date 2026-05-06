package com.sourcelin.blog.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sourcelin.common.core.annotation.Excel;
import com.sourcelin.common.core.web.domain.BaseEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class User extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "User ID", cellType = Excel.ColumnType.NUMERIC, prompt = "User identifier")
    private Long id;

    @Excel(name = "Username")
    private String username;

    @Excel(name = "Nickname")
    private String nickname;

    private String password;

    @Excel(name = "User Type")
    private Integer userType;

    @Excel(name = "Email")
    private String email;

    @Excel(name = "Phone")
    private String phone;

    private String avatar;

    private Long avatarFileId;

    @Excel(name = "Sex")
    private Integer sex;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    private String introduction;

    @Excel(name = "Status", readConverterExp = "0=normal,1=disabled")
    private Long userStatus;

    @Excel(name = "Praise")
    private Long praise;

    @Excel(name = "Follow")
    private Long follow;

    @Excel(name = "Login IP", type = Excel.Type.EXPORT)
    private String loginIp;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "Login Time", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT)
    private Date loginDate;

    private String delFlag;

    private String uuid;

    private String bindQqId;

    private String bindWechatId;

    private String bindSinaId;

    private String bindAlipayId;

    private String realnameAuth;

    private String openId;

    private String sessionKey;

    private Long articleCount;

    private Long followerCount;
}
