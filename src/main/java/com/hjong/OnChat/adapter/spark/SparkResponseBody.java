package com.hjong.OnChat.adapter.spark;

import com.hjong.OnChat.adapter.openai.OpenAiResponseBody;
import com.hjong.OnChat.entity.enums.ChatRoleEnum;
import com.hjong.OnChat.util.JsonUtil;
import lombok.Data;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/7
 **/
@Data
public class SparkResponseBody {
    private Header header;
    private Payload payload;


    @Data
    public static class Header {
        private int code;
        private String message;
        private String sid;
        private int status;
    }

    @Data
    public static class Payload {
        private Choices choices;
        private Usage usage;

        @Data
        public static class Choices {
            private int status;
            private int seq;
            private List<Text> text;

           @Data
            public static class Text {
                private String content;
                private String role;
                private int index;

            }
        }

        @Data
        public static class Usage {
            private Text text;

            @Data
            public static class Text {
                private int question_tokens;
                private int prompt_tokens;
                private int completion_tokens;
                private int total_tokens;
            }
        }
    }


    public static String SparkToOpenAI(SparkResponseBody responseBody, String model) {
        OpenAiResponseBody openAiResponseBody = new OpenAiResponseBody();

        List<OpenAiResponseBody.Choices> choicesList = new ArrayList<>();
        responseBody.getPayload().getChoices().getText().forEach(text -> {
            OpenAiResponseBody.Choices choices = new OpenAiResponseBody.Choices();

            OpenAiResponseBody.Message message = new OpenAiResponseBody.Message();
            message.setRole(ChatRoleEnum.ASSISTANT.getRole());
            message.setContent(text.getContent());
            choices.setMessage(message);
            choices.setDelta(message);
            choicesList.add(choices);
        });

        openAiResponseBody.setChoices(choicesList);
        openAiResponseBody.getChoices().getFirst().setIndex(0);
        openAiResponseBody.getChoices().getFirst().setFinish_reason("stop");
        openAiResponseBody.setId(responseBody.getHeader().getSid());
        openAiResponseBody.setObject("chat.completion.chunk");
        openAiResponseBody.setCreated(Instant.now().getEpochSecond());
        openAiResponseBody.setModel(model);

        if(responseBody.getPayload().getUsage() != null){
            OpenAiResponseBody.Usage usage = new OpenAiResponseBody.Usage();
            usage.setPrompt_tokens(responseBody.getPayload().getUsage().getText().getPrompt_tokens());
            usage.setCompletion_tokens(responseBody.getPayload().getUsage().getText().getCompletion_tokens());
            usage.setTotal_tokens(responseBody.getPayload().getUsage().getText().getTotal_tokens());

            openAiResponseBody.setUsage(usage);
        }


        return JsonUtil.toJSONString(openAiResponseBody);
    }
}
