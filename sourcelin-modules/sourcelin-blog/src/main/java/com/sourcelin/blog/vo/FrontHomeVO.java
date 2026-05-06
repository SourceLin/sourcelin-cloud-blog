package com.sourcelin.blog.vo;

import com.sourcelin.blog.domain.Category;
import com.sourcelin.blog.domain.Tag;
import com.sourcelin.common.core.web.domain.response.PageResult;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 前台首页聚合数据。
 */
@Data
public class FrontHomeVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private FrontSiteInfoVO siteInfo;

    private PageResult<ArticleVO> latest;

    private List<ArticleVO> recommend = new ArrayList<>();

    private List<ArticleVO> carousel = new ArrayList<>();

    private List<Category> categories = new ArrayList<>();

    private List<Tag> tags = new ArrayList<>();
}
