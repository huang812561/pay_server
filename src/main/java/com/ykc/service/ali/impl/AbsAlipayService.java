package com.ykc.service.ali.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.ykc.entity.TradeStatus;
import com.ykc.entity.ali.request.RequestBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName AbsAlipayService
 * @Description 支付宝支付抽象类
 * 调用支付宝远程接口 和 验证 bizContent 对象
 * @Author hgq
 * @Date 2020/3/1 12:16
 * @Version 1.0
 */
@Slf4j
abstract class AbsAlipayService {

    /**
     * 验证BizContent对象
     *
     * @param requestBuilder
     */
    public void validateBuilder(RequestBuilder requestBuilder) {

        if (null == requestBuilder) {
            throw new NullPointerException("requestBuilder should not be NULL!");
        }

        if (!requestBuilder.validate()) {
            throw new IllegalStateException("requestBuilder validate failed : " + requestBuilder.toString());
        }
    }

    /**
     * 请求AlipayClient的execute方法，进行远程调用
     *
     * @param client
     * @param request
     * @return
     */
    public AlipayResponse alipayExecute(AlipayClient client, AlipayRequest request) {
        try {
            AlipayResponse response = client.execute(request);
            if (null != response) log.info("请求支付宝远程接口返回对象", response.getBody());
            return response;
        } catch (AlipayApiException e) {
            log.error("请求支付宝远程接口失败", e);
            return null;
        }
    }
}
