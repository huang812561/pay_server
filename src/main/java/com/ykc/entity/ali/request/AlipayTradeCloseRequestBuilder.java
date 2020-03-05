package com.ykc.entity.ali.request;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.StringUtils;

/**
 * @ClassName AlipayTradeCloseRequestBuilder
 * @Description 统一收到交易关闭接口
 * @Author hgq
 * @Date 2020/3/1 15:59
 * @Version 1.0
 */
public class AlipayTradeCloseRequestBuilder extends RequestBuilder {

    private BizContent bizContent;

    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(bizContent.outTradeNo) && StringUtils.isEmpty(bizContent.tradeNo)) {
            throw new NullPointerException("out_trade_no and tradeNo should not be all NULL!");
        }
        return true;
    }

    @Override
    public Object getBizContent() {
        return bizContent;
    }

    @Override
    public AlipayTradeCloseRequestBuilder setAppAuthToken(String appAuthToken) {
        return (AlipayTradeCloseRequestBuilder) super.setAppAuthToken(appAuthToken);
    }

    @Override
    public AlipayTradeCloseRequestBuilder setNotifyUrl(String notifyUrl) {
        return (AlipayTradeCloseRequestBuilder) super.setNotifyUrl(notifyUrl);
    }

    public AlipayTradeCloseRequestBuilder setOutTradeNo(String outTradeNo) {
        bizContent.outTradeNo = outTradeNo;
        return this;
    }
    public String getOutTradeNo(){
        return bizContent.outTradeNo;
    }
    public AlipayTradeCloseRequestBuilder setTradeNo(String tradeNo) {
        bizContent.tradeNo = tradeNo;
        return this;
    }
    public String getTradeNo(){
        return bizContent.tradeNo;
    }

    static class BizContent{

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
