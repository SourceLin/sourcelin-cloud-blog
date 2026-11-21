package com.sourcelin.blog.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentAuditDecisionParserTest
{
    @Test
    void jsonPass()
    {
        CommentAuditDecisionParser.Result r = CommentAuditDecisionParser.parse("{\"decision\":\"PASS\",\"reason\":\"\"}");
        assertTrue(r.isPass());
        assertFalse(r.isReject());
    }

    @Test
    void jsonReject()
    {
        CommentAuditDecisionParser.Result r = CommentAuditDecisionParser.parse("{\"decision\":\"REJECT\",\"reason\":\"人身攻击\"}");
        assertTrue(r.isReject());
        assertEquals("人身攻击", r.getReason());
    }

    @Test
    void jsonRejectWithoutReasonUsesDefault()
    {
        CommentAuditDecisionParser.Result r = CommentAuditDecisionParser.parse("{\"decision\":\"REJECT\"}");
        assertTrue(r.isReject());
        assertEquals("内容违规", r.getReason());
    }

    @Test
    void fencedJsonWithSurroundingText()
    {
        String content = "好的，审核结果如下：\n```json\n{\"decision\": \"REJECT\", \"reason\": \"疑似广告引流\"}\n```";
        CommentAuditDecisionParser.Result r = CommentAuditDecisionParser.parse(content);
        assertTrue(r.isReject());
        assertEquals("疑似广告引流", r.getReason());
    }

    @Test
    void legacyPass()
    {
        assertTrue(CommentAuditDecisionParser.parse("PASS").isPass());
        assertTrue(CommentAuditDecisionParser.parse("PASS：内容安全").isPass());
    }

    @Test
    void legacyRejectHalfWidthColon()
    {
        CommentAuditDecisionParser.Result r = CommentAuditDecisionParser.parse("REJECT:包含人身攻击");
        assertTrue(r.isReject());
        assertEquals("包含人身攻击", r.getReason());
    }

    @Test
    void legacyRejectFullWidthColon()
    {
        CommentAuditDecisionParser.Result r = CommentAuditDecisionParser.parse("REJECT：疑似广告引流");
        assertTrue(r.isReject());
        assertEquals("疑似广告引流", r.getReason());
    }

    @Test
    void chineseKeywordReject()
    {
        assertTrue(CommentAuditDecisionParser.parse("该评论存在违规内容").isReject());
    }

    @Test
    void ambiguousGoesNull()
    {
        assertTrue(CommentAuditDecisionParser.parse("我不太确定").isAmbiguous());
        assertTrue(CommentAuditDecisionParser.parse("").isAmbiguous());
        assertTrue(CommentAuditDecisionParser.parse(null).isAmbiguous());
    }

    @Test
    void rejectTakesPrecedenceWhenBothPresent()
    {
        // 同时出现 PASS 与 REJECT 时，按安全侧从严判违规
        CommentAuditDecisionParser.Result r = CommentAuditDecisionParser.parse("不是 PASS，应当 REJECT：辱骂");
        assertTrue(r.isReject());
    }

    @Test void negatedOrQuotedPassDoesNotAutomaticallyApprove() {
        assertFalse(CommentAuditDecisionParser.parse("NOT PASS").isPass());
        assertFalse(CommentAuditDecisionParser.parse("不能确认内容安全").isPass());
        assertFalse(CommentAuditDecisionParser.parse("不安全").isPass());
        assertFalse(CommentAuditDecisionParser.parse("{\"decision\":\"UNKNOWN\",\"reason\":\"可能 PASS\"}").isPass());
    }
}
