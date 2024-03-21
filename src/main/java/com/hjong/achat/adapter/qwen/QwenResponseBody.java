package com.hjong.achat.adapter.qwen;

import com.hjong.achat.adapter.openai.OpenAiResponseBody;
import com.hjong.achat.enums.ChatRoleEnum;
import com.hjong.achat.util.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/
@Slf4j
@Data
public class QwenResponseBody {

    private Output output;
    private Usage usage;
    private String request_id;

    @Data
    public static class Output {
        private String finish_reason;
        private String text;
    }

    @Data
    public static class Usage {
        private int total_tokens;
        private int input_tokens;
        private int output_tokens;
    }

    public static Flux<String> QwenToOpenAI(QwenResponseBody responseBody,String model){

        OpenAiResponseBody openAiResponseBody = new OpenAiResponseBody();

        List<OpenAiResponseBody.Choices> choicesList = new ArrayList<>();

        OpenAiResponseBody.Choices choices = new OpenAiResponseBody.Choices();

        OpenAiResponseBody.Message message = new OpenAiResponseBody.Message();
        message.setRole(ChatRoleEnum.ASSISTANT.getRole());
        message.setContent(responseBody.getOutput().getText());
        choices.setMessage(message);
        choices.setDelta(message);
        choicesList.add(choices);
        openAiResponseBody.setChoices(choicesList);

        openAiResponseBody.setId(responseBody.getRequest_id());
        openAiResponseBody.setObject("chat.completion.chunk");
        openAiResponseBody.setCreated(Instant.now().getEpochSecond());
        openAiResponseBody.setModel(model);

        openAiResponseBody.getChoices().getFirst().setIndex(0);
        openAiResponseBody.getChoices().getFirst().setFinish_reason("stop");

        OpenAiResponseBody.Usage usage = new OpenAiResponseBody.Usage();

        usage.setPrompt_tokens(responseBody.getUsage().getInput_tokens());
        usage.setCompletion_tokens(responseBody.getUsage().getOutput_tokens());
        usage.setTotal_tokens(responseBody.getUsage().getTotal_tokens());

        openAiResponseBody.setUsage(usage);

        return Flux.just(JsonUtil.toJSONString(openAiResponseBody));
    }
}
