create table cars
(
    id          int auto_increment comment '主键ID'
        primary key,
    car_name    varchar(50)                                     null comment '车辆编号（业务唯一标识，如车牌号）',
    image_url   varchar(500)                                    null comment '车辆图片存储路径/URL',
    port_id     int                                             not null comment '所在港口ID（外键关联port表）',
    status      enum ('闲置', '在用') default '闲置'            not null comment '车辆状态',
    update_time datetime              default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_time datetime              default CURRENT_TIMESTAMP null comment '创建时间',
    constraint trailer_number
        unique (car_name)
)
    comment '车辆信息表';

create index idx_port
    on cars (port_id)
    comment '港口查询索引';

create index idx_status
    on cars (status)
    comment '状态查询索引';

create table companies
(
    id           int auto_increment comment '主键ID'
        primary key,
    company_name varchar(100)                       not null comment '公司名称',
    country      varchar(50)                        not null comment '所在国家',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uk_company_name
        unique (company_name) comment '公司名称唯一索引'
)
    comment '公司信息表';

create index idx_country
    on companies (country)
    comment '国家查询索引';

create table containers
(
    id          int auto_increment comment '主键ID'
        primary key,
    content     varchar(500)                       null comment '集装箱内容/货物描述',
    size        varchar(20)                        not null comment '尺寸大小（如：20英尺, 40英尺）',
    company_id  int                                not null comment '所属公司',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    status      tinyint                            null,
    capacity    int                                null
)
    comment '集装箱信息表';

create index idx_company
    on containers (company_id)
    comment '公司查询索引';

create table logistics
(
    id              int auto_increment comment '主键ID'
        primary key,
    order_id        int                                not null comment '订单ID',
    start_port_id   int                                not null comment '起始港口ID',
    end_port_id     int                                not null comment '终点港口ID',
    current_port_id int                                null comment '当前港口ID',
    ship_id         int                                null comment '船舶ID',
    car_id          int                                null comment '车辆ID',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间'
)
    comment '物流跟踪表';

create index idx_create_time
    on logistics (create_time)
    comment '创建时间索引';

create index idx_current_port
    on logistics (current_port_id)
    comment '当前港口索引';

create index idx_end_port
    on logistics (end_port_id)
    comment '终点港口索引';

create index idx_order
    on logistics (order_id)
    comment '订单查询索引';

create index idx_ship
    on logistics (ship_id)
    comment '船舶索引';

create index idx_start_port
    on logistics (start_port_id)
    comment '起始港口索引';

create index idx_vehicle
    on logistics (car_id)
    comment '车辆索引';

create table orders
(
    id            int auto_increment comment '主键ID'
        primary key,
    order_number  varchar(50)                                                   not null comment '订单号（业务唯一标识）',
    user_id       int                                                           not null comment '用户ID（商户，外键关联用户表）',
    status        enum ('待处理', '进行中', '已完成') default '待处理'          not null comment '订单状态',
    update_time   datetime                            default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_time   datetime                            default CURRENT_TIMESTAMP null comment '创建时间',
    container_ids varchar(100)                                                  null comment '涉及集装箱id',
    constraint order_number
        unique (order_number)
)
    comment '订单信息表';

create index idx_create_time
    on orders (create_time)
    comment '创建时间索引';

create index idx_order_number
    on orders (order_number)
    comment '订单号索引';

create index idx_status
    on orders (status)
    comment '状态索引';

create index idx_user_id
    on orders (user_id)
    comment '用户ID索引';

create table ports
(
    id          int auto_increment comment '港口主键'
        primary key,
    port_name   varchar(100)                       not null comment '港口名称',
    longitude   decimal(9, 6)                      not null comment '经度',
    latitude    decimal(8, 6)                      not null comment '纬度',
    country     varchar(50)                        not null comment '所在国家',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uk_port_name
        unique (port_name) comment '港口名称唯一索引'
)
    comment '港口信息表';

create table ships
(
    id              int auto_increment comment '主键ID'
        primary key,
    ship_name       varchar(100)                       not null comment '船舶名字',
    company_id      varchar(100)                       not null comment '所属公司id',
    update_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_time     datetime default CURRENT_TIMESTAMP null comment '创建时间',
    status          tinyint                            null,
    capacity        double   default 0                 null,
    current_port_id int                                null
)
    comment '船舶信息表';

create index idx_company
    on ships (company_id)
    comment '所属公司id索引';

create index idx_ship_name
    on ships (ship_name)
    comment '船舶名字索引';

create table users
(
    id          int auto_increment comment '主键ID'
        primary key,
    username    varchar(50)                        not null comment '用户名称（唯一标识）',
    phone       varchar(100)                       not null comment '联系方式（电话）',
    password    varchar(100)                       not null comment '密码',
    state       int                                not null comment '用户身份',
    country     varchar(50)                        not null comment '所在国家',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint username
        unique (username)
)
    comment '用户信息表';

create index idx_create_time
    on users (create_time)
    comment '创建时间索引';

create index idx_username
    on users (username)
    comment '用户名称索引';


