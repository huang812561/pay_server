package com.ykc.entity;

import lombok.Data;

/**
 * @ClassName PayOrderEntity
 * @Description 支付订单公共实体
 * @Author hgq
 * @Date 2020/2/29 21:31
 * @Version 1.0
 */
@Data
public class PayOrderEntity {

    /**
     * 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
     * （例如：20150320010101001）
     */
    private String outTradeNo;
    /**
     * 支付交易单号（和商户单号不嫩同时为空）
     */
    private String tradeNo;
    /**
     * 商品名称
     */
    private String orderName;
    /**
     * 商品总金额/订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
     * 如果同时传入了【打折金额】，【不可打折金额】，【订单总金额】三者，则必须满足如下条件：
     * 【订单总金额】=【打折金额】+【不可打折金额
     */
    private String totalAmount;

}
