package com.sourcelin.blog.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sourcelin.blog.constant.ModerationDecisionEnum;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 评论审核结论解析器。
 *
 * <p>LLM 输出不稳定，历史实现用 {@code startsWith("PASS"/"REJECT")} 极易误判。本解析器按以下优先级稳健解析，
 * 并对任何无法明确判定的情况返回 {@code null}，交由上游回落人工/AI 复审，保证安全侧失败。</p>
 *
 * <ol>
 *     <li>结构化 JSON：{@code {"decision":"PASS|REJECT","reason":"..."}}（兼容 ```json 围栏与前后说明文字）；</li>
 *     <li>旧版文本：{@code PASS} / {@code REJECT:原因}（兼容全角冒号）；</li>
 *     <li>关键词兜底：命中 REJECT/拒绝/违规 判违规，仅显式 PASS 或独立的通过/安全结论判通过。</li>
 * </ol>
 */
public final class CommentAuditDecisionParser
{
    /** 从文本中提取首个 JSON 对象片段（允许 LLM 在 JSON 前后附带说明或 ```json 围栏）。 */
    private static final Pattern JSON_OBJECT = Pattern.compile("\\{[\\s\\S]*}");

    private static final int MAX_REASON_LENGTH = 100;

    private CommentAuditDecisionParser()
    {
    }

    public static Result parse(String rawContent)
    {
        if (rawContent == null)
        {
            return Result.ambiguous();
        }
        String content = rawContent.trim();
        if (content.isEmpty())
        {
            return Result.ambiguous();
        }

        Result jsonResult = tryParseJson(content);
        if (jsonResult != null)
        {
            return jsonResult;
        }

        return parseText(content);
    }

    private static Result tryParseJson(String content)
    {
        Matcher matcher = JSON_OBJECT.matcher(content);
        if (!matcher.find())
        {
            return null;
        }
        try
        {
            JSONObject obj = JSON.parseObject(matcher.group());
            if (obj == null)
            {
                return null;
            }
            String decision = firstNonNull(obj.getString("decision"), obj.getString("result"), obj.getString("label"));
            ModerationDecisionEnum parsed = ModerationDecisionEnum.fromCode(decision);
            if (parsed == ModerationDecisionEnum.PASS)
            {
                return Result.pass();
            }
            if (parsed == ModerationDecisionEnum.REJECT)
            {
                String reason = firstNonNull(obj.getString("reason"), obj.getString("message"), obj.getString("msg"));
                return Result.reject(reason);
            }
            return null;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private static Result parseText(String content)
    {
        String upper = content.toUpperCase(Locale.ROOT);
        boolean hasReject = upper.contains("REJECT") || content.contains("拒绝") || content.contains("违规") || content.contains("不通过") || content.contains("不安全");
        boolean hasPass = upper.matches("PASS(?:\\s*[:：].*)?") || "通过".equals(content) || "安全".equals(content);

        // 同时命中时按"安全侧从严"，判为违规
        if (hasReject)
        {
            return Result.reject(extractReason(content));
        }
        if (hasPass)
        {
            return Result.pass();
        }
        return Result.ambiguous();
    }

    private static String extractReason(String content)
    {
        int idx = -1;
        int colon = content.indexOf(':');
        int fullColon = content.indexOf('：');
        if (colon >= 0 && fullColon >= 0)
        {
            idx = Math.min(colon, fullColon);
        }
        else
        {
            idx = Math.max(colon, fullColon);
        }
        if (idx < 0 || idx + 1 >= content.length())
        {
            return null;
        }
        String reason = content.substring(idx + 1).trim();
        if (reason.isEmpty())
        {
            return null;
        }
        return reason.length() > MAX_REASON_LENGTH ? reason.substring(0, MAX_REASON_LENGTH) : reason;
    }

    private static String firstNonNull(String... values)
    {
        if (values == null)
        {
            return null;
        }
        for (String v : values)
        {
            if (v != null && !v.trim().isEmpty())
            {
                return v.trim();
            }
        }
        return null;
    }

    /** 解析结果：决策为 null 表示无法判定，交回落复审。 */
    public static final class Result
    {
        private static final String DEFAULT_REJECT_REASON = "内容违规";

        private final ModerationDecisionEnum decision;
        private final String reason;

        private Result(ModerationDecisionEnum decision, String reason)
        {
            this.decision = decision;
            this.reason = reason;
        }

        static Result pass()
        {
            return new Result(ModerationDecisionEnum.PASS, null);
        }

        static Result reject(String reason)
        {
            String r = (reason == null || reason.trim().isEmpty()) ? DEFAULT_REJECT_REASON : reason.trim();
            return new Result(ModerationDecisionEnum.REJECT, r);
        }

        static Result ambiguous()
        {
            return new Result(null, null);
        }

        public boolean isPass()
        {
            return decision == ModerationDecisionEnum.PASS;
        }

        public boolean isReject()
        {
            return decision == ModerationDecisionEnum.REJECT;
        }

        public boolean isAmbiguous()
        {
            return decision == null;
        }

        public ModerationDecisionEnum getDecision()
        {
            return decision;
        }

        public String getReason()
        {
            return reason;
        }
    }
}
