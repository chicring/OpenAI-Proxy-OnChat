package com.hjong.OnChat.controller;

import com.hjong.OnChat.entity.Result;
import com.hjong.OnChat.entity.dto.KnowledgeBase;
import com.hjong.OnChat.entity.vo.req.KnowledgeBaseVO;
import com.hjong.OnChat.filter.annotation.CheckRole;
import com.hjong.OnChat.service.KnowledgeBaseService;
import jakarta.annotation.Resource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static com.hjong.OnChat.entity.Consts.USER_ID;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/15
 **/
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
    public Mono<Void> upload(){
        return null;
    }



}
