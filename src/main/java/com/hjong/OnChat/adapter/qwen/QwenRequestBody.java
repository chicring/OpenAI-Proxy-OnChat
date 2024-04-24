package com.hjong.OnChat.adapter.qwen;

import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.hjong.OnChat.adapter.Consts.OPEN_WEB_SEARCH;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/

@Slf4j
@Data
public class QwenRequestBody {
    private Input input;
    private String model;
    private Parameters parameters;

    @Data
    public static class Input {
        private List<Message> messages;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
        private String name;

        public static List<Message> builder(List<OpenAiRequestBody.Message> messages){

            return messages.stream().map(message -> {
                Message m = new Message();
                m.setRole(message.getRole());
                m.setContent(message.getContent());
                return m;
            }).toList();
        }
    }

    @Data
    public static class Parameters {
        private Float temperature;
        private Float top_p;
        private Integer max_tokens;
        private String incremental_output;
        private String stop;
        private boolean enable_search;
        private List<Tools> tools;
        private String result_format;

        @Data
        private static class Tools {
            private String type;
            private Function function;

            @Data
            private static class Function {
                private String description;
                private String name;
                private Object parameters;
            }
        }
    }

    public static QwenRequestBody builder(OpenAiRequestBody openAiRequestBody){
        QwenRequestBody qwenRequestBody = new QwenRequestBody();

        qwenRequestBody.setModel(openAiRequestBody.getModel());

        Input input = new Input();
        input.setMessages(Message.builder(openAiRequestBody.getMessages()));
        qwenRequestBody.setInput(input);
        qwenRequestBody.setModel(openAiRequestBody.getModel());

        Parameters parameters = new Parameters();
        parameters.setTemperature(openAiRequestBody.getTemperature());

        parameters.setTop_p(openAiRequestBody.getTop_p());
        parameters.setResult_format("message");

        Integer max_tokens = openAiRequestBody.getMax_tokens();
        if(max_tokens != null && max_tokens > 1500) {
            parameters.setMax_tokens(1500);
        } else if(max_tokens == null) {

        } else {
            parameters.setMax_tokens(openAiRequestBody.getMax_tokens());
        }

        if(openAiRequestBody.isStream()){
            parameters.setIncremental_output("true");
        }

        parameters.setStop(openAiRequestBody.getStop());

        if(!openAiRequestBody.getTools().isEmpty()) {
            List<Parameters.Tools> tools = openAiRequestBody.getTools()
                    .stream().map(tool -> {
                        Parameters.Tools newTool = new Parameters.Tools();
                        newTool.setType(tool.getType());
                        Parameters.Tools.Function function = new Parameters.Tools.Function();
                        function.setDescription(tool.getFunction().getDescription());
                        function.setName(tool.getFunction().getName());
                        function.setParameters(tool.getFunction().getParameters());
                        newTool.setFunction(function);
                        return newTool;
                    }).toList();
            parameters.setTools(tools);
        }

        // 如果模型以web-开头，开启联网搜索
        if(openAiRequestBody.getModel().startsWith(OPEN_WEB_SEARCH)){
            parameters.setEnable_search(true);
            qwenRequestBody.setModel(openAiRequestBody.getModel().substring(4));
        }

        qwenRequestBody.setParameters(parameters);

        return qwenRequestBody;
    }
}


