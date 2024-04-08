package com.hjong.OnChat.entity.enums;

import lombok.Getter;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Getter
public enum ChatRoleEnum {

    SYSTEM("system","系统"),
    USER("user","用户"),
    ASSISTANT("assistant","助手"),
    MODEL("model","模型")
    ;

    private final String role;

    private final String des;

    ChatRoleEnum(String role, String des){
        this.role = role;
        this.des = des;
    }

}