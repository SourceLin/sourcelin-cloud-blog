package com.sourcelin.blog.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.SensitiveUtil;
import cn.hutool.dfa.WordTree;
import com.sourcelin.blog.config.ModerationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 基于 Hutool {@link WordTree} DFA 的三级词库；过滤动作使用同包 {@link SensitiveUtil#sensitiveFilter(String)} 做展示级校验（单树场景）。
 * 多档词库并存时使用多棵 WordTree，避免 SensitiveUtil 全局 init 覆盖。
 */
@Slf4j
@Component
@RefreshScope
public class BlogSensitiveWordTool
{
    private static final String ZW_PATTERN = "[\\u200B-\\u200D\\uFEFF]";

    private final ModerationProperties properties;

    private volatile WordTree allowTree = new WordTree();
    private volatile WordTree blockTree = new WordTree();
    private volatile WordTree suspectTree = new WordTree();
    private final AtomicInteger loadedVersion = new AtomicInteger(0);

    public BlogSensitiveWordTool(ModerationProperties properties)
    {
        this.properties = properties;
    }

    @PostConstruct
    public void init()
    {
        rebuildFromProperties();
    }

    /**
     * 规范化：去首尾空白、去零宽、全角字母数字转半角（Hutool）、折叠连续空白。
     */
    public String normalizeContent(String content)
    {
        if (content == null)
        {
            return "";
        }
        String s = ReUtil.replaceAll(content, ZW_PATTERN, "");
        s = StrUtil.trim(s);
        s = CharSequenceUtil.replace(s, "\u00A0", " ");
        s = Convert.toDBC(s);
        s = ReUtil.replaceAll(s, "\\s+", " ");
        return StrUtil.trim(s);
    }

    /**
     * 使用 WordTree DFA 检测命中词列表。
     */
    public List<String> matchWordTree(WordTree tree, String normalizedText)
    {
        if (tree == null || StrUtil.isBlank(normalizedText))
        {
            return Collections.emptyList();
        }
        List<String> raw = tree.matchAll(normalizedText, -1, false, true);
        if (raw == null || raw.isEmpty())
        {
            return Collections.emptyList();
        }
        return new ArrayList<>(new LinkedHashSet<>(raw));
    }

    /**
     * 使用 Hutool {@link SensitiveUtil#sensitiveFilter(String)}（依赖全局 init，仅用于低频管理端/单测，已同步避免并发脏写）。
     */
    public synchronized String sensitiveFilterWithBlockWords(String normalizedText, List<String> blockWords)
    {
        if (StrUtil.isBlank(normalizedText))
        {
            return normalizedText;
        }
        List<String> words = blockWords == null ? Collections.emptyList() : blockWords;
        try
        {
            SensitiveUtil.init(words, false);
            return SensitiveUtil.sensitiveFilter(normalizedText);
        }
        finally
        {
            SensitiveUtil.init(Collections.emptyList(), false);
        }
    }

    public List<FoundWord> foundWordsDetail(WordTree tree, String normalizedText)
    {
        if (tree == null || StrUtil.isBlank(normalizedText))
        {
            return Collections.emptyList();
        }
        return tree.matchAllWords(normalizedText, -1, false, true);
    }

    public void rebuildFromProperties()
    {
        ModerationProperties.Keyword kw = properties.getKeyword();
        int ver = kw.getVersion();
        allowTree = buildTree(kw.getAllow());
        blockTree = buildTree(kw.getBlock());
        suspectTree = buildTree(kw.getSuspect());
        loadedVersion.set(ver);
        log.info("评论词库已加载: version={}, allow={}, block={}, suspect={}",
                ver, size(kw.getAllow()), size(kw.getBlock()), size(kw.getSuspect()));
    }

    /**
     * 管理端推送词库时刷新内存（不修改 Nacos 文件）。
     */
    public void refreshKeywordCache(Map<String, List<String>> keywordMap, Integer version)
    {
        ModerationProperties.Keyword kw = properties.getKeyword();
        if (keywordMap != null)
        {
            if (keywordMap.containsKey("allow"))
            {
                kw.setAllow(keywordMap.get("allow"));
            }
            if (keywordMap.containsKey("block"))
            {
                kw.setBlock(keywordMap.get("block"));
            }
            if (keywordMap.containsKey("suspect"))
            {
                kw.setSuspect(keywordMap.get("suspect"));
            }
        }
        if (version != null)
        {
            kw.setVersion(version);
        }
        rebuildFromProperties();
    }

    public int getCurrentVersion()
    {
        return loadedVersion.get();
    }

    public WordTree getAllowTree()
    {
        return allowTree;
    }

    public WordTree getBlockTree()
    {
        return blockTree;
    }

    public WordTree getSuspectTree()
    {
        return suspectTree;
    }

    private static WordTree buildTree(List<String> words)
    {
        WordTree tree = new WordTree();
        if (words == null)
        {
            return tree;
        }
        for (String w : words)
        {
            if (StrUtil.isNotBlank(w))
            {
                tree.addWord(StrUtil.trim(w));
            }
        }
        return tree;
    }

    private static int size(List<?> list)
    {
        return list == null ? 0 : list.size();
    }

    public List<String> getHitKeywordsSummary(String normalizedText)
    {
        Set<String> hits = new LinkedHashSet<>();
        hits.addAll(matchWordTree(allowTree, normalizedText));
        hits.addAll(matchWordTree(blockTree, normalizedText));
        hits.addAll(matchWordTree(suspectTree, normalizedText));
        return hits.stream().filter(StrUtil::isNotBlank).collect(Collectors.toList());
    }
}
