package com.hjong.OnChat.chain.split;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/15
 **/

@Component
public class MarkdownSplitter implements Splitter{

    private static final int MAX_LENGTH = 800;

    @Override
    public List<String> split(String content) {
        List<String> result = new ArrayList<>();
        splitContent(result, content, 1);
        return result;
    }

    private void splitContent(List<String> result, String content, int level) {

        if (level > 6 || content.length() <= MAX_LENGTH) {
            result.add(content);
            return;
        }

        String regex = "\n" + String.join("", Collections.nCopies(level, "#")) + " ";
        String[] parts = content.split(regex);


        if (parts.length <= 1) {
            splitContent(result, content, level + 1);
        } else {
            for (String part : parts) {
                if (part.length() > MAX_LENGTH) {
                    splitContent(result, part, level + 1);
                } else {
                    result.add(part.trim());
                }
            }
        }
    }

}
