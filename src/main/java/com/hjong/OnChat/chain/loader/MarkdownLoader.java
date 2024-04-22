package com.hjong.OnChat.chain.loader;

import com.hjong.OnChat.chain.split.MarkdownSplitter;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.hibernate.validator.constraints.Range;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/19
 **/
@Component
public class MarkdownLoader implements ResourceLoader{

    @Resource
    MarkdownSplitter markdownSplitter;


    @SneakyThrows
    @Override
    public String getContent(InputStream inputStream){

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder content = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            content.append(line);
            content.append("\n");
        }

        return content.toString();
    }

    @Override
    public List<String> getChunkList(String content) {
        return markdownSplitter.split(content);
    }
}
