package com.hjong.OnChat.repositories;

import com.hjong.OnChat.entity.dto.Logs;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/19
 **/
@Repository
public interface LogsRepositories extends R2dbcRepository<Logs,Long> {

//    Flux<Logs> findAll(Pageable pageable, Logs logs);
}
