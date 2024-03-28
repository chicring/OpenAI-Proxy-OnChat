package com.hjong.achat.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/

@Table("ApiKey")
@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKey {
    @Id
    private Integer id;
    private String name;
    private String apiKey;
    private Boolean enabled;
    private Integer userId;
    private Long createdAt;
    private Long expiresAt;
}