package com.hjong.OnChat.entity.vo.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/27
 **/
@Data
public class SaveApikeyVO {
    @NotEmpty(message = "name不能为空")
    @Length(min = 3, max = 15, message = "名称应该在3-15个字符之间")
    private String name;


    private Long expiresAt;
}
