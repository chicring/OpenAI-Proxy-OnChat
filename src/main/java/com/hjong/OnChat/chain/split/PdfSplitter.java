package com.hjong.OnChat.chain.split;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/

@Component
public class PdfSplitter implements Splitter{


    private static final int MAX_LENGTH = 200;

    @Override
    public List<String> split(String content) {
        String text = content.replaceAll("\\s", " ").replaceAll("(\\r\\n|\\r|\\n|\\n\\r)"," ");
        String[] sentences = text.split("。");

        List<String> ans = new ArrayList<>();
        for (String s : sentences) {
            if (s.length() > MAX_LENGTH) {
                for (int index = 0; index < s.length(); index += MAX_LENGTH) {
                    String substring = s.substring(index, Math.min((index + MAX_LENGTH), s.length()));
                    if(substring.length() < 5) continue;
                    ans.add(substring);
                }
            } else {
                if(s.length() < 5) continue;
                ans.add(s);
            }
        }
        return ans;
    }
}
