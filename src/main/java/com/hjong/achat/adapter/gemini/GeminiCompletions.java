package com.hjong.achat.adapter.gemini;

import com.fasterxml.jackson.databind.JsonNode;
import com.hjong.achat.adapter.Adapter;
import com.hjong.achat.adapter.openai.OpenAiRequestBody;
import com.hjong.achat.adapter.openai.OpenAiResponseBody;
import com.hjong.achat.entity.DTO.Channel;
import com.hjong.achat.enums.ChatRoleEnum;
import com.hjong.achat.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Slf4j
@Component("gemini")
public class GeminiCompletions extends Adapter {
    public GeminiCompletions(WebClient webClient, WebClient webClientEnableProxy) {
        super(webClient, webClientEnableProxy);
    }

    @Override
    protected Flux<String> completions(OpenAiRequestBody request, Channel channel, WebClient webClient) {
        return null;
    }


    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request, Channel channel,WebClient webClient) {


        return webClient.post()
                .uri(url)
                .bodyValue(this.convertRequest(request))
                .retrieve()
                .bodyToFlux(String.class)
                .filter((str) -> str.contains("text"))
                .flatMap(this::convertResponse)
                .concatWithValues("[DONE]");

    }

    private GeminiRequestBody convertRequest(OpenAiRequestBody request){

        GeminiRequestBody geminiRequestBody = new GeminiRequestBody();
        //设置配置类
        GeminiRequestBody.GenerationConfig generationConfig = new GeminiRequestBody.GenerationConfig();
        generationConfig.setTemperature(request.getTemperature());
        generationConfig.setTopK(2);
        generationConfig.setTopP(request.getTop_p());
        generationConfig.setMaxOutputTokens(request.getMax_tokens());
        generationConfig.setStopSequences(List.of());

        //转换消息
        List<GeminiRequestBody.Content> contents = new ArrayList<>();

        request.getMessages().forEach( message -> {
            GeminiRequestBody.Content newContent = new GeminiRequestBody.Content();

            if(message.getRole().equals(ChatRoleEnum.ASSISTANT.getRole())){
                //将角色改成model
                newContent.setRole(ChatRoleEnum.MODEL.getRole());
            }else {
                newContent.setRole(ChatRoleEnum.USER.getRole());
            }
            GeminiRequestBody.Part newPart = new GeminiRequestBody.Part();
            newPart.setText(message.getContent());
            newContent.setParts(Collections.singletonList(newPart));

            contents.add(newContent);
        });

        geminiRequestBody.setContents(contents);
        geminiRequestBody.setGenerationConfig(generationConfig);

        return geminiRequestBody;
    }

    private Flux<String> convertResponse(String text) {

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

        return Flux.just(JsonUtil.toJSONString(super.modifyBody(openAIResponseBody)));
    }
}
