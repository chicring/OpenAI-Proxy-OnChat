package com.hjong.achat.adapter.gemini;

import com.hjong.achat.adapter.openai.OpenAiRequestBody;
import com.hjong.achat.adapter.qwen.QwenRequestBody;
import com.hjong.achat.enums.ChatRoleEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        private double temperature;
        private double topK;
        private double topP;
        private Integer maxOutputTokens;
        private List<String> stopSequences;
    }

    public static GeminiRequestBody builder(OpenAiRequestBody request){
        GeminiRequestBody geminiRequestBody = new GeminiRequestBody();
        //设置配置类
        GeminiRequestBody.GenerationConfig generationConfig = new GeminiRequestBody.GenerationConfig();
        generationConfig.setTemperature(request.getTemperature());
        generationConfig.setTopK(2);
        generationConfig.setTopP(request.getTop_p());
        generationConfig.setMaxOutputTokens(request.getMax_tokens());
        generationConfig.setStopSequences(List.of());

        geminiRequestBody.setContents(Content.builder(request.getMessages()));

        return geminiRequestBody;
    }
}