package com.sourcelin.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 个人头像上传结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAvatarUploadVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String imgUrl;

    private Long avatarFileId;
}
