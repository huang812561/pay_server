package com.ykc.constant;

import lombok.Getter;

/**
 * @ClassName BusinessCodeEnum
 * @Description 通用响应码定义
 * @Author hgq
 * @Date 2020/2/28 12:49
 * @Version 1.0
 */
@Getter
public enum BusinessCodeEnum {
    /**
     * 响应成功
     */
    SUCCESS("0", "SUCCESS"),
    /**
     * 响应失败
     */
    FAILURE("-1", "网络异常,请稍后重试！"),
    /**
     * 响应失败
     */
    REQUEST_FAILURE("-1", "请求失败,请稍后重试！"),
    /**
     * 请求参数错误
     */
    PARAMS_ERROR("-1", "参数错误"),
    /**
     * 授权失败
     */
    AUTH_FAILURE("-1","授权失败"),
    /**
     * 认证失败
     */
    GRANT_FAILURE("-1","认证失败"),
    ;


    /**
     * 业务返回码
     */
    private String code;
    /**
     * 业务返回描述
     */
    private String message;

    /**
     * 响应信息
     * @param code      业务返回码
     * @param message   业务返回描述
     */
    BusinessCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
