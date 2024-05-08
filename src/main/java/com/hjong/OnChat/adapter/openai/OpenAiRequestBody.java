package com.hjong.OnChat.adapter.openai;

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
        private String content; //String or Content
        private String name;
        private Object tool_calls;
        private String tool_call_id;

        @Data
        public static class Content{
            private String type;
            private String text;
            private Image_url image_url;

            @Data
            public static class Image_url{
                private String url;
                private String detail;
            }
        }

        public Message builder(String role, String content){
            Message message = new Message();
            message.setRole(role);
            message.setContent(content);

            return message;
        }
    }

    private boolean stream;
    private String model;
    private Float temperature;
    private Integer presence_penalty;
    private Integer frequency_penalty;
    private Float top_p;
    private Integer top_k;
    private String stop;
    private Integer max_tokens;

    List<Tools> tools;

    private String tool_choice;

    @Data
    public static class Tools{
        private String type;  //function
        private Function function;

        @Data
        public static class Function{
            private String description;
            private String name;
            private Object parameters;
        }
    }
}
