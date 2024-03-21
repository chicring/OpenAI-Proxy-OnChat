package com.hjong.achat.repositories;

import com.hjong.achat.entity.DTO.User;
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

}
