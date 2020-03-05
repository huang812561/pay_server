package com.ykc.entity.ali.request;

import com.google.gson.Gson;
import lombok.Data;

/**
 * @ClassName RequestBuilder
 * @Description 支付宝支付请求对象
 * @Author hgq
 * @Date 2020/3/1 12:16
 * @Version 1.0
 */
@Data
public abstract class RequestBuilder {
    /**
     * 选填：
     */
    private String appAuthToken;
    /**
     * 选填：支付宝服务器主动通知商户服务器里指定的页面http/https路径。
     */
    private String notifyUrl;

    // 验证请求对象
    public abstract boolean validate();

    // 获取bizContent对象，用于下一步转换为json字符串
    public abstract Object getBizContent();

    // 将bizContent对象转换为json字符串
    public String toJsonString() {
        // 使用gson将对象转换为json字符串
        return new Gson().toJson(this.getBizContent());
    }

    public RequestBuilder setAppAuthToken(String appAuthToken) {
        this.appAuthToken = appAuthToken;
        return this;
    }

    public RequestBuilder setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
        return this;
    }
}
