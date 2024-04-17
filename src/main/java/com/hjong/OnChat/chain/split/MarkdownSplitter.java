package com.hjong.OnChat.chain.split;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/15
 **/

@Component
public class MarkdownSplitter implements Splitter{
    @Override
    public List<String> split(String content) {
        return List.of();
    }
}
