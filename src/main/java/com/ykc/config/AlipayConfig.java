package com.ykc.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 支付宝配置类
 * @Author: hgq
 * @Date: 2019/3/27
 */
@Data
@Configuration
public class AlipayConfig {
    /**
     * 商户appId
     */
    @Value("${alipay.app_id}")
    private String appId;

    /**
     * 应用私钥
     */
    @Value("${alipay.private_key}")
    private String privateKey;

    /**
     * 支付宝公钥
     */
    @Value("${alipay.alipay_public_key}")
    private String publicKey;

    /**
     * 支付宝小程序-appId
     */
    @Value("${alipay.applet_app_id}")
    private String appletAppId;

    /**
     * 服务器异步通知路径
     */
    @Value("${alipay.notify_url}")
    private String notifyUrl;

    /**
     * 请求网关地址
     */
    @Value("${alipay.server_url}")
    private String serverUrl;

    /**
     * 编码
     */
    @Value("${alipay.charset}")
    private String charset;

    /**
     * 返回格式
     */
    @Value("${alipay.format}")
    private String format;

    /**
     * 加密类型
     */
    @Value("${alipay.sign_type}")
    private String signType;

    /**
     * 服务器异步通知路径
     */
    @Value("${alipay.grant_type}")
    private String grantType;
}
