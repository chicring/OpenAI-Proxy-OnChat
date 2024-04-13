package com.hjong.OnChat.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/13
 **/

@Table("Model")
@Data
public class Model {
    @Id
    private Integer id;
    private String requestModel;
    private String realModel;
    private String description;
    private Integer channelId;
}
