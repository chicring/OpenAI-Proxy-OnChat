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
        StringBuilder sql = new StringBuilder("SELECT ChannelPermission.*, User.username, Channel.name, Channel.type FROM ChannelPermission ")
                .append("JOIN User ON ChannelPermission.user_id = User.id ")
                .append("JOIN Channel ON ChannelPermission.channel_id = Channel.id WHERE 1=1 ");

        if (vo.getSearch() != null && !vo.getSearch().isEmpty()) {
            sql.append("AND (User.username LIKE :search OR Channel.name LIKE :search OR Channel.type LIKE :search) ");
        }

        String countSql = sql.toString().replace("SELECT ChannelPermission.*, User.username, Channel.name, Channel.type", "SELECT COUNT(*) as count");

        sql.append("ORDER BY ChannelPermission.id DESC LIMIT :limit OFFSET :offset");

        DatabaseClient.GenericExecuteSpec executeSpec = databaseClient.sql(countSql);

        if (vo.getSearch() != null && !vo.getSearch().isEmpty()){
            executeSpec = executeSpec.bind("search", vo.getSearch());
        }


        return executeSpec.fetch()
                .one()
                .flatMap(total -> {
                    DatabaseClient.GenericExecuteSpec executeSpecData = databaseClient.sql(sql.toString());

                    if (vo.getSearch() != null && !vo.getSearch().isEmpty()) {
                        executeSpecData = executeSpecData.bind("search", vo.getSearch());
                    }

                    executeSpecData = executeSpecData.bind("limit", vo.getSize())
                            .bind("offset", vo.getSize() * (vo.getPage() - 1));

                    return executeSpecData.fetch()
                            .all()
                            .map(result -> {
                                ChannelPermissionVO channelPermissionVO = new ChannelPermissionVO();
                                channelPermissionVO.setId((Integer) result.get("id"));
                                channelPermissionVO.setUserId((Integer) result.get("user_id"));
                                channelPermissionVO.setChannelId((Integer) result.get("channel_id"));
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
                                pageVO.setTotal((Long) total.get("count"));
                                return pageVO;
                            });
                });
    }

}
