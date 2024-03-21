package com.hjong.achat.entity;

import com.hjong.achat.enums.ServiceExceptionEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    private Integer code;
    private String msg;
    private T data;

    public static final int SUCCESS = 200;
    public static <T> Result<T> ok() {
        return restResult(null, SUCCESS, "操作成功");
    }

    public static <T> Result<T> ok(T data) {
        return restResult(data, SUCCESS, "操作成功");
    }

    public static <T> Result<T> ok(String msg) {
        return restResult(null, SUCCESS, msg);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> Result<T> fail() {
        return restResult(null, ServiceExceptionEnum.SERVICE_EXCEPTION.getCode(), ServiceExceptionEnum.SERVICE_EXCEPTION.getMsg());
    }

    public static <T> Result<T> fail(ServiceExceptionEnum serviceExceptionEnum) {
        return restResult(null, serviceExceptionEnum.getCode(), serviceExceptionEnum.getMsg());
    }

    public static <T> Result<T> fail(ServiceExceptionEnum serviceExceptionEnum, T data) {
        return restResult(data, serviceExceptionEnum.getCode(), serviceExceptionEnum.getMsg());
    }
    public static <T> Result<T> fail(T data) {
        return restResult(data, 500, "操作失败");
    }
    
    private static <T> Result<T> restResult(T data, int code, String msg) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }


}
