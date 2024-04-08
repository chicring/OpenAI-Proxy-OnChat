package com.hjong.OnChat.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/29
 **/


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mail {

    private String title;
    private String content;
    private String email;

    /**
     * 邮件类型 text | html
     */
    private String type;

}
