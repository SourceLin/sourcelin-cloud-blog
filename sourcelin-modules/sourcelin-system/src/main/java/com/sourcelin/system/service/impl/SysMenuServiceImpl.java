package com.sourcelin.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sourcelin.system.domain.vo.MetaVo;
import com.sourcelin.system.domain.vo.RouterVo;
import com.sourcelin.system.domain.vo.TreeSelect;
import com.sourcelin.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sourcelin.common.core.constant.Constants;
import com.sourcelin.common.core.constant.UserConstants;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.security.utils.SecurityUtils;
import com.sourcelin.system.api.domain.SysRole;
import com.sourcelin.system.api.domain.SysUser;
import com.sourcelin.system.domain.SysMenu;
import com.sourcelin.system.mapper.SysMenuMapper;
import com.sourcelin.system.mapper.SysRoleMapper;
import com.sourcelin.system.mapper.SysRoleMenuMapper;

/**
 * 菜单 业务层处理
 * 
 * @author sourcelin
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService
{
    private static final Logger log = LoggerFactory.getLogger(SysMenuServiceImpl.class);

    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    public static final Long MENU_ROOT_ID = 0L;

    private static final Pattern ROUTE_NAME_SEGMENT_SPLIT = Pattern.compile("[^A-Za-z0-9]+");

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 根据用户查询系统菜单列表
     * 
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(Long userId)
    {
        return selectMenuList(new SysMenu(), userId);
    }

    /**
     * 查询系统菜单列表
     * 
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId)
    {
        List<SysMenu> menuList = null;
        // 管理员显示所有菜单信息
        if (SysUser.isAdmin(userId))
        {
            menuList = menuMapper.selectMenuList(menu);
        }
        else
        {
            menu.getParams().put("userId", userId);
            menuList = menuMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId)
    {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms)
        {
            if (StringUtils.isNotEmpty(perm))
            {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据角色ID查询权限
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Long roleId)
    {
        List<String> perms = menuMapper.selectMenuPermsByRoleId(roleId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms)
        {
            if (StringUtils.isNotEmpty(perm))
            {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户ID查询菜单
     * 
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId)
    {
        List<SysMenu> menus = null;
        if (SecurityUtils.isAdmin(userId))
        {
            menus = menuMapper.selectMenuTreeAll();
        }
        else
        {
            menus = menuMapper.selectMenuTreeByUserId(userId);
        }
        return getChildPerms(menus, 0);
    }

    /**
     * 根据角色ID查询菜单树信息
     * 
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId)
    {
        SysRole role = roleMapper.selectRoleById(roleId);
        return menuMapper.selectMenuListByRoleId(roleId, role.isMenuCheckStrictly());
    }

    /**
     * 构建前端路由所需要的菜单
     * 
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus)
    {
        return buildMenus(menus, StringUtils.EMPTY);
    }

    private List<RouterVo> buildMenus(List<SysMenu> menus, String parentRouteKey)
    {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenu menu : menus)
        {
            String currentRouteKey = resolveRouteKey(menu, parentRouteKey);
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu.getRouteName(), currentRouteKey));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQuery());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
            List<SysMenu> cMenus = menu.getChildren();
            if (StringUtils.isNotEmpty(cMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType()))
            {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus, currentRouteKey));
            }
            else if (isMenuFrame(menu))
            {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(getRouteName(menu.getRouteName(), currentRouteKey));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
                children.setQuery(menu.getQuery());
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            else if (menu.getParentId().intValue() == MENU_ROOT_ID.intValue() && isInnerLink(menu))
            {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(getRouteName(menu.getRouteName(), currentRouteKey));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 构建前端所需要树结构
     * 
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus)
    {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext();)
        {
            SysMenu menu = (SysMenu) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId()))
            {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     * 
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus)
    {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据菜单ID查询信息
     * 
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu selectMenuById(Long menuId)
    {
        return menuMapper.selectMenuById(menuId);
    }

    /**
     * 是否存在菜单子节点
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId)
    {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0;
    }

    /**
     * 查询菜单使用数量
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId)
    {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0;
    }

    /**
     * 新增保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int insertMenu(SysMenu menu)
    {
        return menuMapper.insertMenu(menu);
    }

    /**
     * 修改保存菜单信息
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(SysMenu menu)
    {
        return menuMapper.updateMenu(menu);
    }

    /**
     * 删除菜单管理信息
     * 
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(Long menuId)
    {
        return menuMapper.deleteMenuById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public boolean checkMenuNameUnique(SysMenu menu)
    {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId());
        if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验路由名称是否唯一（与 Sourcelin 3.6.8 一致：同级 path、根目录 path、全局 routeName）
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public boolean checkRouteConfigUnique(SysMenu menu)
    {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        Long parentId = StringUtils.isNull(menu.getParentId()) ? -1L : menu.getParentId();
        String path = menu.getPath();
        List<SysMenu> routeMenus = menuMapper.selectMenuList(new SysMenu()).stream()
                .filter(item -> UserConstants.TYPE_DIR.equals(item.getMenuType()) || UserConstants.TYPE_MENU.equals(item.getMenuType()))
                .collect(Collectors.toList());
        upsertCurrentMenu(routeMenus, menu);
        Map<Long, SysMenu> menuMap = buildMenuMap(routeMenus);
        String routeName = getRouteName(menu.getRouteName(), resolveRouteKey(menu, menuMap));
        for (SysMenu sysMenu : routeMenus)
        {
            Long dbMenuId = StringUtils.isNull(sysMenu.getMenuId()) ? -1L : sysMenu.getMenuId();
            if (dbMenuId.longValue() != menuId.longValue())
            {
                Long dbParentId = StringUtils.isNull(sysMenu.getParentId()) ? -1L : sysMenu.getParentId();
                String dbPath = sysMenu.getPath();
                String dbRouteName = getRouteName(sysMenu.getRouteName(), resolveRouteKey(sysMenu, menuMap));
                if (StringUtils.equalsAnyIgnoreCase(path, dbPath) && parentId.longValue() == dbParentId.longValue())
                {
                    log.warn("[同级路由冲突] 同级下已存在相同路由路径 '{}'，冲突菜单：{}", dbPath, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
                else if (StringUtils.equalsAnyIgnoreCase(path, dbPath) && parentId.longValue() == MENU_ROOT_ID.longValue())
                {
                    log.warn("[根目录路由冲突] 根目录下路由 '{}' 必须唯一，已被菜单 '{}' 占用", path, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
                else if (StringUtils.equalsAnyIgnoreCase(routeName, dbRouteName))
                {
                    log.warn("[路由名称冲突] 路由名称 '{}' 需全局唯一，已被菜单 '{}' 使用", routeName, sysMenu.getMenuName());
                    return UserConstants.NOT_UNIQUE;
                }
            }
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 获取路由名称
     * 
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenu menu)
    {
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu))
        {
            return StringUtils.EMPTY;
        }
        return getRouteName(menu.getRouteName(), resolveRouteKey(menu, StringUtils.EMPTY));
    }

    /**
     * 获取路由名称，如没有配置路由名称则取路由地址
     * 
     * @param name 路由名称
     * @param path 路由地址
     * @return 路由名称（首字母大写）
     */
    public String getRouteName(String name, String path)
    {
        if (StringUtils.isNotEmpty(name))
        {
            return StringUtils.capitalize(name.trim());
        }
        return toPascalCase(path);
    }

    private String resolveRouteKey(SysMenu menu, String parentRouteKey)
    {
        if (menu == null)
        {
            return StringUtils.EMPTY;
        }
        if (StringUtils.isNotEmpty(menu.getRouteName()))
        {
            return menu.getRouteName().trim();
        }
        String segment = normalizeRoutePathSegment(menu.getPath());
        if (StringUtils.isEmpty(segment))
        {
            return parentRouteKey;
        }
        if (StringUtils.isEmpty(parentRouteKey))
        {
            return segment;
        }
        return parentRouteKey + "/" + segment;
    }

    private String resolveRouteKey(SysMenu menu, Map<Long, SysMenu> menuMap)
    {
        if (menu == null)
        {
            return StringUtils.EMPTY;
        }
        if (StringUtils.isNotEmpty(menu.getRouteName()))
        {
            return menu.getRouteName().trim();
        }
        List<String> segments = new ArrayList<>();
        appendRoutePathSegments(menu, menuMap, new HashSet<>(), segments);
        if (segments.isEmpty())
        {
            return normalizeRoutePathSegment(menu.getPath());
        }
        return String.join("/", segments);
    }

    private void appendRoutePathSegments(SysMenu menu, Map<Long, SysMenu> menuMap, Set<Long> visited, List<String> segments)
    {
        if (menu == null)
        {
            return;
        }
        Long menuId = menu.getMenuId();
        if (menuId != null)
        {
            if (visited.contains(menuId))
            {
                return;
            }
            visited.add(menuId);
        }
        Long parentId = menu.getParentId();
        if (parentId != null && !MENU_ROOT_ID.equals(parentId))
        {
            appendRoutePathSegments(menuMap.get(parentId), menuMap, visited, segments);
        }
        String currentSegment = normalizeRoutePathSegment(menu.getPath());
        if (StringUtils.isNotEmpty(currentSegment))
        {
            segments.add(currentSegment);
        }
    }

    private void upsertCurrentMenu(List<SysMenu> routeMenus, SysMenu currentMenu)
    {
        for (int i = 0; i < routeMenus.size(); i++)
        {
            SysMenu item = routeMenus.get(i);
            if (item.getMenuId() != null && currentMenu.getMenuId() != null
                    && item.getMenuId().longValue() == currentMenu.getMenuId().longValue())
            {
                routeMenus.set(i, currentMenu);
                return;
            }
        }
        routeMenus.add(currentMenu);
    }

    private Map<Long, SysMenu> buildMenuMap(List<SysMenu> routeMenus)
    {
        Map<Long, SysMenu> menuMap = new HashMap<>();
        for (SysMenu item : routeMenus)
        {
            if (item.getMenuId() != null)
            {
                menuMap.put(item.getMenuId(), item);
            }
        }
        return menuMap;
    }

    private String normalizeRoutePathSegment(String rawPath)
    {
        if (StringUtils.isEmpty(rawPath))
        {
            return StringUtils.EMPTY;
        }
        String normalized = rawPath.trim();
        if (StringUtils.isEmpty(normalized))
        {
            return StringUtils.EMPTY;
        }
        if (StringUtils.ishttp(normalized))
        {
            normalized = innerLinkReplaceEach(normalized);
        }
        normalized = normalized.replace("\\", "/");
        normalized = StringUtils.strip(normalized, "/");
        return normalized;
    }

    private String toPascalCase(String rawValue)
    {
        if (StringUtils.isEmpty(rawValue))
        {
            return StringUtils.EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        String[] segments = ROUTE_NAME_SEGMENT_SPLIT.split(rawValue);
        for (String segment : segments)
        {
            if (StringUtils.isEmpty(segment))
            {
                continue;
            }
            builder.append(StringUtils.capitalize(segment));
        }
        if (builder.length() > 0)
        {
            return builder.toString();
        }
        return StringUtils.capitalize(rawValue.replace("/", ""));
    }

    /**
     * 获取路由地址
     * 
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu)
    {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != MENU_ROOT_ID.intValue() && isInnerLink(menu))
        {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (MENU_ROOT_ID.intValue() == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
                && UserConstants.NO_FRAME.equals(menu.getIsFrame()))
        {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu))
        {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     * 
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenu menu)
    {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu))
        {
            component = menu.getComponent();
        }
        else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu))
        {
            component = UserConstants.INNER_LINK;
        }
        else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu))
        {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SysMenu menu)
    {
        return menu.getParentId().longValue() == MENU_ROOT_ID.longValue() && UserConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 是否为内链组件
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(SysMenu menu)
    {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtils.ishttp(menu.getPath());
    }

    /**
     * 是否为parent_view组件
     * 
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(SysMenu menu)
    {
        return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }

    /**
     * 根据父节点的ID获取所有子节点
     * 
     * @param list 分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId)
    {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext();)
        {
            SysMenu t = (SysMenu) iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId)
            {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     * 
     * @param list
     * @param t
     */
    private void recursionFn(List<SysMenu> list, SysMenu t)
    {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t)
    {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext())
        {
            SysMenu n = (SysMenu) it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t)
    {
        return getChildList(list, t).size() > 0;
    }

    /**
     * 内链域名特殊字符替换
     * 
     * @return
     */
    public String innerLinkReplaceEach(String path)
    {
        return StringUtils.replaceEach(path, new String[] { Constants.HTTP, Constants.HTTPS, Constants.WWW, "." },
                new String[] { "", "", "", "/" });
    }
}
