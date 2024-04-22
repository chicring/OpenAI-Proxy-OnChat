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
                .filter(s -> s.length() >= 5 || s.contains("。") && !s.trim().isEmpty())
                .map(s -> {
                    if (s.length() > MAX_LENGTH) {
                        List<String> substrings = new ArrayList<>();
                        int index = 0;
                        while (index < s.length()) {
                            int end = Math.min(index + MAX_LENGTH, s.length());
                            String substring = s.substring(index, end);
                            if (!substring.trim().isEmpty()){
                                substrings.add(substring);
                            }
                            index = end;
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
