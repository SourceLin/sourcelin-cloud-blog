package com.sourcelin.blog.service;

import cn.hutool.dfa.WordTree;
import com.sourcelin.blog.config.BlogAiProperties;
import com.sourcelin.blog.config.ModerationProperties;
import com.sourcelin.blog.constant.ModerationDecisionEnum;
import com.sourcelin.blog.utils.BlogSensitiveWordTool;
import com.sourcelin.blog.vo.ModerationOutcome;
import com.sourcelin.ai.api.domain.AiAbilityInvokeRequest;
import com.sourcelin.ai.api.domain.AiAbilityInvokeResult;
import com.sourcelin.ai.api.service.RemoteAiService;
import com.sourcelin.common.core.constant.SecurityConstants;
import com.sourcelin.common.core.web.domain.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private final BlogAiProperties blogAiProperties;

    @Autowired(required = false)
    private RemoteAiService remoteAiService;

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
            if (blogAiProperties.isEnabled() && remoteAiService != null)
            {
                try
                {
                    Map<String, Object> input = new HashMap<>();
                    input.put("content", rawContent);

                    AiAbilityInvokeRequest invokeRequest = new AiAbilityInvokeRequest();
                    invokeRequest.setRequestId("req_audit_" + UUID.randomUUID().toString().replace("-", ""));
                    invokeRequest.setAppCode("blog");
                    invokeRequest.setDomainCode("comment");
                    invokeRequest.setAbilityCode("comment_risk_check");
                    invokeRequest.setBizId("temp_comment");
                    invokeRequest.setUserId("anonymous");
                    invokeRequest.setInput(input);

                    log.info("Sending comment audit request to AI service. RequestId: {}, Content: {}",
                             invokeRequest.getRequestId(), rawContent);

                    ApiResponse<AiAbilityInvokeResult> response = remoteAiService.invoke(
                            "comment_risk_check", invokeRequest, SecurityConstants.INNER);

                    if (response != null && response.getCode() == 0 && response.getData() != null)
                    {
                        AiAbilityInvokeResult result = response.getData();
                        if (result.isSuccess() && result.getContent() != null)
                        {
                            CommentAuditDecisionParser.Result decision = CommentAuditDecisionParser.parse(result.getContent());
                            log.info("AI comment audit raw content: {}, parsed decision: {}",
                                     result.getContent(), decision.getDecision());
                            if (decision.isPass())
                            {
                                return ModerationOutcome.builder()
                                        .decision(ModerationDecisionEnum.PASS)
                                        .status(1)
                                        .hitKeywords(suspectHits)
                                        .persistModeration(true)
                                        .build();
                            }
                            if (decision.isReject())
                            {
                                return ModerationOutcome.builder()
                                        .decision(ModerationDecisionEnum.REJECT)
                                        .status(2)
                                        .hitKeywords(Collections.singletonList(decision.getReason()))
                                        .persistModeration(true)
                                        .build();
                            }
                            // 决策无法明确判定（isAmbiguous）时，落回下方 AI_REVIEW 人工复审
                            log.warn("AI comment audit decision ambiguous, fallback to manual review. content: {}",
                                     result.getContent());
                        }
                    }
                }
                catch (Exception e)
                {
                    log.error("AI comment audit invoke failed, fallback to manual review", e);
                }
            }

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
