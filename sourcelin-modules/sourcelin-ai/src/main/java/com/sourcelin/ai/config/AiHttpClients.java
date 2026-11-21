package com.sourcelin.ai.config;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
public final class AiHttpClients {
 private AiHttpClients() {}
 public static RestTemplate create() { SimpleClientHttpRequestFactory factory=new SimpleClientHttpRequestFactory(); factory.setConnectTimeout(5000);factory.setReadTimeout(120000);return new RestTemplate(factory); }
}
