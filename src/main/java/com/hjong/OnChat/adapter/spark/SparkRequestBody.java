package com.hjong.OnChat.adapter.spark;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import com.hjong.OnChat.adapter.qwen.QwenRequestBody;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/7
 **/

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SparkRequestBody {

    private static final Logger log = LoggerFactory.getLogger(SparkRequestBody.class);
    private Header header;
    private parameter parameter;
    private Payload payload;

    @Data
    public static class Header {
        private String app_id;
        private String uid;
    }

    @Data
    public static class parameter {
        private Chat chat;
    }

    @Data
    public static class Chat {
        //取值为[general,generalv2,generalv3,generalv3.5]
        private String domain;
        private Float temperature;
        private Integer max_tokens;
        //取值为[1，6],默认为4
        private Integer top_k;
        private String chat_id;
    }

    @Data
    public static class Payload{
        private Message message;
    }

    @Data
    public static class Message{
        private List<Text> text;
    }

    @Data
    public static class Text{
        private String role;
        private String content;
    }

    public static SparkRequestBody builder(String app_id, String domain, OpenAiRequestBody request){
        SparkRequestBody sparkRequestBody = new SparkRequestBody();
        SparkRequestBody.Header header = new SparkRequestBody.Header();
        header.setApp_id(app_id);
//        header.setApp_id();
        sparkRequestBody.setHeader(header);

        SparkRequestBody.parameter parameter = new SparkRequestBody.parameter();
        SparkRequestBody.Chat chat = new SparkRequestBody.Chat();
        chat.setDomain(domain);

        if(request.getTemperature() == null){
            chat.setTemperature(0.5f);
        }else {
            chat.setTemperature(request.getTemperature());

        }

        if (request.getMax_tokens() != null) {
            chat.setMax_tokens(request.getMax_tokens());
        }else {
            chat.setMax_tokens(2048);
        }

        chat.setTop_k(request.getTop_k());

//        chat.setChat_id(chat_id);
        parameter.setChat(chat);
        sparkRequestBody.setParameter(parameter);

        SparkRequestBody.Payload payload = new SparkRequestBody.Payload();
        SparkRequestBody.Message message = new SparkRequestBody.Message();

        message.setText( request.getMessages().stream().map(msg -> {
            SparkRequestBody.Text m = new SparkRequestBody.Text();
            m.setRole(msg.getRole());
            m.setContent(msg.getContent());
            return m;
        }).toList());

        payload.setMessage(message);
        sparkRequestBody.setPayload(payload);

        return sparkRequestBody;
    }
}
