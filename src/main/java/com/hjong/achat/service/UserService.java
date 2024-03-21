package com.hjong.achat.service;

import com.hjong.achat.entity.DTO.User;
import com.hjong.achat.entity.VO.req.UserLoginVO;
import com.hjong.achat.entity.VO.req.UserRegisterVO;
import com.hjong.achat.entity.VO.resp.UserInfoVO;
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

}
