package com.sourcelin.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class GatewayAuthFilter implements GlobalFilter, Ordered {

    private static final PathMatcher pathMatcher = new AntPathMatcher();

    private static final List<String> WHITE_LIST = Arrays.asList(
        "/auth/login",
        "/auth/logout",
        "/auth/register",
        "/auth/captcha",
        "/auth/code",
        "/auth/user/login",
        "/auth/user/register",
        "/auth/user/logout",
        "/auth/user/refresh",
        "/code",
        "/user/login",
        "/user/register",
        "/user/logout",
        "/user/refresh",
        "/front/**",
        "/app/**",
        "/blog/**",
        "/blog-api/**"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        String token = getToken(exchange);
        if (token == null || token.isEmpty()) {
            return unauthorized(exchange.getResponse(), "请先登录");
        }

        return chain.filter(exchange);
    }

    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private String getToken(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
