package com.hjong.OnChat.adapter.gemini;

import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import com.hjong.OnChat.entity.enums.ChatRoleEnum;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@Data
public class GeminiRequestBody {

    private List<Content> contents;
    private GenerationConfig generationConfig;
    @Data
    public static class Content {
        private String role;
        private List<Part> parts;

        public static  List<Content> builder(List<OpenAiRequestBody.Message> messages){

             return messages.stream()
                    .map( message -> {
                        GeminiRequestBody.Content newContent = new GeminiRequestBody.Content();
                        newContent.setRole(message.getRole().equals(ChatRoleEnum.ASSISTANT.getRole()) ?
                                ChatRoleEnum.MODEL.getRole() :
                                ChatRoleEnum.USER.getRole());
                        GeminiRequestBody.Part newPart = new GeminiRequestBody.Part();
                        newPart.setText(message.getContent());
                        newContent.setParts(Collections.singletonList(newPart));
                        return newContent;
                    })
                    .toList();
        }
    }

    @Data
    public static class Part {
        private String text;
    }

    @Data
    public static class GenerationConfig {
        private Float temperature;
        private Integer topK;
        private Float topP;
        private Integer maxOutputTokens;
        private List<String> stopSequences;
    }

    public static GeminiRequestBody builder(OpenAiRequestBody request){
        GeminiRequestBody geminiRequestBody = new GeminiRequestBody();
        //设置配置类
        GeminiRequestBody.GenerationConfig generationConfig = new GeminiRequestBody.GenerationConfig();
        generationConfig.setTemperature(request.getTemperature());
        generationConfig.setTopK(request.getTop_k());
        generationConfig.setTopP(request.getTop_p());
        generationConfig.setMaxOutputTokens(request.getMax_tokens());
        generationConfig.setStopSequences(List.of());

        geminiRequestBody.setContents(Content.builder(request.getMessages()));

        return geminiRequestBody;
    }
}