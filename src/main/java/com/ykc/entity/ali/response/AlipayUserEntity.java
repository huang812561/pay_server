package com.ykc.entity.ali.response;

import lombok.Data;

/**
 * @ClassName AlipayUserEntity
 * @Description 支付宝获取用户会员信息
 * @Author hgq
 * @Date 2020/2/28 16:38
 * @Version 1.0
 */
@Data
public class AlipayUserEntity {
    /**
     * 支付宝-会员id
     */
    private String alipayId;
    /**
     * 用户手机号
     */
    private String phone;
    /**
     * 用户昵称
     */
    private String nickName;
}