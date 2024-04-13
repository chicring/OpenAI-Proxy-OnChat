package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.entity.dto.User;
import com.hjong.OnChat.entity.vo.req.EmailResetVO;
import com.hjong.OnChat.entity.vo.req.UserLoginVO;
import com.hjong.OnChat.entity.vo.req.UserRegisterVO;
import com.hjong.OnChat.entity.vo.resp.UserInfoVO;
import com.hjong.OnChat.entity.enums.ServiceExceptionEnum;
import com.hjong.OnChat.exception.ServiceException;
import com.hjong.OnChat.repositories.UserRepositories;
import com.hjong.OnChat.service.UserService;
import com.hjong.OnChat.util.EmailUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.time.Duration;
import java.util.Random;

import static com.hjong.OnChat.service.Consts.VERIFY_EMAIL_DATA;


/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserRepositories userRepositories;

    @Resource
    EmailUtil emailUtil;

    @Resource
    ReactiveRedisTemplate<String,String> reactiveRedisTemplate;

    private final long timeout = 180;

    @Override
    public Mono<User> findByNameOrEmail(UserLoginVO vo) {

        return userRepositories.findByNameOrEmail(vo.getAccount())
                .switchIfEmpty(  Mono.error(new ServiceException(ServiceExceptionEnum.USER_NOT_EXIST)))
                .flatMap(user -> {
                    if(!user.getPassword().equals(vo.getPwd())) {
                        return Mono.error(new ServiceException(ServiceExceptionEnum.PASSWORD_ERROR));
                    }
                    return Mono.just(user);
                });
    }

    @Override
    public Mono<UserInfoVO> findInfoById(Integer id) {
        return userRepositories.findById(id)
                .map(user -> {
                    UserInfoVO vo = new UserInfoVO();
                    vo.setId(user.getId());
                    vo.setUsername(user.getUsername());
                    vo.setEmail(user.getEmail());
                    vo.setRegisterDate(user.getRegisterDate());
                    vo.setRole(user.getRole());
                    return vo;
                });
    }

    @Override
    public Mono<User> saveUser(UserRegisterVO vo) {

        return existsAccount(vo.getEmail())
                .flatMap( exists -> exists ? Mono.error(new ServiceException(ServiceExceptionEnum.USER_EXIST)) : existsAccount(vo.getUsername()))
                .flatMap( exists -> {
                    if(exists) {
                        return Mono.error(new ServiceException(ServiceExceptionEnum.USER_EXIST));
                    }
                    User user = new User();
                    user.setUsername(vo.getUsername());
                    user.setPassword(vo.getPassword());
                    user.setEmail(vo.getEmail());

                    return userRepositories.save(user);
                });

    }


    @Override
    public Mono<Void> deleteUser(Integer id) {
        return userRepositories.deleteById(id);
    }

    @Override
    public Mono<Void> updateUser(User user) {
        return null;
    }

    @Override
    public Flux<User> findAll() {
        return null;
    }


    @Override
    public Mono<Void> resetVerifyCode(String email) {
        return this.existsAccount(email)
                .flatMap(exists -> {
                    if(!exists) {
                        return Mono.error(new ServiceException(ServiceExceptionEnum.USER_NOT_EXIST));
                    }
                    Random random = new Random();
                    int code = random.nextInt(899999) + 100000;

                    //存到redis
                    return reactiveRedisTemplate.opsForValue()
                            .set(VERIFY_EMAIL_DATA + email, String.valueOf(code), Duration.ofMinutes(3))
                            .then(
                                    //发送邮件
                                    emailUtil.sendEmailVerifyCode(email, String.valueOf(code))
                            );

                });
    }

    @Override
    public Mono<Void> emailReset(EmailResetVO vo) {

        return reactiveRedisTemplate.opsForValue()
                .get(VERIFY_EMAIL_DATA + vo.getEmail())
                .filter(code -> code.equals(vo.getCode()))
                .switchIfEmpty(Mono.error(new ServiceException(ServiceExceptionEnum.CODE_ERROR)))
                .flatMap(code -> userRepositories.updatePasswordByEmail(vo.getEmail(), vo.getPassword()))
                .then(reactiveRedisTemplate.delete(vo.getEmail())).then();
    }


    private Mono<Boolean> existsAccount(String account){

        return userRepositories.findByNameOrEmail(account)
                .map(user -> true)
                .defaultIfEmpty(false);
    }


}
