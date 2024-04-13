package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.entity.dto.Model;
import com.hjong.OnChat.repositories.ModelRepository;
import com.hjong.OnChat.service.ModelService;
import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/13
 **/
public class ModelServiceImpl implements ModelService {

    @Resource
    ModelRepository modelRepository;

    @Override
    public Flux<Model> findAll() {
        return modelRepository.findAll();
    }
}
