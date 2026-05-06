package com.sourcelin.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 角色-菜单树结构。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenuTreeVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    private List<Long> checkedKeys;

    private List<TreeSelect> menus;
}

