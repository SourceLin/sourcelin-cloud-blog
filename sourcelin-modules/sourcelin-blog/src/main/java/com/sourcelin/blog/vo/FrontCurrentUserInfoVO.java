package com.sourcelin.blog.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FrontCurrentUserInfoVO extends FrontUserInfoVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    private List<String> roles = new ArrayList<String>();
    private List<String> permissions = new ArrayList<String>();
}
