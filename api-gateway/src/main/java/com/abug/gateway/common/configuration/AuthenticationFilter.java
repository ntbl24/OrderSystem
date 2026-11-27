package com.abug.gateway.common.configuration;

import com.abug.gateway.common.response.ApiResponse;
import com.abug.gateway.service.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GlobalFilter, Ordered {
    private final IdentityService identityService;
    private final ObjectMapper objectMapper;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    private String[] publicEndpoints = {
            "/identity/auth/**",
            "/identity/users/registration",
            "/notification/email/send",
            "/file/media/download/**",
            "/*/v3/api-docs",
            "/*/v3/api-docs/**",
            "/*/swagger-ui/**",
            "/*/swagger-ui.html"
    };

    @Value("${app.api-prefix}")
    private String apiPrefix;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        if(isPublicEndpoint(exchange.getRequest()))
            return chain.filter(exchange);

        List<String> authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION);
        if(CollectionUtils.isEmpty(authHeader)){
            return unauthenticated(exchange.getResponse());
        }

        String auth = authHeader.getFirst();

        if(auth == null || !auth.startsWith("Bearer"))
            return unauthenticated(exchange.getResponse());

        String token = auth.substring(7).trim();

        return identityService.introspect(token).flatMap(introspectResponse -> {
            if(introspectResponse.getData().valid())
                return chain.filter(exchange);
            else
                return unauthenticated(exchange.getResponse());
        }).onErrorResume(throwable -> unauthenticated(exchange.getResponse()));
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean isPublicEndpoint(ServerHttpRequest request){
        String path = request.getURI().getPath();
        return Arrays.stream(publicEndpoints)
                .anyMatch(s -> pathMatcher.match(apiPrefix + s, path));
    }

    Mono<Void> unauthenticated(ServerHttpResponse response){
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(1401)
                .message("Unauthenticated")
                .build();
        String body = null;
        try {
            body = objectMapper.writeValueAsString(apiResponse);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
