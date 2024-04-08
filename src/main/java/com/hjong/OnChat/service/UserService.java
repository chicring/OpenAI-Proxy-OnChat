package com.hjong.OnChat.service;

import com.hjong.OnChat.entity.dto.User;
import com.hjong.OnChat.entity.vo.req.UserLoginVO;
import com.hjong.OnChat.entity.vo.req.UserRegisterVO;
import com.hjong.OnChat.entity.vo.resp.UserInfoVO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/
public interface UserService {

    Mono<User> findByNameOrEmail(UserLoginVO vo);
    Mono<UserInfoVO> findInfoById(Integer id);
    Mono<User> saveUser(UserRegisterVO vo);
    Mono<Void> deleteUser(Integer id);
    Mono<Void> updateUser(User user);
    Flux<User> findAll();
    Mono<Void> resetVerifyCode(String email);

}
