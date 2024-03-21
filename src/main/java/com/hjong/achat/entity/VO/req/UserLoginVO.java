package com.hjong.achat.entity.VO.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/

@Data
public class UserLoginVO {
    @NotEmpty(message = "账号不能为空")
    String account;  //用户名或邮箱
    @NotEmpty(message = "密码不能为空")
    String pwd;
}
