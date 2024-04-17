package com.hjong.OnChat.controller;

import com.hjong.OnChat.entity.vo.req.KnowledgeBaseVO;
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

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/15
 **/
@RequestMapping("/knowledgeBase")
@RestController
public class KnowledgeBaseController {


    @PostMapping("/upload")
    public Mono<Void> uploadKnowledgeBase(@RequestPart KnowledgeBaseVO vo) throws IOException {

        File file = new File("src/main/resources/" + vo.getFile().filename());
        System.out.println(file.getName());

        return Mono.empty();


    }
}
