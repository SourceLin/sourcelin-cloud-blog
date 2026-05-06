package com.sourcelin.blog.controller.admin;

import com.sourcelin.blog.config.ModerationProperties;
import com.sourcelin.blog.utils.BlogSensitiveWordTool;
import com.sourcelin.common.core.enums.ResultCode;
import com.sourcelin.common.core.exception.BusinessException;
import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 评论词库 / 机审配置（内存刷新；持久化仍以 Nacos 为准）
 */
@RestController
@RequestMapping("/admin/moderation")
public class CommentModerationAdminController
{
    @Autowired
    private ModerationProperties moderationProperties;

    @Autowired
    private BlogSensitiveWordTool blogSensitiveWordTool;

    @SaCheckPermission(type = "admin", value = "blog:moderation:query")
    @GetMapping("/keyword/current")
    public KeywordCurrentVO currentKeyword()
    {
        ModerationProperties.Keyword k = moderationProperties.getKeyword();
        KeywordCurrentVO body = new KeywordCurrentVO();
        body.setVersion(k.getVersion());
        body.setAllow(k.getAllow());
        body.setBlock(k.getBlock());
        body.setSuspect(k.getSuspect());
        body.setLoadedVersion(blogSensitiveWordTool.getCurrentVersion());
        body.setAutoPassOnClean(moderationProperties.getArticle().isAutoPassOnClean());
        body.setAiEnabled(moderationProperties.isAiEnabled());
        return body;
    }

    @SaCheckPermission(type = "admin", value = "blog:moderation:edit")
    @PostMapping("/keyword/refresh")
    public Void refreshFromProperties()
    {
        blogSensitiveWordTool.rebuildFromProperties();
        return null;
    }

    @SaCheckPermission(type = "admin", value = "blog:moderation:edit")
    @PostMapping("/keyword/save")
    public Void saveKeyword(@RequestBody KeywordSaveBody body)
    {
        if (body == null)
        {
            throw new BusinessException(ResultCode.VALIDATION_ERROR, "参数不能为空");
        }
        Map<String, List<String>> map = new HashMap<>(4);
        if (body.getAllow() != null)
        {
            map.put("allow", body.getAllow());
        }
        if (body.getBlock() != null)
        {
            map.put("block", body.getBlock());
        }
        if (body.getSuspect() != null)
        {
            map.put("suspect", body.getSuspect());
        }
        blogSensitiveWordTool.refreshKeywordCache(map, body.getVersion());
        return null;
    }

    @Data
    public static class KeywordSaveBody
    {
        private Integer version;
        private List<String> allow;
        private List<String> block;
        private List<String> suspect;
    }

    @Data
    public static class KeywordCurrentVO
    {
        private Integer version;
        private List<String> allow;
        private List<String> block;
        private List<String> suspect;
        private Integer loadedVersion;
        private Boolean autoPassOnClean;
        private Boolean aiEnabled;
    }
}
