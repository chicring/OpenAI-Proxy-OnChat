package com.hjong.OnChat.entity.dto;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Table("Channel")
@Accessors(chain = true)
@Data
public class Channel {
    @Id
    private Integer id;
    private String name;
    private Boolean enabled;
    private String type;
    private String apiKey;
    private String baseUrl;
    private String models;
    private String model;
    private Integer priority;
    private Integer usage;
    private boolean enableProxy;
    private Long createdAt;
}
