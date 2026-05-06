package com.sourcelin.blog.vo;

import com.sourcelin.blog.domain.Article;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class FrontArticleDetailVO extends Article implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long commentCount;
    private Boolean readFull;
    private Boolean needLogin;
    private String unlockHint;
    private Boolean isCollected;
    private Long collectId;
    private Boolean isFollowed;
    private Long followId;
    private Boolean isLike;
}
