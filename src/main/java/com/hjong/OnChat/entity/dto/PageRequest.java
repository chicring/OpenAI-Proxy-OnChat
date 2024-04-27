package com.hjong.OnChat.entity.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageRequest {
    @Min(value = 1, message = "page不能小于1")
    private Integer page;

    @Min(value = 5, message = "size不能小于5")
    @Max(value = 20, message = "size不能大于20")
    private Integer size;

    private String search;
}
