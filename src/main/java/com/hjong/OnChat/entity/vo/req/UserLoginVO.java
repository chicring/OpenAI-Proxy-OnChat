package com.hjong.OnChat.entity.vo.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/

@Data
public class UserLoginVO {

    @Length(min = 2, max = 30, message = "名称应该在2-15个字符之间")
    @NotEmpty(message = "账号不能为空")
    String account;  //用户名或邮箱

    @Length(min = 5, max = 20, message = "密码应该在5-15个字符之间")
    @NotEmpty(message = "密码不能为空")
    String pwd;
}
