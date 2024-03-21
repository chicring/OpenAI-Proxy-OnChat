package com.hjong.achat.entity.VO.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/
@Data
public class UserRegisterVO {
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 3, max = 15, message = "名称应该在3-15个字符之间")
    private String username;

    @NotEmpty(message = "密码不能为空")
    @Length(min = 3, max = 15, message = "密码应该在3-15个字符之间")
    private String password;

    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "邮箱不正确")
    private String email;
}
