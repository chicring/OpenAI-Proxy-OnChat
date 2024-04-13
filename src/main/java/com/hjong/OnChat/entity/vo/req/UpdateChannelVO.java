package com.hjong.OnChat.entity.vo.req;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/29
 **/
@Data
public class UpdateChannelVO {

    @NotEmpty(message = "id不能为空")
    private Integer id;
    @NotEmpty(message = "name不能为空")
    private String name;
    @NotEmpty(message = "key不能为空")
    private String apiKey;
    @NotEmpty(message = "url不能为空")
    private String baseUrl;

    //添加的模型列表
    private List<String> add;

    //删除的模型列表
    private List<String> remove;

    private Integer priority;
    private boolean enableProxy;
}
