package com.hjong.OnChat.entity.dto;

import lombok.Data;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/28
 **/
@Data
public class Server {
    private String coreCount;
    private String cpuSpeed;
    private String cpuUtilization;
    private String memoryUsed;
    private String memoryTotal;
    private double memoryUsage;
    private String RunTimeProject;
    private String RunTimeOS;
}
