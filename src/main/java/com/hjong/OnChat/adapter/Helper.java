package com.hjong.OnChat.adapter;

import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import com.hjong.OnChat.entity.enums.ChatRoleEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/5/8
 **/

public class  Helper {

    // 重构消息
    public static List<OpenAiRequestBody.Message> ReconstructMessage(List<OpenAiRequestBody.Message> messageList){

        return IntStream.range(0, messageList.size())
                .mapToObj(i -> {
                    OpenAiRequestBody.Message message = messageList.get(i);
                    if (message.getRole().equals(ChatRoleEnum.SYSTEM.getRole())) {
                        OpenAiRequestBody.Message userMessage = message.builder(ChatRoleEnum.USER.getRole(), message.getContent());
                        // 如果当前消息不是最后一个消息，那么添加一个新的助手消息
                        if (i < messageList.size() - 1) {
                            OpenAiRequestBody.Message assistantMessage = message.builder(ChatRoleEnum.ASSISTANT.getRole(), "好的");
                            return Arrays.asList(userMessage, assistantMessage);
                        } else {
                            return Collections.singletonList(userMessage);
                        }
                    } else {
                        return Collections.singletonList(message);
                    }
                })
                .flatMap(List::stream)
                .toList();
    }

}
