package com.hjong.OnChat.entity.enums;

import lombok.Getter;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/
@Getter
public enum ServiceExceptionEnum {
    /**
     * 服务异常
     */
    SERVICE_EXCEPTION(500, "服务异常"),
    /**
     * 操作失败
     */
    INVALID_ACTION(500, "操作失败"),
    /**
     * 参数异常
     */
    PARAM_EXCEPTION(400, "参数异常"),
    /**
     * 用户不存在
     */
    USER_NOT_EXIST(404, "用户不存在"),
    /**
     * 渠道不存在
     */
    CHANNEL_NOT_EXIST(404, "模型不可用"),
    /**
     * 用户已存在
     */
    USER_EXIST(400, "用户名或邮箱已存在"),
    /**
     * 用户名或密码错误
     */
    USER_OR_PASSWORD_ERROR(400, "用户名或密码错误"),
    /**
     * 无效的API Key
     */
    INVALID_API_KEY(401, "token验证失败"),
    /**
     * 权限不足
     */
    PERMISSION_DENIED(403, "权限不足"),
    /**
     * 密码或账号错误
     */
    PASSWORD_ERROR(401, "密码或账号错误"),
    /**
     * 请求过于频繁
     */
    Too_Many_Requests(429, "请求过于频繁"),
    /**
     *
     */
    CODE_ERROR(400, "验证码错误"),
    /**
     * 未知异常
     */
    UNKNOWN_EXCEPTION(500, "未知异常");

    private final Integer code;
    private final String msg;

    ServiceExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
