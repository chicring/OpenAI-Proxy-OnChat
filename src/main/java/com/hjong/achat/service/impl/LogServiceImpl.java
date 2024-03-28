package com.hjong.achat.service.impl;

import com.hjong.achat.entity.DTO.Logs;
import com.hjong.achat.entity.VO.req.FindLogVO;
import com.hjong.achat.entity.VO.resp.OverviewVO;
import com.hjong.achat.entity.VO.resp.RequestAmountVO;
import com.hjong.achat.entity.VO.resp.StatusInfoVO;
import com.hjong.achat.repositories.ChannelRepositories;
import com.hjong.achat.repositories.LogsRepositories;
import com.hjong.achat.repositories.UserRepositories;
import com.hjong.achat.service.LogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/19
 **/
@Slf4j
@Service
public class LogServiceImpl implements LogService {
    @Resource
    LogsRepositories logsRepositories;
    @Resource
    ChannelRepositories channelRepositories;
    @Resource
    UserRepositories   userRepositories;
    @Resource
    DatabaseClient databaseClient;

    @Override
    public Mono<Logs> saveLog(Logs savelog) {

        return logsRepositories.save(savelog).onErrorResume(e -> {
            log.error("保存日志失败", e);
            return Mono.empty();
        });
    }

    @Override
    public Flux<Logs> findAll(FindLogVO vo) {

        String sql = builderSQL(vo);
        return databaseClient.sql(sql)
                .fetch()
                .all()
                .map(result -> {
                    Logs logs = new Logs();
                    logs.setId((Long)result.get("id"));
                    logs.setChannelId((Integer)result.get("channel_id"));
                    logs.setChannelType((String)result.get("channel_type"));
                    logs.setChannelName((String)result.get("channel_name"));
                    logs.setUserId((Integer)result.get("user_id"));
                    logs.setUsername((String)result.get("username"));
                    logs.setModel((String)result.get("model"));
                    logs.setTotalToken((Long)result.get("total_token"));
                    logs.setPromptTokens((Long)result.get("prompt_tokens"));
                    logs.setCompletionTokens((Long)result.get("completion_tokens"));
                    logs.setInputText((String)result.get("input_text"));
                    logs.setOutputText((String)result.get("output_text"));
                    logs.setIp((String)result.get("ip"));
                    logs.setConsumeTime((Double) result.get("consume_time"));
                    logs.setCreatedAt((Long)result.get("created_at"));
                    return logs;
                });
    }

    @Override
    public Mono<RequestAmountVO> logAmount(){

        String sql = "SELECT DATE_FORMAT(FROM_UNIXTIME(created_at), '%m-%d') AS date, COUNT(*) AS count " +
                     "FROM logs " +
                     "WHERE created_at >= UNIX_TIMESTAMP(NOW() - INTERVAL 7 DAY) " +
                     "GROUP BY DATE(FROM_UNIXTIME(created_at))";

        return databaseClient.sql(sql)
                .fetch()
                .all()
                .map(result -> {
                    RequestAmountVO.item item = new RequestAmountVO.item();
                    item.setDate((String) result.get("date"));
                    item.setCount((Long) result.get("count"));
                    return item;
                })
                .collectList()
                .map(items -> {
                    RequestAmountVO requestAmountVO = new RequestAmountVO();
                    requestAmountVO.setList(items);
                    return requestAmountVO;
                });
    }

    @Override
    public Mono<OverviewVO> overview() {

        String sql = "SELECT DATE_FORMAT(FROM_UNIXTIME(created_at), '%m-%d') as day, model, COUNT(*) as count FROM logs GROUP BY day, model";

        return databaseClient.sql(sql)
                .map((row, meta) -> {
                    OverviewVO.Day day = new OverviewVO.Day();
                    day.setDate(row.get("day", String.class));
                    OverviewVO.Usage usage = new OverviewVO.Usage();
                    usage.setModel(row.get("model", String.class));
                    usage.setCount(row.get("count", Long.class));
                    day.setUsages(List.of(usage));
                    return day;
                })
                .all()
                .collectList()
                .map(this::toOverview);

    }




    private OverviewVO toOverview(List<OverviewVO.Day> days) {
        Map<String, List<OverviewVO.Usage>> groupedUsages = days.stream()
                .collect(Collectors.groupingBy(
                        OverviewVO.Day::getDate,
                        Collectors.flatMapping(
                                day -> day.getUsages().stream(),
                                Collectors.toList()
                        )
                ));

        OverviewVO overviewVO = new OverviewVO();
        overviewVO.setOverview(groupedUsages.entrySet().stream()
                .map(entry -> {
                    OverviewVO.Day mergedDay = new OverviewVO.Day();
                    mergedDay.setDate(entry.getKey());
                    mergedDay.setUsages(entry.getValue());
                    return mergedDay;
                })
                .collect(Collectors.toList())
        );

        return overviewVO;
    }

    private String builderSQL(FindLogVO vo) {
        StringBuilder sql = new StringBuilder("SELECT * FROM logs WHERE 1=1 ");
        if (vo.getChannelId() != null) {
            sql.append("AND channel_id = ").append(vo.getChannelId()).append(" ");
        }
        if (vo.getUserId() != null) {
            sql.append("AND user_id = ").append(vo.getUserId()).append(" ");
        }
        if (vo.getChannelName() != null) {
            sql.append("AND channel_name = '").append(vo.getChannelName()).append("' ");
        }
        if (vo.getIp() != null) {
            sql.append("AND ip = '").append(vo.getIp()).append("' ");
        }
        if (vo.getModel() != null) {
            sql.append("AND model = '").append(vo.getModel()).append("' ");
        }
        if (vo.getChannelType() != null) {
            sql.append("AND channel_type = '").append(vo.getChannelType()).append("' ");
        }
        if (vo.getStartTime() != null) {
            sql.append("AND create_at >= '").append(vo.getStartTime()).append("' ");
        }
        if (vo.getEndTime() != null) {
            sql.append("AND create_at <= '").append(vo.getEndTime()).append("' ");
        }

        sql.append("ORDER BY created_at DESC ");

        if(vo.getPage() != null && vo.getSize() != null) {
            long offset = (vo.getPage() - 1) * vo.getSize();
            sql.append("LIMIT ").append(vo.getSize()).append(" OFFSET ").append(offset).append(" ");
        }
        return sql.toString();
    }
}
