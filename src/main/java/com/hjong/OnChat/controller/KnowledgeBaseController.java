package com.hjong.OnChat.controller;

import com.hjong.OnChat.entity.Result;
import com.hjong.OnChat.entity.dto.File;
import com.hjong.OnChat.entity.dto.KnowledgeBase;
import com.hjong.OnChat.entity.vo.req.KnowledgeBaseVO;
import com.hjong.OnChat.entity.vo.req.KnowledgeUploadVO;
import com.hjong.OnChat.filter.annotation.CheckRole;
import com.hjong.OnChat.service.KnowledgeBaseService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


import java.util.List;

import static com.hjong.OnChat.entity.Consts.USER_ID;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/15
 **/
@CrossOrigin
@RequestMapping("/knowledgeBase")
@RestController
public class KnowledgeBaseController {

    @Resource
    KnowledgeBaseService knowledgeBaseService;

    /**
     * 保存知识库基本信息
     */
    @CheckRole
    @PostMapping("/save")
    public Mono<Result<Void>> save(@RequestHeader(USER_ID) Integer userId,
                                   @RequestBody KnowledgeBaseVO vo){

        return knowledgeBaseService.save(vo,userId)
                .thenReturn(Result.ok());
    }

    /**
     * 上传知识库文件
     */
    @CheckRole
    @PostMapping("/upload")
    public Mono<Result<String>> upload(KnowledgeUploadVO vo){

        return knowledgeBaseService.upload(vo)
                .then(Mono.just(Result.ok("上传成功,请等待处理")));
    }


    @GetMapping("/list")
    public Mono<Result<List<KnowledgeBase>>> findAll(){
        return knowledgeBaseService.find()
                .collectList()
                .map(Result::ok);
    }


    @GetMapping("/{knowledgeBaseId}")
    public Mono<Result<List<File>>> findFile(@PathVariable Integer knowledgeBaseId){
        return knowledgeBaseService.findFile(knowledgeBaseId)
                .collectList()
                .map(Result::ok);
    }

}
