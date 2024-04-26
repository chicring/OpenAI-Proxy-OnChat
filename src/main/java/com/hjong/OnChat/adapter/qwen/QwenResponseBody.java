package com.hjong.OnChat.adapter.qwen;

import com.hjong.OnChat.adapter.openai.OpenAiResponseBody;
import com.hjong.OnChat.util.JsonUtil;
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

        private List<Choices> choices;

        @Data
        public static class  Choices{
            private Message message;
            private String finish_reason;

            @Data
            public static class Message{
                private String role;
                private String content;
                private List<Tool_calls> tool_calls;
            }

            @Data
            public static class Tool_calls{
                private String id;
                private String type;
                private Function function;

                @Data
                public static class Function{
                    private String name;
                    private String arguments;
                }
            }

        }
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
        message.setRole(responseBody.getOutput().getChoices().getFirst().getMessage().getRole());
        message.setContent(responseBody.getOutput().getChoices().getFirst().getMessage().getContent());

        if(responseBody.getOutput().getChoices().getFirst().getMessage().getTool_calls() != null){
            message.setTool_calls(
                    responseBody.getOutput().getChoices().getFirst().getMessage().getTool_calls()
                            .stream().map(tool -> {
                                OpenAiResponseBody.Message.Tool_calls toolCalls = new OpenAiResponseBody.Message.Tool_calls();
                                toolCalls.setType(tool.getType());
                                toolCalls.setId(tool.getId());
                                OpenAiResponseBody.Message.Tool_calls.Function function = new OpenAiResponseBody.Message.Tool_calls.Function();
                                function.setName(tool.getFunction().getName());
                                function.setArguments(tool.getFunction().getArguments());
                                toolCalls.setFunction(function);
                                return toolCalls;
                            }).toList()
            );
        }


        choices.setMessage(message);
        choices.setDelta(message);
        choicesList.add(choices);
        openAiResponseBody.setChoices(choicesList);

        openAiResponseBody.setId(responseBody.getRequest_id());
        openAiResponseBody.setObject("chat.completion.chunk");
        openAiResponseBody.setCreated(Instant.now().getEpochSecond());
        openAiResponseBody.setModel(model);

        openAiResponseBody.getChoices().getFirst().setIndex(0);
        openAiResponseBody.getChoices().getFirst().setFinish_reason(responseBody.getOutput().getChoices().getFirst().getFinish_reason());

        OpenAiResponseBody.Usage usage = new OpenAiResponseBody.Usage();

        usage.setPrompt_tokens(responseBody.getUsage().getInput_tokens());
        usage.setCompletion_tokens(responseBody.getUsage().getOutput_tokens());
        usage.setTotal_tokens(responseBody.getUsage().getTotal_tokens());

        openAiResponseBody.setUsage(usage);

        return Flux.just(JsonUtil.toJSONString(openAiResponseBody));
    }
}
