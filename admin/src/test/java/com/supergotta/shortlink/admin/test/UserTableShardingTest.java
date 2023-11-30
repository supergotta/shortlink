package com.supergotta.shortlink.admin.test;

public class UserTableShardingTest {

    public static final String SQL = "create table t_link_goto_%d\n" +
            "(\n" +
            "    id              bigint       not null\n" +
            "        primary key,\n" +
            "    gid             varchar(32)  null comment '分组标识',\n" +
            "    full_short_link varchar(128) null comment '完整短链接'\n" +
            ")\n" +
            "    comment '路由表';";

    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i);
        }
    }
}
