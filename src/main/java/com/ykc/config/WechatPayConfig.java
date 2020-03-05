package com.ykc.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName WechatPayConfig
 * @Description 微信支付配置
 * @Author hgq
 * @Date 2020/2/28 11:12
 * @Version 1.0
 */
@Data
@Configuration
public class WechatPayConfig {
    /**
     * 小程序appId
     */
    @Value("${wx.appId}")
    private String appId;

    /**
     * 微信支付的商户id
     */
    @Value("${wx.mch_id}")
    private String mchId;

    /**
     * 微信支付的商户密钥
     */
    @Value("${wx.key}")
    private String key;

    /**
     * 支付成功后的服务器回调url
     */
    @Value("${wx.notify_url}")
    private String notifyUrl;

    /**
     * 签名方式
     */
    @Value("${wx.sing_type}")
    private String singType;

    /**
     * 交易类型
     */
    @Value("${wx.trade_type}")
    private String tradeType;

    /**
     * 微信统一下单接口地址
     */
    @Value("${wx.pay_url}")
    private String payUrl;

    /**
     * 微信统一下单接口地址
     */
    @Value("${wx.refund_url}")
    private String refundUrl;

    /**
     * app密钥
     */
    @Value("${wx.app_secret}")
    private String appSecret;

    /**
     * 证书地址
     */
    @Value("${wx.ssl_certificate_path}")
    private String sslCertPath;
    /**
     * 退款查询
     */
    @Value("${wx.refund_query_url}")
    private String refundQueryUrl;

    /**
     * 退款模板
     */
    @Value("${wx.refund_template_id}")
    private String refundTemplateId;

    /**
     * 授权类型
     */
    @Value("${wx.grant_type}")
    private String grantType;

    /**
     * 调用获取access_token接口
     */
    @Value("${wx.get_access_token_url}")
    private String accessTokenUrl;

    /**
     * 支付成功通知模板Id
     */
    @Value("${wx.pay_template_id}")
    private String payTemplateId;

    /**
     * 消息通知url
     */
    @Value("${wx.send_template_message_url}")
    private String sendTemplateMessageUrl;

    /**
     * 支付模板消息参数规则
     */
    @Value("${wx.pay_emphasis_keyword}")
    private String payEmphasisKeyword;

    /**
     * 退款模板消息参数规则
     */
    @Value("${wx.refund_emphasis_keyword}")
    private String refundEmphasisKeyword;

    /**
     * 获取token的url
     */
    @Value("${wx.get_token_url}")
    private String getTokenUrl;
}
