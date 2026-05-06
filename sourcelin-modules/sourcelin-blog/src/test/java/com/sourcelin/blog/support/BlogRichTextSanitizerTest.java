package com.sourcelin.blog.support;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlogRichTextSanitizerTest
{
    private final BlogRichTextSanitizer sanitizer = new BlogRichTextSanitizer();

    @Test
    void shouldRemoveScriptTagsFromContent()
    {
        String sanitized = sanitizer.sanitizeContent("<p>正文</p><script>alert(1)</script>");
        assertTrue(sanitized.contains("<p>正文</p>"));
        assertFalse(sanitized.contains("<script"));
    }

    @Test
    void shouldRemoveDangerousImageEventHandler()
    {
        String sanitized = sanitizer.sanitizeContent("<p><img src=\"https://img.test/demo.png\" onerror=\"alert(1)\"></p>");
        assertTrue(sanitized.contains("src=\"https://img.test/demo.png\""));
        assertFalse(sanitized.contains("onerror"));
    }

    @Test
    void shouldRemoveJavascriptHref()
    {
        String sanitized = sanitizer.sanitizeContent("<p><a href=\"javascript:alert(1)\">link</a></p>");
        assertTrue(sanitized.contains(">link</a>"));
        assertFalse(sanitized.contains("javascript:"));
    }

    @Test
    void shouldOnlyKeepHttpAndHttpsExternalUrl()
    {
        assertEquals("https://demo.test/post", sanitizer.sanitizeExternalUrl("https://demo.test/post"));
        assertEquals("", sanitizer.sanitizeExternalUrl("javascript:alert(1)"));
    }

    @Test
    void shouldKeepRelativeFileAccessUrlInContent()
    {
        String sanitized = sanitizer.sanitizeContent("<p><img src=\"/file-api/file/12\" alt=\"cover\"></p>");
        assertTrue(sanitized.contains("file-api/file/12"), sanitized);
    }

    @Test
    void shouldCollectDistinctFileIdsFromContent()
    {
        String content = "<p><img src=\"/file-api/file/12\"></p>"
            + "<p><img src=\"/file/34\"></p>"
            + "<p><img src=\"/file-api/file/12\"></p>";
        assertEquals(new LinkedHashSet<>(Arrays.asList(12L, 34L)), sanitizer.collectFileIds(content));
    }

    @Test
    void shouldCollectFileIdsFromNewAndLegacyFileRoutes()
    {
        String html = "<p>" +
            "<img src=\"/file/download/12\" />" +
            "<img src=\"/file/13/download\" />" +
            "<img src=\"/file/14\" />" +
            "<img src=\"/file-api/file/15\" />" +
            "</p>";

        Set<Long> ids = sanitizer.collectFileIds(html);

        assertEquals(4, ids.size());
        assertTrue(ids.contains(12L));
        assertTrue(ids.contains(13L));
        assertTrue(ids.contains(14L));
        assertTrue(ids.contains(15L));
    }

    @Test
    void shouldCollectFileIdsFromImageQueryParameters()
    {
        String html = "<p>" +
            "<img src=\"http://127.0.0.1:9300/statics/2026/04/demo.png?fileId=88\" />" +
            "<img src=\"https://cdn.example.com/abc.jpg?x=1&fid=99\" />" +
            "</p>";

        Set<Long> ids = sanitizer.collectFileIds(html);

        assertEquals(2, ids.size());
        assertTrue(ids.contains(88L));
        assertTrue(ids.contains(99L));
    }
}
