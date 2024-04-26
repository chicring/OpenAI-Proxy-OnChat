package com.hjong.OnChat.repositories;

import com.hjong.OnChat.entity.dto.ChannelPermission;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelPermissionRepositories extends R2dbcRepository<ChannelPermission,Integer>{

}
