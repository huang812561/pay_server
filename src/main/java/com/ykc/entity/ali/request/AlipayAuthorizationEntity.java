package com.ykc.entity.ali.request;

import lombok.Data;

/**
 * @ClassName AlipayAuthorizationEntity
 * @Description 支付宝-用户授权
 * @Author hgq
 * @Date 2020/2/28 14:00
 * @Version 1.0
 */
@Data
public class AlipayAuthorizationEntity {

    /**
     * 调用支付宝的接口：my.getAuthCode获取authCode
     */
    private String authCode;
}
