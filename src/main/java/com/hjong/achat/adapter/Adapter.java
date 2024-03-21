package com.hjong.achat.adapter;

import com.hjong.achat.adapter.openai.OpenAiRequestBody;
import com.hjong.achat.adapter.openai.OpenAiResponseBody;
import com.hjong.achat.entity.DTO.Channel;
import com.hjong.achat.enums.ChannelType;
import com.hjong.achat.enums.ChatRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hjong.achat.enums.UrlEnums.getUrlByType;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@Slf4j
public abstract class Adapter {

    private final WebClient webClient;
    private final WebClient webClientEnableProxy;

    protected String url = null;
    protected String model = null;
    protected String apiKey = null;

    public Adapter(WebClient webClient, WebClient webClientEnableProxy) {
        this.webClient = webClient;
        this.webClientEnableProxy = webClientEnableProxy;
    }


    protected abstract Flux<String> completions(OpenAiRequestBody request, Channel channel, WebClient webClient);

    protected abstract Flux<String> streamCompletions(OpenAiRequestBody request, Channel channel, WebClient webClient);

    public Flux<String> sendMessage(OpenAiRequestBody request, Channel channel){

        WebClient webClientUse = channel.isEnableProxy() ? webClientEnableProxy : webClient;

        url = channel.getBaseUrl() + getUrlByType(channel.getType());

        if(channel.getType().equals("gemini")){
            url = url + channel.getModel() + ":streamGenerateContent?key=" + channel.getApiKey();
        }

        log.debug("请求类型：{},请求站点：{}",channel.getType(),url);
        //设置渠道选择的模型
        model = channel.getModel();
        request.setModel(channel.getModel());
        //判断是否是openai的type, 非openai接口需要将System改成User
        if(!channel.getType().equals(ChannelType.OPEN_AI.getType())){List<OpenAiRequestBody.Message> messages = new ArrayList<>();
            List<OpenAiRequestBody.Message> originalMessages = request.getMessages();
            int size = originalMessages.size();

            for (int i = 0; i < size; i++) {
                OpenAiRequestBody.Message message = originalMessages.get(i);
                if(message.getRole().equals(ChatRoleEnum.SYSTEM.getRole())){
                    messages.add(message.builder(ChatRoleEnum.USER.getRole(), message.getContent()));
                    // 如果当前消息不是最后一个消息，那么添加一个新的助手消息
                    if (i < size - 1) {
                        messages.add(message.builder(ChatRoleEnum.ASSISTANT.getRole(), "好的"));
                    }
                }else {
                    messages.add(message);
                }
            }

            request.setMessages(messages);
        }

        Flux<String> flux;

        //选择是否启用流式响应
        if(request.isStream()){
            flux = streamCompletions(request,channel,webClientUse);
            return flux;
        }else {
            return completions(request,channel,webClientUse);
        }
    }

    public OpenAiResponseBody modifyBody(OpenAiResponseBody response){

        String id = UUID.randomUUID().toString();
        response.setId(id);
        response.setObject("chat.completion.chunk");
        response.setCreated(Instant.now().getEpochSecond());
        response.setModel(model);

        response.getChoices().getFirst().setIndex(0);
        response.getChoices().getFirst().setFinish_reason("stop");

        OpenAiResponseBody.Usage usage = new OpenAiResponseBody.Usage();

        usage.setPrompt_tokens(0);
        usage.setCompletion_tokens(0);
        usage.setTotal_tokens(0);

        response.setUsage(usage);

        return response;
    }
}
