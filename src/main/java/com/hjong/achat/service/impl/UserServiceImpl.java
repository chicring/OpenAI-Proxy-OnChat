package com.hjong.achat.service.impl;

import com.hjong.achat.entity.DTO.User;
import com.hjong.achat.entity.VO.req.UserLoginVO;
import com.hjong.achat.entity.VO.req.UserRegisterVO;
import com.hjong.achat.entity.VO.resp.UserInfoVO;
import com.hjong.achat.enums.ServiceExceptionEnum;
import com.hjong.achat.exception.ServiceException;
import com.hjong.achat.repositories.UserRepositories;
import com.hjong.achat.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static reactor.core.publisher.Mono.empty;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/

@Service
public class UserServiceImpl implements UserService {
    @Resource
    UserRepositories userRepositories;

    @Override
    public Mono<User> findByNameOrEmail(UserLoginVO vo) {

        return userRepositories.findByNameOrEmail(vo.getAccount())
                .switchIfEmpty(  Mono.error(new ServiceException(ServiceExceptionEnum.USER_NOT_EXIST)))
                .flatMap(user -> {
                    if(!user.getPassword().equals(vo.getPwd())) {
                        return Mono.error(new ServiceException(ServiceExceptionEnum.PASSWORD_ERROR));
                    }
                    return Mono.just(user);
                })
                .onErrorResume(e -> {
                    if (e instanceof ServiceException) {
                        return Mono.error(e);
                    }
                    return Mono.error(new ServiceException(ServiceExceptionEnum.SERVICE_EXCEPTION));
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
                })
                .onErrorResume(e -> Mono.error(new ServiceException(ServiceExceptionEnum.SERVICE_EXCEPTION)));
    }

    @Override
    public Mono<User> saveUser(UserRegisterVO vo) {

        return existsAccount(vo.getEmail()).flatMap(
                exists -> {
                    if(exists) {
                        return Mono.error(new ServiceException(ServiceExceptionEnum.USER_EXIST));
                    }else {
                        User user = new User();
                        user.setUsername(vo.getUsername());
                        user.setPassword(vo.getPassword());
                        user.setEmail(vo.getEmail());

                        return userRepositories.save(user)
                                .onErrorResume(e -> Mono.error(new ServiceException(ServiceExceptionEnum.SERVICE_EXCEPTION)));
                    }
                }
        ).onErrorResume(e -> Mono.error(new ServiceException(ServiceExceptionEnum.SERVICE_EXCEPTION)));

    }

    @Override
    public Mono<Void> deleteUser(Integer id) {
        return userRepositories.deleteById(id)
                .onErrorResume(e -> Mono.error(new ServiceException(ServiceExceptionEnum.SERVICE_EXCEPTION)));
    }

    @Override
    public Mono<Void> updateUser(User user) {
        return null;
    }

    @Override
    public Flux<User> findAll() {
        return null;
    }

    private Mono<Boolean> existsAccount(String account){

        return userRepositories.findByNameOrEmail(account)
                .map(user -> true)
                .defaultIfEmpty(false);
    }
}
