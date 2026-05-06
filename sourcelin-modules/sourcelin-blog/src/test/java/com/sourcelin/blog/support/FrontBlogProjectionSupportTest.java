package com.sourcelin.blog.support;

import com.sourcelin.blog.domain.WebConfig;
import com.sourcelin.blog.domain.WebNavbar;
import com.sourcelin.blog.vo.FrontNavbarVO;
import com.sourcelin.blog.vo.FrontSiteInfoVO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FrontBlogProjectionSupportTest
{
    @Test
    void shouldBuildVisibleNavbarTreeInOrder()
    {
        WebNavbar hidden = navbar(99L, 0L, "闅愯棌菜单", "/hidden", "C", "1", 99);
        WebNavbar parent = navbar(1L, 0L, "棣栭〉", "/", "M", "0", 2);
        WebNavbar childLater = navbar(3L, 1L, "瀵艰埅鐩键綍", "/navigation", "C", "0", 9);
        WebNavbar childFirst = navbar(2L, 1L, "褰掓。", "/archives", "C", "0", 1);
        WebNavbar secondParent = navbar(4L, 0L, "鍏充簬", "/about", "C", "0", 8);

        List<FrontNavbarVO> tree = FrontBlogProjectionSupport.buildNavbarTree(Arrays.asList(
            hidden,
            childLater,
            secondParent,
            parent,
            childFirst
        ));

        assertEquals(2, tree.size());
        assertEquals("棣栭〉", tree.get(0).getName());
        assertEquals("鍏充簬", tree.get(1).getName());
        assertEquals(2, tree.get(0).getChildren().size());
        assertEquals("褰掓。", tree.get(0).getChildren().get(0).getName());
        assertEquals("瀵艰埅鐩键綍", tree.get(0).getChildren().get(1).getName());
        assertFalse(tree.stream().anyMatch(item -> "闅愯棌菜单".equals(item.getName())));
    }

    @Test
    void shouldProjectSiteInfoFromWebConfig()
    {
        WebConfig config = new WebConfig();
        config.setAuthor("SourceLin");
        config.setAuthorInfo("长期写作与工程实践");
        config.setAuthorAvatar("https://cdn.example.com/avatar.png");
        config.setAboutMe("关于站长的介绍");
        config.setName("圆圈博客");
        config.setLogo("https://cdn.example.com/logo.png");
        config.setSummary("分享技术与生活");
        config.setNotice("公告1,公告2");
        config.setRecordNum("ICP-TEST");
        config.setGithub("https://github.com/example");
        config.setGitee("https://gitee.com/example");
        config.setQqNumber("123456");
        config.setEmail("author@example.com");

        FrontSiteInfoVO siteInfo = FrontBlogProjectionSupport.buildSiteInfo(config);

        assertEquals("SourceLin", siteInfo.getAuthor());
        assertEquals("长期写作与工程实践", siteInfo.getTitle());
        assertEquals("https://cdn.example.com/avatar.png", siteInfo.getAvatar());
        assertEquals("关于站长的介绍", siteInfo.getBio());
        assertEquals("圆圈博客", siteInfo.getWebName());
        assertEquals("圆圈博客", siteInfo.getSiteName());
        assertEquals("https://cdn.example.com/logo.png", siteInfo.getLogo());
        assertTrue(siteInfo.getNotices().contains("公告1"));
        assertTrue(siteInfo.getNotices().contains("公告2"));
    }

    private static WebNavbar navbar(Long id, Long parentId, String name, String path, String type, String visible, int orderNum)
    {
        WebNavbar navbar = new WebNavbar();
        navbar.setId(id);
        navbar.setParentId(parentId);
        navbar.setName(name);
        navbar.setPath(path);
        navbar.setType(type);
        navbar.setVisible(visible);
        navbar.setOrderNum(orderNum);
        navbar.setIsFrame("1");
        return navbar;
    }
}
