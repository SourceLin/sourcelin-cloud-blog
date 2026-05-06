package com.sourcelin.blog.mapper;

import java.util.List;
import com.sourcelin.blog.domain.WebNavbar;

/**
 * 门户导航栏Mapper接口
 * 
 * @author sourcelin
 * @date 2023-11-06
 */
public interface WebNavbarMapper 
{
    /**
     * 查询门户导航栏
     * 
     * @param id 门户导航栏主键
     * @return 门户导航栏
     */
    public WebNavbar selectWebNavbarById(Long id);

    /**
     * 查询门户导航栏列表
     * 
     * @param webNavbar 门户导航栏
     * @return 门户导航栏集合
     */
    public List<WebNavbar> selectWebNavbarList(WebNavbar webNavbar);

    /**
     * 新增门户导航栏
     * 
     * @param webNavbar 门户导航栏
     * @return 结果
     */
    public int insertWebNavbar(WebNavbar webNavbar);

    /**
     * 修改门户导航栏
     * 
     * @param webNavbar 门户导航栏
     * @return 结果
     */
    public int updateWebNavbar(WebNavbar webNavbar);

    /**
     * 删除门户导航栏
     * 
     * @param id 门户导航栏主键
     * @return 结果
     */
    public int deleteWebNavbarById(Long id);

    /**
     * 批量删除门户导航栏
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWebNavbarByIds(Long[] ids);
}
