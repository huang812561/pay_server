package com.ykc.service.ali.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.ykc.constant.PayConstants;
import com.ykc.entity.TradeStatus;
import com.ykc.entity.ali.request.*;
import com.ykc.entity.ali.result.*;
import com.ykc.service.ali.IAlipayTradeService;
import com.ykc.util.JsonUtil;
import com.ykc.util.ThreadPoolUtil;
import com.ykc.util.Utils;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName AbsAlipayTradeService
 * @Description 支付宝支付请求远程交易接口
 * @Author hgq
 * @Date 2020/3/1 12:21
 * @Version 1.0
 */
@Slf4j
abstract class AbsAlipayTradeService extends AbsAlipayService implements IAlipayTradeService {

    protected AlipayClient client;

    /**
     * 统一收单线下交易预创建
     *
     * @param builder
     * @return
     */
    @Override
    public Result tradePrecreate(AlipayTradePrecreateRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken()); //应用授权令牌，支付请求,该值可不设置
        request.setBizContent(builder.toJsonString());
        log.info("trade.precreate bizContent:" + request.getBizContent());

        //调用支付api
        AlipayTradePrecreateResponse response = (AlipayTradePrecreateResponse) alipayExecute(client, request);
        AlipayF2FPrecreateResult result = new AlipayF2FPrecreateResult(response);
        if (response != null && PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(response.getCode())) {
            // 退货交易成功
            result.setTradeStatus(TradeStatus.SUCCESS);

        } else if (tradeError(response)) {
            // 退货发生异常，退货状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);

        } else {
            // 其他情况表明该订单退货明确失败
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
    }

    /**
     * 统一收单交易创建接口 alipay.trade.create
     *
     * @param builder
     * @return
     */
    @Override
    public Result tradePay(AlipayTradePayRequestBuilder builder) {
        validateBuilder(builder);

        final String outTradeNo = builder.getOutTradeNo();
        String appAuthToken = builder.getAppAuthToken();

        AlipayTradePayRequest request = new AlipayTradePayRequest();
        // 设置平台参数
        request.setNotifyUrl(builder.getNotifyUrl());
        request.putOtherTextParam("app_auth_token", appAuthToken); //应用授权令牌，支付请求,该值可不设置
        request.setBizContent(builder.toJsonString());
        log.info("trade.pay bizContent:" + request.getBizContent());

        //调用支付api
        AlipayTradePayResponse response = (AlipayTradePayResponse) alipayExecute(client, request);
        AlipayF2FPayResult result = new AlipayF2FPayResult(response);

        if (response != null && PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(response.getCode())) {
            // 退货交易成功
            result.setTradeStatus(TradeStatus.SUCCESS);

        } else if (response != null && PayConstants.ALI_TRADE_STATUS.PAYING.equals(response.getCode())) {
            // 返回用户处理中，则轮询查询交易是否成功，如果查询超时，则调用撤销
            AlipayTradeQueryRequestBuilder queryBuiler = new AlipayTradeQueryRequestBuilder()
                    .setAppAuthToken(appAuthToken)
                    .setOutTradeNo(outTradeNo);
            AlipayTradeQueryResponse loopQueryResponse = loopQueryResult(queryBuiler);
            return checkQueryAndCancel(outTradeNo, appAuthToken, result, loopQueryResponse);

        } else if (tradeError(response)) {
            // 退货发生异常，退货状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);

        } else {
            // 其他情况表明该订单退货明确失败
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
    }

    /**
     * 交易查询
     *
     * @param builder
     * @return
     */
    @Override
    public Result queryTradeResult(AlipayTradeQueryRequestBuilder builder) {
        AlipayTradeQueryResponse response = tradeQuery(builder);
        AlipayF2FQueryResult result = new AlipayF2FQueryResult(response);
        if (null != response) {
            if (querySuccess(response)) {
                // 查询返回该订单交易支付成功
                result.setTradeStatus(TradeStatus.SUCCESS);
            } else if (tradeError(response)) {
                // 查询发生异常，交易状态未知
                result.setTradeStatus(TradeStatus.UNKNOWN);
            } else if (tradeWaitPay(response)) {
                result.setTradeStatus(TradeStatus.WAIT_BUYER_PAY);
            } else {
                // 其他情况均表明该订单号交易失败
                result.setTradeStatus(TradeStatus.FAILED);
            }
        } else {  //默认值
            result.setTradeStatus(TradeStatus.UNKNOWN);
        }
        return result;
    }

    /**
     * Trade query alipay trade query response.
     * 交易查询
     *
     * @param builder the builder
     * @return the alipay trade query response
     */
    protected AlipayTradeQueryResponse tradeQuery(AlipayTradeQueryRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
//        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
        request.setBizContent(builder.toJsonString());
        log.info("trade.query bizContent:" + request.getBizContent());

        return (AlipayTradeQueryResponse) alipayExecute(client, request);
    }

    /**
     * 交易退款
     *
     * @param builder
     * @return
     */
    @Override
    public Result tradeRefund(AlipayTradeRefundRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
//        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
        request.setBizContent(builder.toJsonString());
        log.info("trade.refund bizContent:" + request.getBizContent());

        AlipayTradeRefundResponse response = (AlipayTradeRefundResponse) alipayExecute(client, request);
        AlipayF2FRefundResult result = new AlipayF2FRefundResult(response);
        if (response != null && PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(response.getCode())) {
            // 退货交易成功
            result.setTradeStatus(TradeStatus.SUCCESS);

        } else if (tradeError(response)) {
            // 退货发生异常，退货状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);

        } else {
            // 其他情况表明该订单退货明确失败
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
    }

    /**
     * 统一收单交易关闭接口
     *
     * @param builder
     * @return
     */
    @Override
    public Result tradeClose(AlipayTradeCloseRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        request.setBizContent(JsonUtil.format(builder.getBizContent()));
        AlipayTradeCancelResponse response = (AlipayTradeCancelResponse) alipayExecute(client, request);
        AlipayF2FCancelResult result = new AlipayF2FCancelResult(response);
        if (response != null && PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(response.getCode())) {
            result.setTradeStatus(TradeStatus.SUCCESS);
        } else if (tradeError(response)) {
            result.setTradeStatus(TradeStatus.FAILED);
        } else {
            result.setTradeStatus(TradeStatus.UNKNOWN);
        }
        return result;
    }

    /**
     * 统一收单交易撤销接口
     *
     * @param builder
     * @return
     */
    @Override
    public Result tradeCancel_II(AlipayTradeCancelRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        request.setBizContent(JsonUtil.format(builder.getBizContent()));
        AlipayTradeCancelResponse response = (AlipayTradeCancelResponse) alipayExecute(client, request);
        AlipayF2FCancelResult result = new AlipayF2FCancelResult(response);
        if (response != null && PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(response.getCode())) {
            result.setTradeStatus(TradeStatus.SUCCESS);
        } else if (tradeError(response)) {
            result.setTradeStatus(TradeStatus.FAILED);
        } else {
            result.setTradeStatus(TradeStatus.UNKNOWN);
        }
        return result;
    }

    /**
     * 根据外部订单号outTradeNo撤销订单
     *
     * @param builder
     * @return
     */
    protected AlipayTradeCancelResponse tradeCancel(AlipayTradeCancelRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
        request.setBizContent(builder.toJsonString());
        log.info("trade.cancel bizContent:" + request.getBizContent());

        return (AlipayTradeCancelResponse) alipayExecute(client, request);
    }

    /**
     * Check query and cancel alipay f 2 f pay result.
     * 根据查询结果queryResponse判断交易是否支付成功，如果支付成功则更新result并返回，如果不成功则调用撤销
     *
     * @param outTradeNo    the out trade no
     * @param appAuthToken  the app auth token
     * @param result        the result
     * @param queryResponse the query response
     * @return the alipay f 2 f pay result
     */
    protected AlipayF2FPayResult checkQueryAndCancel(String outTradeNo, String appAuthToken, AlipayF2FPayResult result,
                                                     AlipayTradeQueryResponse queryResponse) {
        if (querySuccess(queryResponse)) {
            // 如果查询返回支付成功，则返回相应结果
            result.setTradeStatus(TradeStatus.SUCCESS);
            result.setResponse(toPayResponse(queryResponse));
            return result;
        }

        // 如果查询结果不为成功，则调用撤销
        AlipayTradeCancelRequestBuilder builder = new AlipayTradeCancelRequestBuilder().setOutTradeNo(outTradeNo);
        builder.setAppAuthToken(appAuthToken);
        AlipayTradeCancelResponse cancelResponse = cancelPayResult(builder);
        if (tradeError(cancelResponse)) {
            // 如果第一次同步撤销返回异常，则标记支付交易为未知状态
            result.setTradeStatus(TradeStatus.UNKNOWN);
        } else {
            // 标记支付为失败，如果撤销未能成功，产生的单边帐由人工处理
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
    }

    /**
     * Cancel pay result alipay trade cancel response.
     * 根据外部订单号outTradeNo撤销订单
     *
     * @param builder the builder
     * @return the alipay trade cancel response
     */
    protected AlipayTradeCancelResponse cancelPayResult(AlipayTradeCancelRequestBuilder builder) {
        AlipayTradeCancelResponse response = tradeCancel(builder);
        if (cancelSuccess(response)) {
            // 如果撤销成功，则返回撤销结果
            return response;
        }

        // 撤销失败
        if (needRetry(response)) {
            // 如果需要重试，首先记录日志，然后调用异步撤销
            log.warn("begin async cancel request:" + builder);
            asyncCancel(builder);
        }
        return response;
    }

    /**
     * Async cancel.
     * 异步撤销
     *
     * @param builder the builder
     */
    protected void asyncCancel(final AlipayTradeCancelRequestBuilder builder) {
        new ThreadPoolUtil().executorService().submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < PayConstants.MAX_CANCEL_RETRY; i++) {
                    Utils.sleep(PayConstants.CANCEL_DURATION);

                    AlipayTradeCancelResponse response = tradeCancel(builder);
                    if (cancelSuccess(response) ||
                            !needRetry(response)) {
                        // 如果撤销成功或者应答告知不需要重试撤销，则返回撤销结果（无论撤销是成功失败，失败人工处理）
                        return;
                    }
                }
            }
        });
    }

    /**
     * Loop query result alipay trade query response.
     * 轮询查询订单支付结果
     *
     * @param builder the builder
     * @return the alipay trade query response
     */
    protected AlipayTradeQueryResponse loopQueryResult(AlipayTradeQueryRequestBuilder builder) {
        AlipayTradeQueryResponse queryResult = null;
        for (int i = 0; i < PayConstants.MAX_QUERY_RETRY; i++) {
            Utils.sleep(PayConstants.QUERY_DURATION);

            AlipayTradeQueryResponse response = tradeQuery(builder);
            if (response != null) {
                if (stopQuery(response)) {
                    return response;
                }
                queryResult = response;
            }
        }
        return queryResult;
    }

    /**
     * Stop query boolean.
     * 判断是否停止查询
     *
     * @param response the response
     * @return the boolean
     */
    protected boolean stopQuery(AlipayTradeQueryResponse response) {
        if (PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(response.getCode())) {
            // 如果查询到交易成功、交易结束、交易关闭，则返回对应结果
            if ("TRADE_FINISHED".equals(response.getTradeStatus()) ||
                    "TRADE_SUCCESS".equals(response.getTradeStatus()) ||
                    "TRADE_CLOSED".equals(response.getTradeStatus())) {
                return true;
            }
        }
        return false;
    }

    /**
     * To pay response alipay trade pay response.
     *
     * @param response the response
     * @return the alipay trade pay response
     */
    // 将查询应答转换为支付应答
    protected AlipayTradePayResponse toPayResponse(AlipayTradeQueryResponse response) {
        AlipayTradePayResponse payResponse = new AlipayTradePayResponse();
        // 只有查询明确返回成功才能将返回码设置为10000，否则均为失败
        payResponse.setCode(querySuccess(response) ? PayConstants.ALI_TRADE_STATUS.SUCCESS : PayConstants.ALI_TRADE_STATUS.FAILED);
        // 补充交易状态信息
        StringBuilder msg = new StringBuilder(response.getMsg())
                .append(" tradeStatus:")
                .append(response.getTradeStatus());
        payResponse.setMsg(msg.toString());
        payResponse.setSubCode(response.getSubCode());
        payResponse.setSubMsg(response.getSubMsg());
        payResponse.setBody(response.getBody());
        payResponse.setParams(response.getParams());

        // payResponse应该是交易支付时间，但是response里是本次交易打款给卖家的时间,是否有问题
        // payResponse.setGmtPayment(response.getSendPayDate());
        payResponse.setBuyerLogonId(response.getBuyerLogonId());
        payResponse.setFundBillList(response.getFundBillList());
        payResponse.setOpenId(response.getOpenId());
        payResponse.setOutTradeNo(response.getOutTradeNo());
        payResponse.setReceiptAmount(response.getReceiptAmount());
        payResponse.setTotalAmount(response.getTotalAmount());
        payResponse.setTradeNo(response.getTradeNo());
        return payResponse;
    }

    /**
     * 交易异常，或发生系统错误
     * Trade error boolean.
     *
     * @param response the response
     * @return the boolean
     */
    protected boolean tradeError(AlipayResponse response) {
        return response == null ||
                PayConstants.ALI_TRADE_STATUS.ERROR.equals(response.getCode());
    }

    /**
     * 交易异常，或发生系统错误
     * Trade error boolean.
     *
     * @param response the response
     * @return the boolean
     */
    protected boolean tradeWaitPay(AlipayTradeQueryResponse response) {
        return response != null &&
                PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(response.getCode()) &&
                "WAIT_BUYER_PAY".equals(response.getTradeStatus());
    }

    /**
     * Cancel success boolean.
     * 撤销返回“撤销成功”
     *
     * @param response the response
     * @return the boolean
     */
    protected boolean cancelSuccess(AlipayTradeCancelResponse response) {
        return response != null &&
                PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(response.getCode());
    }

    /**
     * Need retry boolean.
     * 撤销需要重试
     *
     * @param response the response
     * @return the boolean
     */
    protected boolean needRetry(AlipayTradeCancelResponse response) {
        return response == null ||
                "Y".equals(response.getRetryFlag());
    }

    /***
     * 查询返回“支付成功”
     * Modified by Hanley 新增查询成功状态 TRADE_CLOSED-[line 310 已注释]
     * @param response the response
     * @return boolean
     */
    protected boolean querySuccess(AlipayTradeQueryResponse response) {
        return response != null &&
                PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(response.getCode()) &&
                ("TRADE_SUCCESS".equals(response.getTradeStatus()) ||
                        "TRADE_FINISHED".equals(response.getTradeStatus())
                        || "TRADE_CLOSED".equals(response.getTradeStatus())
                );
    }
}
