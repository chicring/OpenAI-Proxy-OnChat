package com.hjong.OnChat.service.impl;


import com.hjong.OnChat.entity.dto.ChannelPermission;
import com.hjong.OnChat.entity.vo.req.ChannelPermissionQueryVO;
import com.hjong.OnChat.entity.vo.resp.ChannelPermissionVO;
import com.hjong.OnChat.entity.vo.resp.PageVO;
import com.hjong.OnChat.repositories.ChannelPermissionRepositories;
import com.hjong.OnChat.service.ChannelPermissionService;
import jakarta.annotation.Resource;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ChannelPermissionServiceImpl implements ChannelPermissionService {

    @Resource
    ChannelPermissionRepositories channelPermissionRepositories;

    @Resource
    DatabaseClient databaseClient;

    @Override
    public Mono<ChannelPermission> save(Integer userId, Integer channelId) {
        ChannelPermission channelPermission = new ChannelPermission();
        channelPermission.setUserId(userId);
        channelPermission.setChannelId(channelId);
        return channelPermissionRepositories.save(channelPermission);
    }

    @Override
    public Mono<Void> delete(Integer id) {
        return channelPermissionRepositories.deleteById(id);
    }

    @Override
    public Mono<PageVO<ChannelPermissionVO>> find(ChannelPermissionQueryVO vo) {
        StringBuilder sql = new StringBuilder("SELECT *, user.username, channel.name, channel.type FROM channel_permission ")
                .append("JOIN user ON channel_permission.user_id = user.id ")
                .append("JOIN channel ON channel_permission.channel_id = channel.id WHERE 1=1 ");

        if (vo.getUsername() != null) {
            sql.append("AND user.username = :username ");
        }

        if (vo.getChannelName() != null) {
            sql.append("AND channel.name = :channelName ");
        }

        sql.append("ORDER BY channel_permission.id DESC LIMIT :limit OFFSET :offset");

        String countSql = sql.toString().replace("SELECT *, user.username, channel.name, channel.type", "SELECT COUNT(*)");
        Mono<Long> count = databaseClient.sql(countSql)
                .bind("username", vo.getUsername())
                .bind("channelName", vo.getChannelName())
                .fetch()
                .one()
                .map(result -> (Long) result.get("count"));

        return count.flatMap(total -> {
            int totalPages = (int) Math.ceil((double) total / vo.getSize());
            return databaseClient.sql(sql.toString())
                    .bind("username", vo.getUsername())
                    .bind("channelName", vo.getChannelName())
                    .bind("limit", vo.getSize())
                    .bind("offset", vo.getSize() * (vo.getPage() - 1))
                    .fetch()
                    .all()
                    .map(result -> {
                        ChannelPermissionVO channelPermissionVO = new ChannelPermissionVO();
                        channelPermissionVO.setId((Integer) result.get("id"));
                        channelPermissionVO.setUser_id((Integer) result.get("user_id"));
                        channelPermissionVO.setChannel_id((Integer) result.get("channel_id"));
                        channelPermissionVO.setUsername((String) result.get("username"));
                        channelPermissionVO.setChannelName((String) result.get("name"));
                        channelPermissionVO.setChannelType((String) result.get("type"));
                        return channelPermissionVO;
                    }).collectList()
                    .map(list -> {
                        PageVO<ChannelPermissionVO> pageVO = new PageVO<>();
                        pageVO.setList(list);
                        pageVO.setPageSize(vo.getSize());
                        pageVO.setCurrentPage(vo.getPage());
                        pageVO.setTotal(total);
                        pageVO.setTotalPages(totalPages);
                        return pageVO;
                    });
        });
    }


}
