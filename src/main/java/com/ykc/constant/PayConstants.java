package com.ykc.constant;

/**
 * @ClassName Constants
 * @Description 自定义常量类
 * @Author hgq
 * @Date 2020/2/28 15:23
 * @Version 1.0
 */
public class PayConstants {

    public static final String GRANT_TYPE = "authorization_code";

    /**
     * 支付宝支付交易状态
     */
    public interface ALI_TRADE_STATUS {
        public static final String SUCCESS = "10000"; // 成功
        public static final String PAYING = "10003";  // 用户支付中
        public static final String ERROR = "20000"; // 系统异常
        public static final String PARAM_MISS = "40001";  // 缺少必选参数
        public static final String PARAM_ILLEGAL = "40002";  // 非法的参数
        public static final String FAILED = "40004";  // 失败
    }

    //交易保障线程第一次调度延迟和调度间隔（秒）
    public static final int HEARTBEAT_DELAY = 5;
    public static final int HEARTBEAT_DURATION = 900;

    //当面付最大撤销次数和撤销间隔（毫秒）
    public static final int CANCEL_DURATION = 2000;
    public static final int MAX_CANCEL_RETRY = 3;

    //当面付最大查询次数和查询间隔（毫秒）
    public static final int MAX_QUERY_RETRY = 5;
    public static final int QUERY_DURATION = 5000;

}
