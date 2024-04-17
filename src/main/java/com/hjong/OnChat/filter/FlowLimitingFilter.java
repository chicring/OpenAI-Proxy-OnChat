package com.hjong.OnChat.filter;

import com.hjong.OnChat.entity.enums.ServiceExceptionEnum;
import com.hjong.OnChat.exception.ServiceException;
import com.hjong.OnChat.util.FlowUtils;
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

import static com.hjong.OnChat.entity.Consts.FLOW_LIMIT_IP;


/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/
@Slf4j
@Component
@Order(-1)
public class FlowLimitingFilter implements WebFilter {

    @Resource
    FlowUtils flowUtils;

    private final List<PathPattern> excludePatterns;

    public FlowLimitingFilter() {
        PathPatternParser parser = new PathPatternParser();
        // 排除不需要要验证的路径
        this.excludePatterns = List.of(
                parser.parse("/user/ask-code")
        );
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String host =  request.getRemoteAddress().getAddress().getHostAddress();
        PathContainer requestPath = request.getPath().pathWithinApplication();

        boolean isExclude = excludePatterns.stream().anyMatch(pattern -> pattern.matches(requestPath));

        if (request.getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        if(!isExclude){
            log.debug("放行：{}",requestPath);
            return chain.filter(exchange);
        }

        //限制接口60s 1次
        return flowUtils.limitOnceCheck(FLOW_LIMIT_IP + host, 60)
                .flatMap(pass -> {
                    if(pass){
                        ServerHttpRequest mutatedRequest = request.mutate().build();
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    }else{
                        log.warn("请求频率过高，已限制访问, ip：{}",host);
                        return Mono.error(new ServiceException(ServiceExceptionEnum.Too_Many_Requests));
                    }
                });
    }
}
