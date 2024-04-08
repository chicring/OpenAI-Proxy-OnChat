package com.hjong.OnChat.exception;

import com.hjong.OnChat.entity.enums.ServiceExceptionEnum;
import lombok.Getter;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/17
 **/

@Getter
public class ServiceException extends RuntimeException{

    public ServiceExceptionEnum serviceExceptionEnum;
    public ServiceException(ServiceExceptionEnum serviceExceptionEnum) {
        super(serviceExceptionEnum.getMsg());
        this.serviceExceptionEnum = serviceExceptionEnum;
    }
}
