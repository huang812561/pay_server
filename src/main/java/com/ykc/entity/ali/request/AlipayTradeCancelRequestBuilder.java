package com.ykc.entity.ali.request;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

/**
 * @ClassName AlipayTradeCancelRequestBuilder
 * @Description 支付宝-(统一收单交易关闭接口)请求对象
 * @Author hgq
 * @Date 2020/2/28 14:00
 * @Version 1.0
 */
public class AlipayTradeCancelRequestBuilder extends RequestBuilder {

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
        if (StringUtils.isEmpty(bizContent.outTradeNo)) {
            throw new NullPointerException("out_trade_no should not be NULL!");
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayTradeCancelRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public AlipayTradeCancelRequestBuilder setAppAuthToken(String appAuthToken) {
        return (AlipayTradeCancelRequestBuilder) super.setAppAuthToken(appAuthToken);
    }

    @Override
    public AlipayTradeCancelRequestBuilder setNotifyUrl(String notifyUrl) {
        return (AlipayTradeCancelRequestBuilder) super.setNotifyUrl(notifyUrl);
    }

    public String getOutTradeNo() {
        return bizContent.outTradeNo;
    }

    /**
     * 设置取消交易的商户订单号
     * @param outTradeNo
     * @return
     */
    public AlipayTradeCancelRequestBuilder setOutTradeNo(String outTradeNo) {
        bizContent.outTradeNo = outTradeNo;
        return this;
    }

    public String getTradeNoo(){
        return bizContent.tradeNo;
    }

    /**
     * 设置取消交易的支付宝系统中的交易流水号
     * @param tradeNo
     * @return
     */
    public AlipayTradeCancelRequestBuilder setTradeNo(String tradeNo){
        bizContent.tradeNo = tradeNo;
        return this;
    }

    static class BizContent {
        /**
         * 商户订单号：订单支付时传入的商户订单号,和支付宝交易号不能同时为空。
         * trade_no,out_trade_no如果同时存在优先取trade_no
         */
        @SerializedName("out_trade_no")
        private String outTradeNo;
        /**
         * 该交易在支付宝系统中的交易流水号。最短 16 位，最长 64 位。和out_trade_no不能同时为空，
         * 如果同时传了 out_trade_no和 trade_no，则以 trade_no为准。
         */
        @SerializedName("trade_no")
        private String tradeNo;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("outTradeNo='").append(outTradeNo).append('\'');
            sb.append("tradeNo='").append(tradeNo).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
