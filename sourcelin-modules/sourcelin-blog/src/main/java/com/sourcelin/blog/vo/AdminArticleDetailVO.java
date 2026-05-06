package com.sourcelin.blog.vo;

import com.sourcelin.blog.domain.Article;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AdminArticleDetailVO extends Article implements Serializable
{
    private static final long serialVersionUID = 1L;

    private List<Long> tagIds = new ArrayList<Long>();
}
