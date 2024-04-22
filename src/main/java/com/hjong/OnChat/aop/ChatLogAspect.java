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

        log.info("输入: {}", requestBody.getMessages().getLast().getContent());
        //执行目标方法
        Flux<Object> result = (Flux<Object>) joinPoint.proceed();

        return result.doOnError(error -> {
            log.error("error: " + error);
        }).doOnNext(json -> {
            if(requestBody.isStream()){
                if (!json.equals(DONE)) {
                    JsonNode jsonNode = JsonUtil.parseJSONObject((String) json);
                    if (jsonNode.get("choices").get(0).get("delta").has("content")) {
                        output.append(jsonNode.get("choices").get(0).get("delta").get("content").toString());
                    }
                }
            }else {
                JsonNode jsonNode = JsonUtil.parseJSONObject((String) json);
                output.append(jsonNode.get("choices").get(0).get("message").get("content").toString());
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
                    );
            saveLog.subscribeOn(Schedulers.boundedElastic()).subscribe();

        });
    }


}
