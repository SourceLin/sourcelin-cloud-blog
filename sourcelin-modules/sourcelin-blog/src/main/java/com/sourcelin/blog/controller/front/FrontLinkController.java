package com.sourcelin.blog.controller.front;

import com.sourcelin.blog.domain.FriendLink;
import com.sourcelin.blog.service.IFriendLinkService;
import com.sourcelin.blog.vo.FriendLinkApplyRequest;
import com.sourcelin.blog.vo.FriendLinkPublicVO;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import com.sourcelin.common.core.utils.StringUtils;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import com.sourcelin.common.core.web.domain.response.ListResult;
import com.sourcelin.file.api.domain.SysFile;
import com.sourcelin.file.api.service.RemoteFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/front/links")
public class FrontLinkController
{
    private static final int NAME_MAX = 50;
    private static final int URL_MAX = 90;
    private static final int DESCRIPTION_MAX = 255;
    private static final int AVATAR_MAX = 255;
    private static final int EMAIL_MAX = 75;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    @Autowired
    private IFriendLinkService friendLinkService;

    @Autowired
    private RemoteFileService remoteFileService;

    @GetMapping
    public ListResult<FriendLinkPublicVO> list()
    {
        FriendLink filter = new FriendLink();
        filter.setStatus(1);
        List<FriendLink> rows = friendLinkService.selectFriendLinkList(filter);
        return ListResult.of(toPublicFriendLinks(rows));
    }

    private List<FriendLinkPublicVO> toPublicFriendLinks(List<FriendLink> rows)
    {
        List<FriendLinkPublicVO> list = new ArrayList<FriendLinkPublicVO>();
        if (rows == null)
        {
            return list;
        }
        for (FriendLink row : rows)
        {
            FriendLinkPublicVO vo = new FriendLinkPublicVO();
            vo.setId(row.getId());
            vo.setName(row.getName());
            vo.setUrl(row.getUrl());
            vo.setDescription(row.getDescription());
            String avatar = StringUtils.trim(row.getAvatar());
            if (StringUtils.isEmpty(avatar) && row.getAvatarFileId() != null && row.getAvatarFileId() > 0)
            {
                avatar = resolveAvatarByFileId(row.getAvatarFileId());
            }
            vo.setAvatar(StringUtils.isEmpty(avatar) ? null : avatar);
            list.add(vo);
        }
        return list;
    }

    private String resolveAvatarByFileId(Long fileId)
    {
        try
        {
            ApiResponse<SysFile> result = remoteFileService.getFileMetadata(fileId);
            if (result != null
                && ResultCode.SUCCESS.getCode().equals(result.getCode())
                && result.getData() != null
                && StringUtils.isNotEmpty(result.getData().getUrl()))
            {
                return result.getData().getUrl().trim();
            }
        }
        catch (Exception ignored)
        {
        }
        return null;
    }

    /**
     * 匿名申请友链，写入为「申请」状态，待后台审核。
     */
    @PostMapping("/apply")
    public Void apply(@RequestBody FriendLinkApplyRequest body)
    {
        if (body == null)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "请求体不能为空");
        }
        String name = StringUtils.trim(body.getName());
        String urlRaw = StringUtils.trim(body.getUrl());
        String email = StringUtils.trim(body.getEmail());
        String description = StringUtils.trim(body.getDescription());
        String avatar = StringUtils.trim(body.getAvatar());

        if (StringUtils.isEmpty(name) || name.length() > NAME_MAX)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "网站名称不能为空且不超过 " + NAME_MAX + " 字");
        }
        if (StringUtils.isEmpty(urlRaw))
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "网站地址不能为空");
        }
        if (StringUtils.isEmpty(email) || email.length() > EMAIL_MAX)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "联系邮箱不能为空且不超过 " + EMAIL_MAX + " 字");
        }
        if (!EMAIL_PATTERN.matcher(email).matches())
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "邮箱格式不正确");
        }

        String normalizedUrl = normalizeHttpUrl(urlRaw);
        if (normalizedUrl == null || normalizedUrl.length() > URL_MAX)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "网站地址需为有效 http(s) 链接，且长度不超过 " + URL_MAX);
        }

        if (description != null && description.length() > DESCRIPTION_MAX)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "网站描述不超过 " + DESCRIPTION_MAX + " 字");
        }
        if (description != null && description.isEmpty())
        {
            description = null;
        }
        if (avatar != null && !avatar.isEmpty())
        {
            if (avatar.length() > AVATAR_MAX)
            {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "头像链接不超过 " + AVATAR_MAX + " 字");
            }
            String avatarUrl = normalizeHttpUrl(avatar);
            if (avatarUrl == null)
            {
                throw new BusinessException(ResultCode.VALIDATION_ERROR, "头像需为有效 http(s) 图片链接");
            }
            avatar = avatarUrl;
        }
        else
        {
            avatar = null;
        }

        FriendLink row = new FriendLink();
        row.setName(name);
        row.setUrl(normalizedUrl);
        row.setDescription(description);
        row.setAvatar(avatar);
        row.setEmail(email);
        row.setStatus(0);
        row.setCategory(3);
        row.setOrderNum(0);

        int n = friendLinkService.insertFriendLink(row);
        if (n <= 0)
        {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "提交失败，请稍后重试");
        }
        return null;
    }

    private static String normalizeHttpUrl(String raw)
    {
        String s = raw.trim();
        if (!StringUtils.startsWithIgnoreCase(s, "http://") && !StringUtils.startsWithIgnoreCase(s, "https://"))
        {
            s = "https://" + s;
        }
        try
        {
            URI uri = new URI(s);
            if (!"http".equalsIgnoreCase(uri.getScheme()) && !"https".equalsIgnoreCase(uri.getScheme()))
            {
                return null;
            }
            if (StringUtils.isEmpty(uri.getHost()))
            {
                return null;
            }
            return uri.toURL().toString();
        }
        catch (URISyntaxException | MalformedURLException ex)
        {
            return null;
        }
    }
}
