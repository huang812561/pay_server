package com.ykc.service.ali;

import com.ykc.entity.ali.request.*;
import com.ykc.entity.ali.result.Result;

/**
 * @ClassName IAlipayTradeService
 * @Description 支付宝支付交易接口
 * 参考支付宝当面付接口：https://docs.open.alipay.com/194/105203/
 * @Author hgq
 * @Date 2020/3/1 13:03
 * @Version 1.0
 */
public interface IAlipayTradeService {

    /**
     * 当面付2.0预下单(生成二维码)     统一收单线下交易预创建
     * 收银员通过收银台或商户后台调用支付宝接口，生成二维码后，展示给用户，由用户扫描二维码完成订单支付。
     *
     * @param builder
     * @return
     */
    Result tradePrecreate(AlipayTradePrecreateRequestBuilder builder);

    // 电脑网站支付 alipay.trade.page.pay(统一收单下单并支付页面接口)  待增加

    /**
     * 当面付2.0流程支付              统一收单交易支付接口
     * 收银员使用扫码设备读取用户手机支付宝“付款码”/声波获取设备（如麦克风）读取用户手机支付宝的声波信息后，
     * 将二维码或条码信息/声波信息通过本接口上送至支付宝发起支付。
     *
     * @param builder
     * @return
     */
    Result tradePay(AlipayTradePayRequestBuilder builder);

    /**
     * 当面付2.0消费查询              统一收单线下交易查询
     * 该接口提供所有支付宝支付订单的查询，商户可以通过该接口主动查询订单状态，完成下一步的业务逻辑。
     * 需要调用查询接口的情况： 当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知； 调用支付接口后，返回系统错误或未知交易状态情况；
     * 调用alipay.trade.pay，返回INPROCESS的状态； 调用alipay.trade.cancel之前，需确认支付状态；
     *
     * @param builder
     * @return
     */
    Result queryTradeResult(AlipayTradeQueryRequestBuilder builder);

    /**
     * 当面付2.0消费退款              统一收单交易退款接口
     * 当交易发生之后一段时间内，由于买家或者卖家的原因需要退款时，卖家可以通过退款接口将支付款退还给买家，支付宝将在收到退款请求并且验证成功之后，
     * 按照退款规则将支付款按原路退到买家帐号上。 交易超过约定时间（签约时设置的可退款时间）的订单无法进行退款 支付宝退款支持单笔交易分多次退款，
     * 多次退款需要提交原支付订单的商户订单号和设置不同的退款单号。一笔退款失败后重新提交，要采用原来的退款单号。总退款金额不能超过用户实际支付金额
     *
     * @param builder
     * @return
     */
    Result tradeRefund(AlipayTradeRefundRequestBuilder builder);

    //当面付2.0 撤销订单              统一收单交易撤销接口
    Result tradeCancel_II(AlipayTradeCancelRequestBuilder builder);

    /**
     * 当面付2.0   统一收单交易关闭接口
     * 用于交易创建后，用户在一定时间内未进行支付，可调用该接口直接将未付款的交易进行关闭。
     *
     * @return
     */
    Result tradeClose(AlipayTradeCloseRequestBuilder builder);
}
