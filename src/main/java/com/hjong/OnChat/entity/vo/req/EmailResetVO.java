package com.hjong.OnChat.entity.vo.req;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/12
 **/

@Data
public class EmailResetVO {
    @Email
    String email;
    @Length(max = 6, min = 6)
    String code;
    @Length(min = 5, max = 20)
    String password;
}
