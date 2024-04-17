package com.hjong.OnChat.chain.prompt;

import org.springframework.stereotype.Component;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/15
 **/

@Component
public class PromptTemplate {

    public String knowledgePrompt(String prompt, String question) {
        return "基于我提供的知识库文本回答问题，请自行根据问题和文本判断是否根据知识库文本回答" +
                "\n" + "知识库：" + prompt +
                "\n" + "问题: " + question;
    }
}
