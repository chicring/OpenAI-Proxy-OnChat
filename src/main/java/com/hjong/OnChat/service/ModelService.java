package com.hjong.OnChat.service;

import com.hjong.OnChat.entity.dto.Model;
import reactor.core.publisher.Flux;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/13
 **/
public interface ModelService {

    Flux<Model> findAll();
}
