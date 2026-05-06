package com.sourcelin.blog.service;

import cn.hutool.dfa.WordTree;
import com.sourcelin.blog.config.ModerationProperties;
import com.sourcelin.blog.constant.ModerationDecisionEnum;
import com.sourcelin.blog.utils.BlogSensitiveWordTool;
import com.sourcelin.blog.vo.ModerationOutcome;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 机审管道：白名单优先，其次黑名单、灰名单；结合 autoPassOnClean 应急开关。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ModerationPipeline
{
    private final ModerationProperties moderationProperties;
    private final BlogSensitiveWordTool sensitiveWordTool;

    public ModerationOutcome moderate(String rawContent)
    {
        String text = sensitiveWordTool.normalizeContent(rawContent);
        boolean autoPass = moderationProperties.getArticle().isAutoPassOnClean();
        WordTree allowTree = sensitiveWordTool.getAllowTree();
        WordTree blockTree = sensitiveWordTool.getBlockTree();
        WordTree suspectTree = sensitiveWordTool.getSuspectTree();

        List<String> allowHits = sensitiveWordTool.matchWordTree(allowTree, text);
        if (!allowHits.isEmpty())
        {
            if (autoPass)
            {
                return ModerationOutcome.builder()
                        .decision(ModerationDecisionEnum.PASS)
                        .status(1)
                        .hitKeywords(allowHits)
                        .persistModeration(true)
                        .build();
            }
            return ModerationOutcome.builder()
                    .decision(ModerationDecisionEnum.REVIEW)
                    .status(0)
                    .hitKeywords(allowHits)
                    .persistModeration(true)
                    .build();
        }

        List<String> blockHits = sensitiveWordTool.matchWordTree(blockTree, text);
        if (!blockHits.isEmpty())
        {
            return ModerationOutcome.builder()
                    .decision(ModerationDecisionEnum.REJECT)
                    .status(2)
                    .hitKeywords(blockHits)
                    .persistModeration(true)
                    .build();
        }

        List<String> suspectHits = sensitiveWordTool.matchWordTree(suspectTree, text);
        if (!suspectHits.isEmpty())
        {
            return ModerationOutcome.builder()
                    .decision(ModerationDecisionEnum.AI_REVIEW)
                    .status(0)
                    .hitKeywords(suspectHits)
                    .persistModeration(true)
                    .build();
        }

        if (!autoPass)
        {
            return ModerationOutcome.builder()
                    .decision(ModerationDecisionEnum.REVIEW)
                    .status(0)
                    .hitKeywords(Collections.emptyList())
                    .persistModeration(true)
                    .build();
        }

        return ModerationOutcome.builder()
                .decision(ModerationDecisionEnum.PASS)
                .status(1)
                .hitKeywords(Collections.emptyList())
                .persistModeration(true)
                .build();
    }
}
