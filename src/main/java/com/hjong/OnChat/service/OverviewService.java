package com.hjong.OnChat.service;

import com.hjong.OnChat.entity.dto.overview.ApiRequestCount;
import com.hjong.OnChat.entity.dto.overview.ModelUsageStats;
import com.hjong.OnChat.entity.dto.overview.TotalModelUsageRatio;
import com.hjong.OnChat.entity.dto.overview.UserUsageRatio;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/5
 **/


public interface OverviewService {

    /*
       查询过去7天的API请求次数
     */
    Mono<ApiRequestCount> getApiRequestCount(Integer useId);

    /*
       查询过去7天的模型使用占比
     */
    Mono<ModelUsageStats> getModelUsageStats(Integer userId);

    /*
       根据用户ID和日期查询一个月内模型使用占比
     */
    Mono<TotalModelUsageRatio> getTotalModelUsageRatio(Integer userId, long date);

    /*
       根据日期查询用户使用占比
     */
    Mono<UserUsageRatio> getUserUsageRatio(long date);

    /*
       查询用户角色数量
     */
    Mono<Map<String, Long>> getCountOfUsersByRole();

    Mono<List<String>> getAllAvailableModels();
}
