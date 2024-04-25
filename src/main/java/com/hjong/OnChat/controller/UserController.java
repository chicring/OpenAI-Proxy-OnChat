package com.hjong.OnChat.controller;

import com.hjong.OnChat.entity.Result;
import com.hjong.OnChat.entity.vo.req.EmailResetVO;
import com.hjong.OnChat.entity.vo.req.UserLoginVO;
import com.hjong.OnChat.entity.vo.req.UserRegisterVO;
import com.hjong.OnChat.entity.vo.resp.TokenInfoVO;
import com.hjong.OnChat.entity.vo.resp.UserInfoVO;
import com.hjong.OnChat.service.UserService;
import com.hjong.OnChat.util.JwtUtil;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.hjong.OnChat.entity.Consts.USER_ID;

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
    public Mono<Result<TokenInfoVO>> doLogin(@Valid @RequestBody UserLoginVO vo){
        return userService.findByNameOrEmail(vo).flatMap( user -> {
            log.debug("用户：{} 登录成功",user.getUsername());
            return Mono.just(Result.ok("登录成功",jwtUtil.tokenize(user)));
        });
    }


    @PostMapping("/register")
    public Mono<Result<Void>> doRegister(@Valid @RequestBody UserRegisterVO vo){
        return userService.saveUser(vo).thenReturn(Result.ok("注册成功"));
    }


    @GetMapping("/info")
    public Mono<Result<UserInfoVO>> userInfo(@RequestHeader(USER_ID) Integer userId) {

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

    @PostMapping("/reset")
    public Mono<Result<Void>> doUpdate(@Valid @RequestBody EmailResetVO vo){

        return userService.emailReset(vo).then(Mono.just(Result.ok()));
    }

    @GetMapping("/ask-code")
    public Mono<Result<Void>> askVerifyCode(@RequestParam @Email String email){
        return userService.resetVerifyCode(email).thenReturn(Result.ok("验证码已发送"));
    }
}
