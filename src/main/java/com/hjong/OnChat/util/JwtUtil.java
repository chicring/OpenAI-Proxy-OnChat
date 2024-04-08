package com.hjong.OnChat.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.hjong.OnChat.entity.dto.User;
import com.hjong.OnChat.entity.vo.resp.TokenInfoVO;
import com.hjong.OnChat.entity.enums.ServiceExceptionEnum;
import com.hjong.OnChat.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/
@Slf4j
@Component
public class JwtUtil {

    @Value("${chat.token.secret}")
    private String secret;

    @Value("${chat.token.issuer}")
    private String issuer;

    @Value("${chat.token.expires-minute}")
    private int expires;

    public TokenInfoVO tokenize(User user) {
        Instant now = Instant.now();
        Duration duration = Duration.ofMinutes(expires);
        Instant expiresAt = now.plus(duration);

        String token = JWT.create()
                .withIssuer(issuer)
                .withClaim("id", user.getId())
                .withClaim("role", user.getRole())
                .withExpiresAt(expiresAt)
                .sign(algorithm());

        return TokenInfoVO.builder()
                .token(token)
                .userId(user.getId())
                .role(user.getRole())
                .expiresAt(expiresAt.toEpochMilli())
                .build();
    }

    public Mono<DecodedJWT> verify(String headerToken) {
        if(headerToken == null){
            log.error("token is null");
            return Mono.error(new ServiceException(ServiceExceptionEnum.INVALID_API_KEY));
        }

        String token = convertToken(headerToken);
        try {
            JWTVerifier verifier = JWT.require(algorithm()).withIssuer(issuer).build();
            return Mono.just(verifier.verify(token));
        } catch (Exception e) {
            log.error("Failed to verify token: {}", e.getMessage());
            return Mono.error(new ServiceException(ServiceExceptionEnum.INVALID_API_KEY));
        }
    }

    public Mono<Integer> getUserId(DecodedJWT jwt){
        Claim claim = jwt.getClaim("id");
        return Mono.just(claim.asInt());
    }

    private Algorithm algorithm() {
        return Algorithm.HMAC256(secret);
    }

    private String convertToken(String headerToken){
        if(headerToken == null || !headerToken.startsWith("Bearer "))
            return null;
        return headerToken.substring(7);
    }
}
