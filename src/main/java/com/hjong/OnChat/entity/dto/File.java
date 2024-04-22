package com.hjong.OnChat.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/18
 **/

@Table("Files")
@Data
public class File {
    @Id
    private Integer id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Instant createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Instant updateTime;
    private String name;
    private Integer status;
    private Integer knowledgeBaseId;
}
