package com.hjong.OnChat.adapter.gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.hjong.OnChat.adapter.openai.OpenAiResponseBody;
import com.hjong.OnChat.entity.enums.ChatRoleEnum;
import com.hjong.OnChat.util.JsonUtil;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Data
public class GeminiResponseBody {

    private List<Candidate> candidates;

    @Data
    public static class Candidate {
        private Content content;
        private String finishReason;
        private Integer index;
        @Data
        public static class Content {
            private List<Part> parts;
            private String role;

            @Data
            public static class Part {
                private String text;
            }
        }
    }


    public static Flux<String> GeminiToOpenAI(String text){
        JsonNode replyJson = JsonUtil.parseJSONObject("{" + text + "}");

        OpenAiResponseBody openAIResponseBody = new OpenAiResponseBody();

        List<OpenAiResponseBody.Choices> choicesList = new ArrayList<>();

        OpenAiResponseBody.Choices choices = new OpenAiResponseBody.Choices();
        choices.setIndex(1);

        OpenAiResponseBody.Message message = new OpenAiResponseBody.Message();
        message.setRole(ChatRoleEnum.ASSISTANT.getRole());
        message.setContent(replyJson.get("text").asText());

        choices.setMessage(message);
        choices.setDelta(message);
        choicesList.add(choices);

        openAIResponseBody.setChoices(choicesList);

        String id = UUID.randomUUID().toString();
        openAIResponseBody.setId(id);
        openAIResponseBody.setObject("chat.completion.chunk");
        openAIResponseBody.setCreated(Instant.now().getEpochSecond());
        openAIResponseBody.setModel("gemini-pro");

        openAIResponseBody.getChoices().getFirst().setIndex(0);
        openAIResponseBody.getChoices().getFirst().setFinish_reason("stop");

        OpenAiResponseBody.Usage usage = new OpenAiResponseBody.Usage();

        usage.setPrompt_tokens(0);
        usage.setCompletion_tokens(0);
        usage.setTotal_tokens(0);

        openAIResponseBody.setUsage(usage);

        return Flux.just(JsonUtil.toJSONString(openAIResponseBody));
    }
}