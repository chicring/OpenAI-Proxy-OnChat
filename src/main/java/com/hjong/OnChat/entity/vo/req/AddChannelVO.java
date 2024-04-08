package com.hjong.OnChat.entity.vo.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/18
 **/

@Data
public class AddChannelVO {
    @Length(min = 2, max = 15, message = "名称应该在2-15个字符之间")
    @NotEmpty(message = "name不能为空")
    private String name;

    @NotEmpty(message = "类型不能为空")
    private String type;

    @Length(min = 3, message = "请填写正确的key")
    @NotEmpty(message = "key不能为空")
    private String apiKey;

    @Length(min = 3, message = "请填写正确的url")
    @NotEmpty(message = "url不能为空")
    private String baseUrl;

    @NotEmpty(message = "模型不能为空")
    private String models;

    @Min(value = 0,message = "优先级最小为1")
    private Integer priority;

    private boolean enableProxy;
}
