package com.sourcelin.common.core.web.advice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiResponseBodyAdviceTest
{
    @Test
    void stringResponseUsesJsonContentType() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addSerializer(LocalDateTime.class, ToStringSerializer.instance));
        ApiResponseBodyAdvice advice = new ApiResponseBodyAdvice(mapper);
        ServletServerHttpResponse response = new ServletServerHttpResponse(new MockHttpServletResponse());

        Object body = advice.beforeBodyWrite("任务已重新执行", null, MediaType.TEXT_PLAIN,
            StringHttpMessageConverter.class, null, response);

        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        JsonNode json = mapper.readTree((String) body);
        assertEquals(0, json.get("code").intValue());
        assertEquals("任务已重新执行", json.get("data").textValue());
    }
}
