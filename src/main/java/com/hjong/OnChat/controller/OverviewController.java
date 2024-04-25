package com.hjong.OnChat.controller;

import com.hjong.OnChat.entity.Result;
import com.hjong.OnChat.entity.dto.overview.ApiRequestCount;
import com.hjong.OnChat.entity.dto.overview.ModelUsageStats;
import com.hjong.OnChat.entity.dto.overview.TotalModelUsageRatio;
import com.hjong.OnChat.entity.dto.overview.UserUsageRatio;
import com.hjong.OnChat.filter.annotation.CheckRole;
import com.hjong.OnChat.service.OverviewService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static com.hjong.OnChat.entity.Consts.*;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/5
 **/

@Validated
@RestController
@RequestMapping("/overview")
public class OverviewController {

    @Resource
    OverviewService overviewService;

    @GetMapping("/request-Count")
    Mono<Result<ApiRequestCount>> getApiRequestCount(@RequestHeader(USER_ID) Integer userId,
                                                     @RequestHeader(ROLE) String role){

        if (role.equals(USER_ROLE)) {
            return overviewService.getApiRequestCount(userId)
                    .map(Result::ok);
        } else {
            return overviewService.getApiRequestCount(null)
                    .map(Result::ok);
        }

    }

    @GetMapping("/model-usage-stats")
    Mono<Result<ModelUsageStats>> getModelUsageStats(@RequestHeader(USER_ID) Integer userId,
                                                     @RequestHeader(ROLE) String role){
        if (role.equals(USER_ROLE)) {
            return overviewService.getModelUsageStats(userId)
                    .map(Result::ok);
        } else {
            return overviewService.getModelUsageStats(null)
                    .map(Result::ok);
        }
    }

    @GetMapping("/model-usage-ratio")
    Mono<Result<TotalModelUsageRatio>> getTotalModelUsageRatio(@RequestHeader(USER_ID) Integer userId,
                                                               @RequestHeader(ROLE) String role,
                                                               @NotNull @RequestParam long timestamp){
        if (role.equals(USER_ROLE)) {
            return overviewService.getTotalModelUsageRatio(userId, timestamp)
                    .map(Result::ok);
        } else {
            return overviewService.getTotalModelUsageRatio(null, timestamp)
                    .map(Result::ok);
        }

    }

    @CheckRole
    @GetMapping("/user-usage-ratio")
    Mono<Result<UserUsageRatio>> getUserUsageRatio(@NotNull @RequestParam long timestamp){
        return overviewService.getUserUsageRatio(timestamp)
                .map(Result::ok);
    }

    @CheckRole
    @GetMapping("/user-count")
    Mono<Result<Map<String, Long>>> getCountOfUsersByRole(){
        return overviewService.getCountOfUsersByRole()
                .map(Result::ok);
    }

    @GetMapping("/all-models")
    Mono<Result<List<String>>> getAllAvailableModels(){
        return overviewService.getAllAvailableModels()
                .map(Result::ok);
    }

}
