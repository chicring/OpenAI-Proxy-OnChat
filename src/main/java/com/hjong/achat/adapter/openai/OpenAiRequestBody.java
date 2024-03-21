package com.hjong.achat.adapter.openai;

import lombok.Data;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Data
public class OpenAiRequestBody {
    private List<Message> messages;

    @Data
    public static class Message{
        private String role;
        private String content;

        public Message builder(String role, String content){
            Message message = new Message();
            message.setRole(role);
            message.setContent(content);

            return message;
        }
    }

    private boolean stream;
    private String model;
    private double temperature;
    private int presence_penalty;
    private int frequency_penalty;
    private double top_p;
    private String stop;
    private Integer max_tokens;
}
