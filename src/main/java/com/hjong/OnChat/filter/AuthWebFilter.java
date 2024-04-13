package com.hjong.OnChat.filter;

import com.hjong.OnChat.entity.enums.ServiceExceptionEnum;
import com.hjong.OnChat.exception.ServiceException;
import com.hjong.OnChat.service.ApiKeyService;
import com.hjong.OnChat.util.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.hjong.OnChat.entity.Consts.ROLE;
import static com.hjong.OnChat.entity.Consts.USER_ID;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/
@Slf4j
@Order(1)
@Component
public class AuthWebFilter implements WebFilter {

    private final List<PathPattern> excludePatterns;
    @Resource
    JwtUtil jwtUtil;

    @Resource
    ApiKeyService apiKeyService;


    public AuthWebFilter() {
        PathPatternParser parser = new PathPatternParser();
        // 排除不需要要验证的路径
        this.excludePatterns = List.of(
                parser.parse("/user/login"),
                parser.parse("/user/register"),
                parser.parse("/user/ask-code")
        );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        PathContainer requestPath = request.getPath().pathWithinApplication();
        String token = request.getHeaders().getFirst("Authorization");

        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        boolean isExclude = excludePatterns.stream().anyMatch(pattern -> pattern.matches(requestPath));
        if(isExclude){
            log.debug("放行：{}",requestPath);
            return chain.filter(exchange);
        }

        if (requestPath.toString().equals("/v1/chat/completions")) {
            return apiKeyService.validateKey(token)
                    .flatMap(pass -> {
                        if (pass) {
                            ServerHttpRequest mutatedRequest = request.mutate().build();
                            return chain.filter(exchange.mutate().request(mutatedRequest).build());
                        }else {
                            log.warn("无效API KEY");
                            return Mono.error(new ServiceException(ServiceExceptionEnum.INVALID_API_KEY));
                        }
                    });
        }

        return jwtUtil.verify(token)
                .flatMap(jwt -> {
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header(USER_ID, String.valueOf(jwt.getClaim("id")))
                            .header(ROLE, jwt.getClaim("role").asString())
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });

    }

}
