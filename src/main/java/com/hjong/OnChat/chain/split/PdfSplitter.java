package com.hjong.OnChat.chain.split;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/

@Component
public class PdfSplitter implements Splitter{


    private static final int MAX_LENGTH = 500;

    @Override
    public List<String> split(String content) {
        String text = content.replaceAll("\\s+", " ").replaceAll("(\\r\\n|\\r|\\n|\\n\\r)", "");
        String[] sentences = text.split("。");

        return Arrays.stream(sentences)
                .filter(s -> s.length() >= 5 || s.contains("。"))
                .map(s -> {
                    if (s.length() > MAX_LENGTH) {
                        List<String> substrings = new ArrayList<>();
                        for (int index = 0; index < s.length(); index = (index + 1) * MAX_LENGTH) {
                            String substring = s.substring(index, MAX_LENGTH);
                            substrings.add(substring);
                        }
                        return substrings;
                    } else {
                        return List.of(s);
                    }
                })
                .flatMap(List::stream)
                .toList();
    }

}
