package net.jiaobaowang.visitor.common;

/**
 * Created by ShangLinMo on 2018/1/5.
 */

public class VisitorConfig {
    /**
     * 获取token
     */
    public static final String VISITOR_GET_TOKEN = "http://139.129.252.49:8080/visitor/api/getToken";
    /**
     * 主URL
     */
    public static final String VISITOR_MAIN_URL = "http://139.129.252.49:8080/visitor";
    /**
     * 访客添加
     */
    public static final String VISITOR_API_ADD = VISITOR_MAIN_URL + "/api/add";
    /**
     * 访客签离
     */
    public static final String VISITOR_API_LEAVE = VISITOR_MAIN_URL + "/api/leave";
    /**
     * 获取访客列表
     */
    public static final String VISITOR_API_LIST = VISITOR_MAIN_URL + "/api/list";
}