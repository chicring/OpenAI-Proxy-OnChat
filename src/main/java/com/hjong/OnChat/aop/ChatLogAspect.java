package com.hjong.OnChat.aop;

import com.fasterxml.jackson.databind.JsonNode;
import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import com.hjong.OnChat.entity.dto.Channel;
import com.hjong.OnChat.entity.dto.Logs;
import com.hjong.OnChat.service.LogService;
import com.hjong.OnChat.util.JsonUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.hjong.OnChat.entity.Consts.DONE;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/19
 **/
@Slf4j
@Aspect
@Component
public class ChatLogAspect {


    @Resource
    LogService logService;

    @Pointcut("execution(* com.hjong.OnChat.adapter.Adapter.sendMessage(..)))")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Flux<Object> aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        OpenAiRequestBody requestBody = (OpenAiRequestBody) joinPoint.getArgs()[0];
        Channel channel = (Channel) joinPoint.getArgs()[1];
        ServerWebExchange exchange = (ServerWebExchange) joinPoint.getArgs()[2];

        ServerHttpRequest request = exchange.getRequest();

        StringBuilder output = new StringBuilder();
        AtomicReference<Integer> completionTokens = new AtomicReference<>(0);
        AtomicReference<Integer> promptTokens = new AtomicReference<>(0);
        AtomicReference<Integer> totalTokens = new AtomicReference<>(0);

        log.info("输入: {}", requestBody.getMessages().getLast().getContent());

        @SuppressWarnings("unchecked")
        Flux<Object> result = (Flux<Object>) joinPoint.proceed();

        return result.doOnError(error -> {
            log.error("error: " + error);
        }).doOnNext(json -> {
                if (!json.equals(DONE)) {
                    JsonNode jsonNode = JsonUtil.parseJSONObject((String) json);

                    String finishReason = "null";
                    if (jsonNode.get("choices").get(0).has("finish_reason")) {
                        finishReason = jsonNode.get("choices").get(0).get("finish_reason").toString();
                    }

                    switch (finishReason) {
                        case "\"tool_calls\"":
                            log.debug("工具调用,不记录对话");
                            break;
                        case "\"max_tokens\"":
                            log.debug("达到最大token数,不记录对话");
                            break;
                        case "\"stop\"":
                            log.debug("达到stop条件,记录token数");
                            completionTokens.set(jsonNode.get("usage").get("completion_tokens").asInt());
                            promptTokens.set(jsonNode.get("usage").get("prompt_tokens").asInt());
                            totalTokens.set(jsonNode.get("usage").get("total_tokens").asInt());
                        case "\"\"":
                            if(!jsonNode.get("usage").isEmpty()){
                                completionTokens.set(jsonNode.get("usage").get("completion_tokens").asInt());
                                promptTokens.set(jsonNode.get("usage").get("prompt_tokens").asInt());
                                totalTokens.set(jsonNode.get("usage").get("total_tokens").asInt());
                            }
                        default:
                            if (jsonNode.get("choices").get(0).has("delta")) {
                                output.append(jsonNode.get("choices").get(0).get("delta").get("content").toString());
                            }else {
                                output.append(jsonNode.get("choices").get(0).get("message").get("content").toString());
                            }
                    }
                }
        }).doOnCancel(() -> {
            log.warn("流被取消");
        }).doOnComplete(() ->{
            log.info("开始保存对话日志");
            String outputStr = output.toString();
            outputStr = outputStr.replace("\"", "");
            log.info("输出: " + outputStr);
            log.info("耗时 {} 秒", (System.currentTimeMillis()-start)/ 1000.0);

            Integer userId = exchange.getAttribute("userId");

            Mono<Logs> saveLog = logService.saveLog(
                            new Logs()
                                    .setChannelId(channel.getId())
                                    .setChannelType(channel.getType())
                                    .setChannelName(channel.getName())
                                    .setUserId(userId)
                                    .setModel(requestBody.getModel())
                                    .setInputText(requestBody.getMessages().getLast().getContent())
                                    .setOutputText(outputStr)
                                    .setConsumeTime((System.currentTimeMillis()-start)/ 1000.0)
                                    .setCreatedAt(Instant.now().getEpochSecond())
                                    .setIp(Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress())
                                    .setCompletionTokens(completionTokens.get())
                                    .setPromptTokens(promptTokens.get())
                                    .setTotalToken(totalTokens.get())
                    );
            saveLog.subscribeOn(Schedulers.boundedElastic()).subscribe();

        });
    }


}
