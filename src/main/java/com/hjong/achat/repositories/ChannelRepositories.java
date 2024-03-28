package com.hjong.achat.repositories;

import com.hjong.achat.entity.DTO.Channel;
import jakarta.annotation.Resource;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Repository
public interface ChannelRepositories extends R2dbcRepository<Channel,Integer> {

    @Query("SELECT *, JSON_VALUE(models, CONCAT('$.\"', :model, '\"')) as model FROM Channel HAVING model IS NOT NULL ORDER BY priority DESC")
    Flux<Channel> selectChannel(String model);

}
