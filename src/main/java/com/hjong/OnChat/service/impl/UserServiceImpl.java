package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.entity.dto.Mail;
import com.hjong.OnChat.entity.dto.User;
import com.hjong.OnChat.entity.vo.req.UserLoginVO;
import com.hjong.OnChat.entity.vo.req.UserRegisterVO;
import com.hjong.OnChat.entity.vo.resp.UserInfoVO;
import com.hjong.OnChat.entity.enums.ServiceExceptionEnum;
import com.hjong.OnChat.exception.ServiceException;
import com.hjong.OnChat.repositories.UserRepositories;
import com.hjong.OnChat.service.EmailService;
import com.hjong.OnChat.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;

import static com.hjong.OnChat.entity.Constants.TEXT_MAIL;

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
    EmailService mailService;

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

                    return mailService.sendEmail(new Mail("重置密码",
                                    "您正在执行重置密码操作，验证码: "+code+"，有效时间3分钟，如非本人操作，请无视。",
                                    email, TEXT_MAIL
                            ));
                });
    }

    private Mono<Boolean> existsAccount(String account){

        return userRepositories.findByNameOrEmail(account)
                .map(user -> true)
                .defaultIfEmpty(false);
    }


}
