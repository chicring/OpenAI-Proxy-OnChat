package com.hjong.achat.aop;

import com.hjong.achat.adapter.openai.OpenAiRequestBody;
import com.hjong.achat.entity.DTO.Channel;
import com.hjong.achat.entity.DTO.Logs;
import com.hjong.achat.service.LogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/19
 **/
@Slf4j
@Aspect
@Component
public class ChatLogAspect {

    /** 换行符 */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final Instant instant = Instant.now();
    @Resource
    LogService logService;

    @Pointcut("execution(* com.hjong.achat.adapter.Adapter.sendMessage(..)))")
    public void logPointCut() {

    }

    @Around("logPointCut()")
    public Flux<Object> aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("************************ Start ************************");
        long start = System.currentTimeMillis();

        OpenAiRequestBody requestBody = (OpenAiRequestBody) joinPoint.getArgs()[0];

        Flux<Object> result = (Flux<Object>) joinPoint.proceed();

        return result.doOnError(error -> {
            log.info("************************目标方法异常以后************************");
            log.info("error:" + error);
        }).doFinally(signalType -> {
            Channel channel = (Channel) joinPoint.getArgs()[1];
            log.info("耗时 {} 秒", (System.currentTimeMillis()-start)/ 1000.0);

            Mono<Logs> saveLog = logService.saveLog(
                            new Logs()
                                    .setChannelId(channel.getId())
                                    .setChannelType(channel.getType())
                                    .setChannelName(channel.getName())
                                    .setModel(requestBody.getModel())
                                    .setInputText(requestBody.getMessages().getLast().getContent())
                                    .setConsumeTime((System.currentTimeMillis()-start)/ 1000.0)
                                    .setCreatedAt(instant.getEpochSecond())
                    );
            saveLog.subscribeOn(Schedulers.boundedElastic()).subscribe();
            log.info("************************ End ************************" + LINE_SEPARATOR);
        });
    }


}
