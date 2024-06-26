package com.hjong.OnChat.repositories;

import com.hjong.OnChat.entity.dto.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@Repository
public interface UserRepositories extends R2dbcRepository<User,Integer> {

    @Query("select * from User where username = :account or email = :account")
    Mono<User> findByNameOrEmail(String account);

    @Query("update User set password = :password where email = :email")
    Mono<Void> updatePasswordByEmail(String email, String password);
}
