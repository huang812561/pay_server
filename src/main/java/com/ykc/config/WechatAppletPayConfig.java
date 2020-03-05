package com.ykc.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName WechatAppletPayConfig
 * @Description 微信小程序支付配置
 * @Author hgq
 * @Date 2020/2/28 11:12
 * @Version 1.0
 */
@Data
@Configuration
public class WechatAppletPayConfig {

    /**
     * APP appId
     */
    @Value("${ykc.wechat.appId}")
    private String appId;

    /**
     * 小程序 appId
     */
    @Value("${ykc.wechat.applet.app.appid}")
    private String appletAppId;

    /**
     * 公众号 appId
     */
    @Value("${ykc.wechat.subions.appId}")
    private String subionsAppId;

    /**
     * 商户号
     */
    @Value("${ykc.wechat.mchId}")
    private String mchId;

    /**
     * 商户秘钥
     */
    @Value("${ykc.wechat.mch.key}")
    private String mchKey;

    /**
     * 小程序秘钥
     */
    @Value("${ykc.wechat.applet.app.secret}")
    private String appletAppSecret;

    /**
     * 公众号秘钥
     */
    @Value("${ykc.wechat.subions.app.secret}")
    private String subionsAppSecret;

    /**
     * 回调地址
     */
    @Value("${ykc.wechat.notify.url}")
    private String notifyUrl;

    /**
     * 授权类型
     */
    @Value("${ykc.wechat.grant.type}")
    private String grantType;

    /**
     * 认证地址
     */
    @Value("${ykc.wechat.access.token.url}")
    private String accessTokenUrl;

    /**
     * 证书
     */
    @Value("${ykc.wechat.ssl.certificate.path}")
    private String sslCertificatePath;

    /**
     * 授权域名
     */
    @Value("${ykc.wechat.h5.wap.url}")
    private String h5WapUrl;

    /**
     * 网站名称
     */
    @Value("${ykc.wechat.h5.wap.name}")
    private String h5WapName;

}
