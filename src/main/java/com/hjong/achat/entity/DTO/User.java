package com.hjong.achat.entity.DTO;

import com.hjong.achat.enums.RoleType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@Table("User")
@Data
public class User {
    @Id
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String role;
    private int currentUsage;
    private int totalUsage;
    private boolean isActive;
    private Instant registerDate;
}
