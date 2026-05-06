package com.sourcelin.blog.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class FrontNavbarVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String path;

    private String icon;

    private String summary;

    private String isFrame;

    private String type;

    private Integer orderNum;

    private List<FrontNavbarVO> children = new ArrayList<>();
}
