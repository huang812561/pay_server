package com.ykc.entity.ali.request;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

/**
 * @ClassName AlipayTradeQueryRequestBuilder
 * @Description 支付宝-（统一收单线下交易查询）请求对象
 * @Author hgq
 * @Date 2020/2/28 14:00
 * @Version 1.0
 */
public class AlipayTradeQueryRequestBuilder extends RequestBuilder {

    /**
     * bizContent：请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递，具体参照各产品快速接入文档
     */
    private BizContent bizContent = new BizContent();

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(bizContent.tradeNo) &&
                StringUtils.isEmpty(bizContent.outTradeNo)) {
            throw new IllegalStateException("tradeNo and outTradeNo can not both be NULL!");
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayTradeQueryRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public AlipayTradeQueryRequestBuilder setAppAuthToken(String appAuthToken) {
        return (AlipayTradeQueryRequestBuilder) super.setAppAuthToken(appAuthToken);
    }

    @Override
    public AlipayTradeQueryRequestBuilder setNotifyUrl(String notifyUrl) {
        return (AlipayTradeQueryRequestBuilder) super.setNotifyUrl(notifyUrl);
    }

    public String getTradeNo() {
        return bizContent.tradeNo;
    }

    public AlipayTradeQueryRequestBuilder setTradeNo(String tradeNo) {
        bizContent.tradeNo = tradeNo;
        return this;
    }

    public String getOutTradeNo() {
        return bizContent.outTradeNo;
    }

    public AlipayTradeQueryRequestBuilder setOutTradeNo(String outTradeNo) {
        bizContent.outTradeNo = outTradeNo;
        return this;
    }

    static class BizContent {
        // 支付宝交易号,和商户订单号不能同时为空, 如果同时存在则通过tradeNo查询支付宝交易
        @SerializedName("trade_no")
        private String tradeNo;

        // 商户订单号，通过此商户订单号查询当面付的交易状态
        @SerializedName("out_trade_no")
        private String outTradeNo;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("tradeNo='").append(tradeNo).append('\'');
            sb.append(", outTradeNo='").append(outTradeNo).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
