package com.sourcelin.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FrontFollowActionVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long followId;
    private Boolean followed;
}
