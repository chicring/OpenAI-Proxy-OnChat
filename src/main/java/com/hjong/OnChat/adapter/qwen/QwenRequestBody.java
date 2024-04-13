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
        // qwen top_p 不能大于等于1
//        double top_p = openAiRequestBody.getTop_p();
//        if(top_p > 1 || top_p == 1 ){
//            parameters.setTop_p(0.9);
//        }else {
//            parameters.setTop_p(openAiRequestBody.getTop_p());
//        }
        parameters.setTop_p(openAiRequestBody.getTop_p());
        //token不能大于1500
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

        parameters.setStop(openAiRequestBody.getStop() == null ? "null" : openAiRequestBody.getStop());
        // 如果有工具，开启联网搜索
        if(openAiRequestBody.getModel().startsWith(OPEN_WEB_SEARCH)){
            parameters.setEnable_search(true);
            qwenRequestBody.setModel(openAiRequestBody.getModel().substring(4));
        }

        qwenRequestBody.setParameters(parameters);

        return qwenRequestBody;
    }
}


