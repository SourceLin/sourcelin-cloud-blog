package com.sourcelin.blog.support;

import com.sourcelin.blog.domain.WebConfig;
import com.sourcelin.blog.domain.WebNavbar;
import com.sourcelin.blog.vo.FrontAboutInfoVO;
import com.sourcelin.blog.vo.FrontNavbarVO;
import com.sourcelin.blog.vo.FrontSiteInfoVO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class FrontBlogProjectionSupport
{
    private static final String NAVBAR_VISIBLE = "0";

    private FrontBlogProjectionSupport()
    {
    }

    public static FrontSiteInfoVO buildSiteInfo(WebConfig config)
    {
        FrontSiteInfoVO vo = new FrontSiteInfoVO();
        if (config == null)
        {
            return vo;
        }
        vo.setAuthor(config.getAuthor());
        vo.setTitle(config.getAuthorInfo());
        vo.setAuthorInfo(config.getAuthorInfo());
        vo.setAvatar(config.getAuthorAvatar());
        vo.setBio(config.getAboutMe());
        vo.setWebName(config.getName());
        vo.setSiteName(config.getName());
        vo.setLogo(config.getLogo());
        vo.setFooter(config.getSummary());
        vo.setRecordNum(config.getRecordNum());
        vo.setGithub(config.getGithub());
        vo.setGitee(config.getGitee());
        vo.setQqNumber(config.getQqNumber());
        vo.setQqGroup(config.getQqGroup());
        vo.setEmail(config.getEmail());
        vo.setWechat(config.getWechat());
        vo.setWebUrl(config.getWebUrl());
        vo.setAliPay(config.getAliPay());
        vo.setWeixinPay(config.getWeixinPay());
        vo.setOpenComment(config.getOpenComment());
        vo.setOpenAdmiration(config.getOpenAdmiration());
        vo.setLoginTypeList(config.getLoginTypeList());
        vo.setBackgroundImage(config.getTouristAvatar());
        vo.setNotices(splitCsv(config.getNotice()));
        vo.setWebTitle(buildWebTitles(config));
        vo.setShowList(parseIntegerList(config.getShowList()));
        return vo;
    }

    public static FrontAboutInfoVO buildAboutInfo(WebConfig config)
    {
        FrontAboutInfoVO vo = new FrontAboutInfoVO();
        if (config == null)
        {
            return vo;
        }
        vo.setAuthor(config.getAuthor());
        vo.setAuthorInfo(config.getAuthorInfo());
        vo.setAvatar(config.getAuthorAvatar());
        vo.setBio(config.getAboutMe());
        vo.setWebName(config.getName());
        vo.setSummary(config.getSummary());
        vo.setKeyword(config.getKeyword());
        vo.setRecordNum(config.getRecordNum());
        vo.setWebUrl(config.getWebUrl());
        vo.setGithub(config.getGithub());
        vo.setGitee(config.getGitee());
        vo.setQqNumber(config.getQqNumber());
        vo.setQqGroup(config.getQqGroup());
        vo.setEmail(config.getEmail());
        vo.setWechat(config.getWechat());
        vo.setAliPay(config.getAliPay());
        vo.setWeixinPay(config.getWeixinPay());
        vo.setOpenComment(config.getOpenComment());
        vo.setOpenAdmiration(config.getOpenAdmiration());
        vo.setShowList(parseIntegerList(config.getShowList()));
        vo.setNotices(splitCsv(config.getNotice()));
        return vo;
    }

    public static List<FrontNavbarVO> buildNavbarTree(List<WebNavbar> navbars)
    {
        if (CollectionUtils.isEmpty(navbars))
        {
            return new ArrayList<>();
        }
        List<WebNavbar> visibleNavbars = new ArrayList<>();
        for (WebNavbar navbar : navbars)
        {
            if (navbar != null && NAVBAR_VISIBLE.equals(navbar.getVisible()))
            {
                visibleNavbars.add(navbar);
            }
        }

        visibleNavbars.sort(Comparator
            .comparing((WebNavbar item) -> item.getOrderNum() == null ? Integer.MAX_VALUE : item.getOrderNum())
            .thenComparing(item -> item.getId() == null ? Long.MAX_VALUE : item.getId()));

        Map<Long, FrontNavbarVO> nodeMap = new HashMap<>(visibleNavbars.size());
        List<FrontNavbarVO> roots = new ArrayList<>();
        for (WebNavbar navbar : visibleNavbars)
        {
            nodeMap.put(navbar.getId(), toNavbarVO(navbar));
        }
        for (WebNavbar navbar : visibleNavbars)
        {
            FrontNavbarVO current = nodeMap.get(navbar.getId());
            Long parentId = navbar.getParentId();
            if (parentId == null || parentId <= 0 || !nodeMap.containsKey(parentId))
            {
                roots.add(current);
                continue;
            }
            nodeMap.get(parentId).getChildren().add(current);
        }
        for (FrontNavbarVO root : roots)
        {
            sortChildren(root);
        }
        return roots;
    }

    private static FrontNavbarVO toNavbarVO(WebNavbar navbar)
    {
        FrontNavbarVO vo = new FrontNavbarVO();
        vo.setId(navbar.getId());
        vo.setName(navbar.getName());
        vo.setPath(navbar.getPath());
        vo.setIcon(navbar.getIcon());
        vo.setSummary(navbar.getSummary());
        vo.setIsFrame(navbar.getIsFrame());
        vo.setType(navbar.getType());
        vo.setOrderNum(navbar.getOrderNum());
        return vo;
    }

    private static void sortChildren(FrontNavbarVO parent)
    {
        if (CollectionUtils.isEmpty(parent.getChildren()))
        {
            return;
        }
        parent.getChildren().sort(Comparator
            .comparing((FrontNavbarVO item) -> item.getOrderNum() == null ? Integer.MAX_VALUE : item.getOrderNum())
            .thenComparing(item -> item.getId() == null ? Long.MAX_VALUE : item.getId()));
        for (FrontNavbarVO child : parent.getChildren())
        {
            sortChildren(child);
        }
    }

    private static List<String> buildWebTitles(WebConfig config)
    {
        List<String> titles = splitCsv(config.getAuthorInfo());
        if (titles.isEmpty() && StringUtils.hasText(config.getSummary()))
        {
            titles.add(config.getSummary().trim());
        }
        if (titles.isEmpty() && StringUtils.hasText(config.getName()))
        {
            titles.add(config.getName().trim());
        }
        return titles;
    }

    private static List<String> splitCsv(String value)
    {
        List<String> result = new ArrayList<>();
        if (!StringUtils.hasText(value))
        {
            return result;
        }
        String[] tokens = value.split("[,锛孿\n]");
        for (String token : tokens)
        {
            if (StringUtils.hasText(token))
            {
                result.add(token.trim());
            }
        }
        return result;
    }

    private static List<Integer> parseIntegerList(String value)
    {
        List<Integer> result = new ArrayList<>();
        for (String token : splitCsv(value))
        {
            try
            {
                result.add(Integer.parseInt(token));
            }
            catch (NumberFormatException ignored)
            {
                // Ignore invalid persisted value and keep the rest.
            }
        }
        return result;
    }
}
