package com.hjong.OnChat.adapter.spark;

import com.hjong.OnChat.adapter.Adapter;
import com.hjong.OnChat.adapter.openai.OpenAiRequestBody;
import com.hjong.OnChat.util.JsonUtil;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.publisher.Sinks;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hjong.OnChat.adapter.spark.SparkResponseBody.SparkToOpenAI;
import static com.hjong.OnChat.entity.Constants.DONE;


/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/7
 **/
@Slf4j
@Component("spark")
public class SparkCompletions extends Adapter {

    @Resource
    WebSocketClient webSocketClient;

    @Override
    protected Flux<String> completions(OpenAiRequestBody request, String url, String key, Boolean enableProxy) {
        String[] parts = key.split("\\|");
        String appid = parts[0];
        String apiSecret = parts[1];
        String apikey = parts[2];
        String hostUrl = getHostUrl(url, request.getModel());
        String authUrl = this.getAuthUrl(hostUrl, apikey, apiSecret);
        String domain = getDomain(request.getModel());
        String requestJson = JsonUtil.toJSONString(SparkRequestBody.builder( appid,domain,request));

        StringBuilder output = new StringBuilder();

        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

        webSocketClient.execute(URI.create(authUrl), session ->
                session.send(Mono.just(session.textMessage(requestJson)))
                        .thenMany(session
                                .receive()
                                .mapNotNull(message -> JsonUtil.parseObject(message.getPayloadAsText(), SparkResponseBody.class)))
                        .doOnNext(response -> {
                            if (response.getHeader().getStatus() == 2) {
                                output.append(response.getPayload().getChoices().getText().getFirst().getContent());
                                response.getPayload().getChoices().getText().getFirst().setContent(output.toString());
                                sink.tryEmitNext(SparkToOpenAI(response, request.getModel()));
                            }else {
                                output.append(response.getPayload().getChoices().getText().getFirst().getContent());
                            }
                        })
                        .doFinally(signalType -> {
                            if (signalType.equals(reactor.core.publisher.SignalType.ON_COMPLETE) || signalType.equals(SignalType.ON_ERROR)) {
                                session.close();
                                sink.tryEmitComplete();
                            }
                        })
                        .then()).subscribe();
        return sink.asFlux();
    }

    @Override
    protected Flux<String> streamCompletions(OpenAiRequestBody request, String url, String key, Boolean enableProxy){
        String[] parts = key.split("\\|");
        String appid = parts[0];
        String apiSecret = parts[1];
        String apikey = parts[2];

        String hostUrl = getHostUrl(url, request.getModel());
        String authUrl = this.getAuthUrl(hostUrl, apikey, apiSecret);

        String domain = getDomain(request.getModel());

        String requestJson = JsonUtil.toJSONString(SparkRequestBody.builder( appid,domain,request));

        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        webSocketClient.execute(URI.create(authUrl), session ->
                        session.send(Mono.just(session.textMessage(requestJson)))
                                .thenMany(session
                                        .receive()
                                        .mapNotNull(message -> JsonUtil.parseObject(message.getPayloadAsText(), SparkResponseBody.class)))
                                .doOnNext(response -> {
                                    if (response.getHeader().getStatus() == 2) {
                                        sink.tryEmitNext(SparkToOpenAI(response, request.getModel()));
                                        sink.tryEmitNext(DONE);
                                        sink.tryEmitComplete();
                                    }else {
                                        sink.tryEmitNext(SparkToOpenAI(response, request.getModel()));
                                    }
                                })
                                .doFinally(signalType -> {
                                    if (signalType.equals(SignalType.ON_ERROR)) {
                                        sink.tryEmitComplete();
                                        session.close();
                                    }
                                })
                                .then()).subscribe();
        return sink.asFlux();
    }

    private String getDomain(String model) {
        return switch (model) {
            case "Spark-v1.1" -> "general";
            case "Spark-v2.1" -> "generalv2";
            case "Spark-v3.1" -> "generalv3";
            case "Spark-v3.5" -> "generalv3.5";
            default -> "general";
        };
    }


    private String getHostUrl(String baseurl, String model) {
        //model格式：Spark-v1.1
        String[] parts = model.split("-");
        String version = parts[1];

        return baseurl + "/"+ version + "/chat";

    }

    @SneakyThrows
    public String getAuthUrl(String hostUrl, String apiKey, String apiSecret){

        // key : appid|apiSecret|apikey
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        String encodedDate = java.net.URLEncoder.encode(date, StandardCharsets.UTF_8);

        //
        URI url = new URI(hostUrl);
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";

        //
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));

        //
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        Map<String, String> params = new HashMap<>();
        params.put("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8)));
        params.put("date", encodedDate);
        params.put("host", url.getHost());

        String authUrl = hostUrl + "?" + params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).reduce((a, b) -> a + "&" + b).orElse("");


        return authUrl.replace("http://", "ws://").replace("https://", "wss://");
    }
}
