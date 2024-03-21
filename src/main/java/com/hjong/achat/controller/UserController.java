package com.hjong.achat.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.hjong.achat.entity.Result;
import com.hjong.achat.entity.VO.req.UserLoginVO;
import com.hjong.achat.entity.VO.req.UserRegisterVO;
import com.hjong.achat.service.UserService;
import com.hjong.achat.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    JwtUtil jwtUtil;

    @Resource
    UserService userService;

    @PostMapping("/login")
    public Mono<Result> doLogin(@Valid @RequestBody UserLoginVO vo){
        return userService.findByNameOrEmail(vo).flatMap( user -> {
            log.debug("用户：{} 登录成功",user.getUsername());
            return Mono.just(Result.ok("登录成功",jwtUtil.tokenize(user)));
        });
    }


    @PostMapping("/register")
    public Mono<Result> doRegister(@Valid @RequestBody UserRegisterVO vo){
        return userService.saveUser(vo).thenReturn(Result.ok("注册成功"));
    }

    @GetMapping("/info")
    public Mono<Result> userInfo(ServerWebExchange exchange) {

        String userId = exchange.getRequest().getHeaders().getFirst("USER-ID");
        log.debug("用户：{} 查询个人信息", userId);
        return userService
                .findInfoById(Integer.valueOf(userId))
                    .flatMap(
                            user -> {
                                log.debug("用户：{} 查询个人信息成功", user.getUsername());
                                return Mono.just(Result.ok("查询成功", user));
                            }
                    );
    }
}
