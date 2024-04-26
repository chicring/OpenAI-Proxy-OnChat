package com.hjong.OnChat.service.impl;

import com.hjong.OnChat.entity.dto.overview.ApiRequestCount;
import com.hjong.OnChat.entity.dto.overview.ModelUsageStats;
import com.hjong.OnChat.entity.dto.overview.TotalModelUsageRatio;
import com.hjong.OnChat.entity.dto.overview.UserUsageRatio;
import com.hjong.OnChat.service.OverviewService;
import jakarta.annotation.Resource;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/4/5
 **/

@Service
public class OverviewServiceImpl implements OverviewService {

    @Resource
    DatabaseClient databaseClient;

    @Override
    public Mono<ApiRequestCount> getApiRequestCount(Integer userId) {
        String sql = "SELECT DATE_FORMAT(FROM_UNIXTIME(created_at), '%m-%d') AS date, COUNT(*) AS count " +
                     "FROM logs " +
                     "WHERE created_at >= UNIX_TIMESTAMP(NOW() - INTERVAL 7 DAY) ";

        if (userId != null) {
            sql += "AND user_id = :userId ";
        }

        sql += "GROUP BY DATE(FROM_UNIXTIME(created_at))";

        DatabaseClient.GenericExecuteSpec executeSpec = databaseClient.sql(sql);

        if (userId != null) {
            executeSpec = executeSpec.bind("userId", userId);
        }

        return executeSpec.fetch()
                .all()
                .map(result -> {
                    ApiRequestCount.Item item = new ApiRequestCount.Item();
                    item.setDate((String) result.get("date"));
                    item.setCount((Long) result.get("count"));
                    return item;
                })
                .collectList()
                .map(items -> {
                    ApiRequestCount apiRequestCount = new ApiRequestCount();
                    apiRequestCount.setList(items);
                    return apiRequestCount;
                });
    }

    @Override
    public Mono<ModelUsageStats> getModelUsageStats(Integer userId) {
        StringBuilder sql = new StringBuilder("SELECT DATE_FORMAT(FROM_UNIXTIME(created_at), '%m-%d') as day, model, COUNT(*) as count " +
                "FROM logs WHERE created_at >= UNIX_TIMESTAMP(NOW() - INTERVAL 7 DAY) ");

        if (userId != null) {
            sql.append("AND user_id = :userId ");
        }

        sql.append("GROUP BY day, model");

        DatabaseClient.GenericExecuteSpec executeSpec = databaseClient.sql(sql.toString());

        if (userId != null) {
            executeSpec = executeSpec.bind("userId", userId);
        }

        return executeSpec.fetch()
                .all()
                .collectList()
                .map(resultList -> {
                    ModelUsageStats modelUsageStats = new ModelUsageStats();
                    modelUsageStats.setList(new ArrayList<>());

                    resultList.stream()
                            .collect(Collectors.groupingBy(result -> result.get("day"), LinkedHashMap::new, Collectors.toList()))
                            .forEach((day, results) -> {
                        ModelUsageStats.Usage usage = new ModelUsageStats.Usage();
                        usage.setDate((String) day);
                        usage.setUsages(results.stream().collect(Collectors.toMap(result -> (String)result.get("model"), result -> (Long) result.get("count"))));
                        modelUsageStats.getList().add(usage);
                    });

                    return modelUsageStats;
                });
    }

    @Override
    public Mono<TotalModelUsageRatio> getTotalModelUsageRatio(Integer userId ,long date) {
        StringBuilder sql = new StringBuilder("SELECT model, COUNT(*) as count " +
                "FROM logs WHERE DATE_FORMAT(FROM_UNIXTIME(created_at), '%Y-%m') = DATE_FORMAT(FROM_UNIXTIME(:date), '%Y-%m') ");

        if (userId != null) {
            sql.append("AND user_id = :userId ");
        }

        sql.append("GROUP BY model");

        DatabaseClient.GenericExecuteSpec executeSpec = databaseClient.sql(sql.toString());

        if (userId != null) {
            executeSpec = executeSpec.bind("userId", userId);
        }

        executeSpec = executeSpec.bind("date", date);

        return executeSpec.fetch()
                .all()
                .collectList()
                .map(resultList -> {
                    TotalModelUsageRatio totalModelUsageRatio = new TotalModelUsageRatio();
                    totalModelUsageRatio.setTotal(resultList.stream().mapToLong(result -> (Long) result.get("count")).sum());
                    totalModelUsageRatio.setRatio(resultList.stream().collect(Collectors.toMap(result -> (String) result.get("model"), result -> (Long) result.get("count"))));
                    totalModelUsageRatio.setDate(Instant.ofEpochSecond(date));
                    return totalModelUsageRatio;
                });
    }

    @Override
    public Mono<UserUsageRatio> getUserUsageRatio(long date) {
        String sql = "SELECT User.username, COUNT(logs.id) as log_count " +
                     "FROM User INNER JOIN logs ON User.id = logs.user_id " +
                     "WHERE DATE_FORMAT(FROM_UNIXTIME(logs.created_at), '%Y-%m') = DATE_FORMAT(FROM_UNIXTIME(:date), '%Y-%m') " +
                     "GROUP BY User.id";

        return databaseClient.sql(sql)
                .bind("date", date)
                .fetch()
                .all()
                .collectList()
                .map(resultList -> {
                    UserUsageRatio userUsageRatio = new UserUsageRatio();
                    userUsageRatio.setTotal(resultList.stream().mapToLong(result -> (Long) result.get("log_count")).sum());
                    userUsageRatio.setDate(Instant.ofEpochSecond(date));
                    userUsageRatio.setRatio(resultList.stream().collect(Collectors.toMap(result -> (String) result.get("username"), result -> (Long) result.get("log_count"))));
                    return userUsageRatio;
                });
    }

    @Override
    public Mono<Map<String, Long>> getCountOfUsersByRole() {
        String sql = "SELECT role, COUNT(*) as count " +
                     "FROM User " +
                     "GROUP BY role";

        return databaseClient.sql(sql)
                .fetch()
                .all()
                .collectMap(result -> (String) result.get("role"), result -> (Long) result.get("count"));
    }

    @Override
    public Mono<List<String>> getAllAvailableModels() {
        String sql = "SELECT jt.models as model FROM Channel CROSS JOIN JSON_TABLE( JSON_KEYS(models), '$[*]' COLUMNS (models VARCHAR(255) PATH '$')) as jt";

        return databaseClient.sql(sql)
                .fetch()
                .all()
                .map(result -> (String) result.get("model"))
                .collectList();
    }

    @Override
    public Mono<Map<String, Long>> getUsageToken(Instant start, Instant end, Integer userId) {

        String sql = "SELECT SUM(total_token) as total_tokens ," +
                     "SUM(prompt_tokens) as prompt_tokens ," +
                     "SUM(completion_tokens) as completion_tokens " +
                     "FROM logs " +
                     "WHERE created_at >= :startTimestamp AND created_at <= :endTimestamp AND user_id = :userId";
        long startTimestamp = start.toEpochMilli();
        long endTimestamp = end.toEpochMilli();

        return databaseClient.sql(sql)
                .bind("startTimestamp", startTimestamp)
                .bind("endTimestamp", endTimestamp)
                .bind("userId", userId)
                .fetch()
                .first()
                .map(result -> {
                    Map<String, Long> map = new HashMap<>();
                    map.put("total_tokens", result.get("total_tokens") == null ? 0L : ((BigDecimal) result.get("total_tokens")).longValue());
                    map.put("prompt_tokens", result.get("prompt_tokens") == null ? 0L : ((BigDecimal) result.get("prompt_tokens")).longValue());
                    map.put("completion_tokens", result.get("completion_tokens") == null ? 0L : ((BigDecimal) result.get("completion_tokens")).longValue());
                    return map;
                });
    }


}
