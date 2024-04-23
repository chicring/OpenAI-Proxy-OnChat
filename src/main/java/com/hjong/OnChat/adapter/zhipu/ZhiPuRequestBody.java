package com.hjong.OnChat.adapter.zhipu;


import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hjong.OnChat.adapter.Consts.*;
import static com.hjong.OnChat.adapter.Consts.TOOL_WEB_SEARCH;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/11
 **/

@Data
public class ZhiPuRequestBody {
    private List<ZhiPuRequestBody.Message> messages;

    @Data
    public static class Message{
        private String role;
        private String content;
        private String tool_call_id;
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

    List<Map<String,Object>> tools;

    private String tool_choice;

    @Data
    public static class Tools{
        private String type;  //function | retrieval | web_search

        @Data
        public static class Web_search{
            private boolean enable; // 部分模型使用 true | false
            private String search_query;
        }

        @Data
        public static class Function{
            private String description;
            private String name;
            private Object parameters;
        }
    }

    public static ZhiPuRequestBody builder(OpenAiRequestBody openAiRequestBody){
        ZhiPuRequestBody zhiPuRequestBody = new ZhiPuRequestBody();

        zhiPuRequestBody.setModel(openAiRequestBody.getModel());
        zhiPuRequestBody.setStream(openAiRequestBody.isStream());
        zhiPuRequestBody.setTemperature(openAiRequestBody.getTemperature());
        zhiPuRequestBody.setPresence_penalty(openAiRequestBody.getPresence_penalty());
        zhiPuRequestBody.setFrequency_penalty(openAiRequestBody.getFrequency_penalty());
        zhiPuRequestBody.setTop_p(openAiRequestBody.getTop_p());
        zhiPuRequestBody.setTop_k(openAiRequestBody.getTop_k());
        zhiPuRequestBody.setStop(openAiRequestBody.getStop());
        zhiPuRequestBody.setMax_tokens(openAiRequestBody.getMax_tokens());
        zhiPuRequestBody.setTool_choice(openAiRequestBody.getTool_choice());

        List<ZhiPuRequestBody.Message> message = openAiRequestBody
                .getMessages()
                .stream().map(m -> {
                    ZhiPuRequestBody.Message newMessage = new ZhiPuRequestBody.Message();
                    newMessage.setRole(m.getRole());
                    newMessage.setContent(m.getContent());
                    return newMessage;
                }).toList();

        zhiPuRequestBody.setMessages(message);

        if (openAiRequestBody.getTools() !=null) {
            List<Map<String, Object>> tools = openAiRequestBody.getTools()
                    .stream().map(tool -> {

                        ZhiPuRequestBody.Tools.Function function = new ZhiPuRequestBody.Tools.Function();
                        function.setDescription(tool.getFunction().getDescription());
                        function.setName(tool.getFunction().getName());
                        function.setParameters(tool.getFunction().getParameters());

                        return Map.of(
                                TOOL_TYPE, tool.getType(),
                                TOOL_FUNCTION, function
                        );

                    }).toList();
            zhiPuRequestBody.setTools(tools);
        }


        if (openAiRequestBody.getModel().startsWith(OPEN_WEB_SEARCH)){
            Map<String, Object> search_tool = new HashMap<>();
            search_tool.put(TOOL_TYPE, TOOL_WEB_SEARCH);

            ZhiPuRequestBody.Tools.Web_search webSearch = new ZhiPuRequestBody.Tools.Web_search();
            webSearch.setEnable(true);

            search_tool.put(TOOL_WEB_SEARCH, webSearch);

            if (zhiPuRequestBody.getTools() == null) {
                zhiPuRequestBody.setTools(List.of(search_tool));
            } else {
                zhiPuRequestBody.getTools().add(search_tool);
            }
            zhiPuRequestBody.setModel(zhiPuRequestBody.getModel().substring(4));
        }

        return zhiPuRequestBody;
    }


}
