package com.hjong.achat.repositories;

import com.hjong.achat.entity.DTO.Logs;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/19
 **/
@Repository
public interface LogsRepositories extends R2dbcRepository<Logs,Long> {
}
