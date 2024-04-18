create table IF NOT EXISTS Channel
(
    id           int auto_increment
        primary key,
    name         varchar(255)                 not null,
    enabled      tinyint(1) default 1         not null,
    type         varchar(255)                 not null,
    api_key      varchar(255)                 not null,
    base_url     varchar(255)                 not null,
    models       longtext collate utf8mb4_bin not null
        check (json_valid(`models`)),
    priority     int        default 0         not null,
    `usage`      int        default 0         null,
    created_at   int                          not null,
    enable_proxy tinyint(1) default 0         not null
);

create table IF NOT EXISTS User
(
    id            int auto_increment
        primary key,
    username      varchar(50)                                                 not null,
    password      varchar(50)                                                 not null,
    email         varchar(50)                                                 null,
    role          enum ('ADMIN', 'USER', 'GUEST') default 'USER'              not null,
    current_usage int                             default 0                   null,
    total_usage   int                             default 50000               null,
    is_active     tinyint(1)                      default 1                   not null,
    register_date timestamp                       default current_timestamp() not null
);

create table IF NOT EXISTS ApiKey
(
    id         int auto_increment
        primary key,
    name       varchar(255)         not null,
    api_key    varchar(255)         not null,
    enabled    tinyint(1) default 1 not null,
    created_at mediumtext           not null,
    expires_at mediumtext           not null,
    user_id    int                  null,
    constraint `key`
        unique (api_key),
    constraint fk_apikey_user
        foreign key (user_id) references User (id)
);

create index IF NOT EXISTS idx_apikey_api_key on ApiKey (api_key);

create table IF NOT EXISTS logs
(
    id                bigint auto_increment
        primary key,
    channel_id        int                     null,
    channel_type      varchar(100) default '' null,
    user_id           int                     null,
    username          varchar(100) default '' null,
    model             varchar(100) default '' null,
    total_token       bigint       default 0  null,
    prompt_tokens     bigint       default 0  null,
    completion_tokens bigint       default 0  null,
    input_text        text         default '' null,
    output_text       text         default '' null,
    ip                varchar(50)             null,
    created_at        bigint                  null,
    consume_time      double       default 0  not null,
    channel_name      varchar(30)  default '' null
);

create index IF NOT EXISTS idx_logs_created_at on logs (created_at);

CREATE TABLE IF NOT EXISTS Model
(
    id     INT AUTO_INCREMENT PRIMARY KEY,
    request_model VARCHAR(255) NOT NULL,
    real_model VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    channel_id INT NOT NULL
);

CREATE TABLE IF NOT EXISTS ChannelPermission
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    channel_id INT NOT NULL
);

create TABLE IF NOT EXISTS knowledgeBase
(
    id                     int auto_increment comment '知识库id'
        primary key,
    user_id                int                                     null comment '创建者id',
    create_time            datetime    default current_timestamp() not null comment '创建时间',
    update_time            datetime    default current_timestamp() not null on update current_timestamp() comment '修改时间',
    name                   varchar(50)                             not null comment '知识库名称',
    description            varchar(255)                            null comment '知识库描述',
    collection_name varchar(50)                             null comment '向量数据库的表名',
    status                 int         default 0                   not null comment '0向量处理中，1已完成，2失败',
    file_name              varchar(50) default ''                  not null
)
    comment '知识库总表';

create TABLE IF NOT EXISTS Files
(
    id                     int auto_increment comment '文件id' primary key,
    create_time            datetime    default current_timestamp() not null comment '创建时间',
    update_time            datetime    default current_timestamp() not null on update current_timestamp() comment '修改时间',
    name                   varchar(50)                             not null comment '文件名',
    status                 int         default 0                   not null comment '0向量处理中，1已完成，2失败',
    knowledgeBase_id       int                                     not null comment '知识库id'
)
    comment '知识库文件表';