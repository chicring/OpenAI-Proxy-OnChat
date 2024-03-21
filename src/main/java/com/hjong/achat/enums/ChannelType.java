package com.hjong.achat.enums;

import lombok.Getter;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Getter
public enum ChannelType {

    OPEN_AI("openai"),

    GEMINI("gemini");

    private final String type;

    ChannelType(String type){
        this.type = type;
    }
}

