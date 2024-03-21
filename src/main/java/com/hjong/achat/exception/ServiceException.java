package com.hjong.achat.exception;

import com.hjong.achat.enums.ServiceExceptionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
