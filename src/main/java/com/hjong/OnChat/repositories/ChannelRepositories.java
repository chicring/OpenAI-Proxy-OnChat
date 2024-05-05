package com.hjong.OnChat.repositories;

import com.ctc.wstx.dtd.ModelNode;
import com.hjong.OnChat.entity.dto.Channel;
import com.hjong.OnChat.entity.vo.req.ChannelVO;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Repository
public interface ChannelRepositories extends R2dbcRepository<Channel,Integer> {

    @Query("SELECT *, JSON_VALUE(models, CONCAT('$.\"', :model, '\"')) as model FROM Channel HAVING model IS NOT NULL ORDER BY priority DESC")
    Flux<Channel> selectChannel(String model);



    @Modifying
    @Query("UPDATE Channel SET name = :name, type = :type, api_key = :apiKey, base_url = :baseUrl, models = :models, priority = :priority, enable_proxy = :enableProxy WHERE id = :id")
    Mono<Integer> updateById(@Param("name") String name,
                             @Param("type") String type,
                             @Param("apiKey") String apiKey,
                             @Param("baseUrl") String baseUrl,
                             @Param("models") String models,
                             @Param("priority") Integer priority,
                             @Param("enableProxy") Boolean enableProxy,
                             @Param("id") Integer id);
}
