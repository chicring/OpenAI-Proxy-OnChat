package com.hjong.OnChat.adapter.ai360;


import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hjong.OnChat.adapter.Consts.*;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/11
 **/

@Data
public class Ai360RequestBody {
    private List<Ai360RequestBody.Message> messages;

    @Data
    public static class Message{
        private String role;
        private String content;

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
        private String type;  //function | retrieval | web_search

        private Web_search web_search;

        @Data
        public static class Web_search{
            private String search_mode;  //always | auto | none
            private String search_query;
        }

        @Data
        public static class Function{
            private String description;
            private String name;
            private Object parameters;
        }
    }

    public static Ai360RequestBody builder(OpenAiRequestBody openAiRequestBody){
        Ai360RequestBody ai360RequestBody = new Ai360RequestBody();

        ai360RequestBody.setModel(openAiRequestBody.getModel());
        ai360RequestBody.setStream(openAiRequestBody.isStream());
        ai360RequestBody.setTemperature(openAiRequestBody.getTemperature());
        ai360RequestBody.setPresence_penalty(openAiRequestBody.getPresence_penalty());
        ai360RequestBody.setFrequency_penalty(openAiRequestBody.getFrequency_penalty());
        ai360RequestBody.setTop_p(openAiRequestBody.getTop_p());
        ai360RequestBody.setTop_k(openAiRequestBody.getTop_k());
        ai360RequestBody.setStop(openAiRequestBody.getStop());
        ai360RequestBody.setMax_tokens(openAiRequestBody.getMax_tokens());
        ai360RequestBody.setTool_choice(openAiRequestBody.getTool_choice());

        List<Message> message = openAiRequestBody
                                    .getMessages()
                                    .stream().map(m -> {
                                        Message newMessage = new Message();
                                        newMessage.setRole(m.getRole());
                                        newMessage.setContent(m.getContent());
                                        return newMessage;
                                    }).toList();

        ai360RequestBody.setMessages(message);

        if (openAiRequestBody.getModel().startsWith(OPEN_WEB_SEARCH)){

            Ai360RequestBody.Tools tool = new Ai360RequestBody.Tools();
            tool.setType(TOOL_WEB_SEARCH);

            Tools.Web_search webSearch = new Tools.Web_search();
            webSearch.setSearch_mode(SEARCH_MODE_AUTO);

            tool.setWeb_search(webSearch);

            if (ai360RequestBody.getTools() == null) {
                ai360RequestBody.setTools(List.of(tool));
            } else {
                ai360RequestBody.getTools().add(tool);
            }
            ai360RequestBody.setModel(ai360RequestBody.getModel().substring(4));
        }

        return ai360RequestBody;
    }
}
