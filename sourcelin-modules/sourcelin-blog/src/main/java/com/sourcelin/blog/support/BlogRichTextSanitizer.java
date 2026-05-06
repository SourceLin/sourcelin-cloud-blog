package com.sourcelin.blog.support;

import com.sourcelin.blog.dto.ArticleInsertDTO;
import com.sourcelin.common.core.utils.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 鏂囩珷瀵屾枃鏈櫧鍚嶅崟娓呮礂銆? */
@Component
public class BlogRichTextSanitizer
{
    /** 仅用于解析相对路径 img/a 的 src/href，使 /file-api/... 能解析为 https 并通过 Safelist */
    private static final String CONTENT_PARSE_BASE = "https://sourcelin.internal";

    private static final Pattern FILE_ACCESS_PATTERN = Pattern.compile("(?:/file-api)?/file/(?:download/)?(\\d+)(?:/download)?");
    private static final Pattern FILE_QUERY_FILE_ID_PATTERN = Pattern.compile("(?:\\?|&)(?:fileId|fid)=(\\d+)");

    private static final Safelist ARTICLE_CONTENT_SAFELIST = Safelist.none()
        .addTags("p", "br", "h1", "h2", "h3", "h4", "h5", "h6",
            "strong", "em", "u", "s", "ul", "ol", "li",
            "blockquote", "pre", "code", "img", "a")
        .addAttributes("img", "src", "alt", "title")
        .addAttributes("a", "href", "target", "rel")
        .addProtocols("img", "src", "http", "https")
        .addProtocols("a", "href", "http", "https")
        .preserveRelativeLinks(true);

    private final Cleaner cleaner = new Cleaner(ARTICLE_CONTENT_SAFELIST);

    public void sanitizeArticleInput(ArticleInsertDTO dto)
    {
        if (dto == null)
        {
            return;
        }
        dto.setTitle(sanitizePlainText(dto.getTitle()));
        dto.setSummary(sanitizePlainText(dto.getSummary()));
        dto.setOriginalUrl(sanitizeExternalUrl(dto.getOriginalUrl()));
        dto.setContent(sanitizeContent(dto.getContent()));
    }

    public String sanitizeContent(String content)
    {
        if (StringUtils.isEmpty(content))
        {
            return content;
        }
        Document dirty = Jsoup.parseBodyFragment(content, CONTENT_PARSE_BASE);
        Document clean = cleaner.clean(dirty);
        clean.outputSettings(new Document.OutputSettings().prettyPrint(false));
        for (Element link : clean.select("a[href]"))
        {
            link.attr("rel", "noopener noreferrer nofollow");
        }
        return clean.body().html();
    }

    public String sanitizePlainText(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            return value;
        }
        return Jsoup.clean(value, Safelist.none()).trim();
    }

    public String sanitizeExternalUrl(String value)
    {
        String sanitized = sanitizePlainText(value);
        if (StringUtils.isEmpty(sanitized))
        {
            return sanitized;
        }
        String normalized = sanitized.trim();
        String lowerCase = normalized.toLowerCase(Locale.ROOT);
        if (lowerCase.startsWith("http://") || lowerCase.startsWith("https://"))
        {
            return normalized;
        }
        return StringUtils.EMPTY;
    }

    public Set<Long> collectFileIds(String content)
    {
        Set<Long> fileIds = new LinkedHashSet<>();
        if (StringUtils.isEmpty(content))
        {
            return fileIds;
        }
        collectFileIds(content, FILE_ACCESS_PATTERN, fileIds);
        collectFileIds(content, FILE_QUERY_FILE_ID_PATTERN, fileIds);
        return fileIds;
    }

    private void collectFileIds(String content, Pattern pattern, Set<Long> fileIds)
    {
        Matcher matcher = pattern.matcher(content);
        while (matcher.find())
        {
            fileIds.add(Long.valueOf(matcher.group(1)));
        }
    }
}
