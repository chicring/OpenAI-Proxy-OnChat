package com.hjong.OnChat.entity;

/**
 * @author HJong
 * @version 1.0
 * @date 2024/3/29
 **/
public class Consts {


    /**
     * 流式对话完成标志
     */
    public static final String DONE = "[DONE]";

    //用户角色
    public static final String ROLE = "ROLE";
    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";

    public static final String USER_ID = "USER_ID";

    //请求频率限制
    public final static String FLOW_LIMIT_IP = "flow:ip:";
    public final static String FLOW_LIMIT_BLOCK = "flow:block:";
}
