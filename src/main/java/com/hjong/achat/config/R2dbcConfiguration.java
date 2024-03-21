package com.hjong.achat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/16
 **/
@EnableR2dbcRepositories
@Configuration
public class R2dbcConfiguration {

//    @Bean //替换容器中原来的
//    @ConditionalOnMissingBean
//    public R2dbcCustomConversions conversions(){
//
//        //把我们的转换器加入进去； 效果新增了我们的 Converter
//        return R2dbcCustomConversions.of(MySqlDialect.INSTANCE,new BookConverter());
//    }
}
