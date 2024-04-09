package com.hjong.OnChat.entity.enums;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/


public enum UrlEnums {

    TEXT_OPENAI("openai", "/v1/chat/completions"),

    //需要后面补全
    TEXT_GEMINI("gemini", "/v1beta/models/"),

    TEXT_QWEN("qwen","/api/v1/services/aigc/text-generation/generation"),

    TEXT_ZHIPU("zhipu","/api/paas/v4/chat/completions"),

    TEXT_Ai360("ai360","/v1/chat/completions"),

    TEXT_MOONSHOT("moonshot","/v1/chat/completions");



    private final String type;
    private final String url;

    UrlEnums(String type, String url){
        this.type = type;
        this.url = url;
    }

    public static String getUrlByType(String type) {
        for (UrlEnums urlEnum : UrlEnums.values()) {
            if (urlEnum.type.equals(type)) {
                return urlEnum.url;
            }
        }
        return null;
    }
}
