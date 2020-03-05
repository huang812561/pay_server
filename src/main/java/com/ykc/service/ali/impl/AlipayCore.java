package com.ykc.service.ali.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.ExtendParams;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.ykc.config.AlipayConfig;
import com.ykc.config.common.entity.ResponseEntity;
import com.ykc.constant.BusinessCodeEnum;
import com.ykc.entity.ali.request.*;
import com.ykc.entity.ali.result.*;
import com.ykc.entity.ali.waprequest.*;
import com.ykc.service.ali.IAlipayTradeService;
import com.ykc.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName AlipayCore
 * @Description 支付宝支付核心处理入口, 封装支付常用API，包括当面付、新版网页支付、退款操作、订单查询;
 * 使用任何方法前,需先行设置ClientBuilder中核心参数,并调用build方法初始化.<br>
 * @Author hgq
 * @Date 2020/2/29 22:46
 * @Version 1.0
 */
@Slf4j
@Service
public class AlipayCore {

    private AlipayConfig alipayConfig;

    public final static String ALIPAY_SERVER_URL = "https://openapi.alipay.com/gateway.do";
    public final static String ALIPAY_SAND_BOX_SERVER_URL = "https://openapi.alipaydev.com/gateway.do";
    public final static String CHARSET = "utf-8";
    public final static String FORMAT = "JSON";

    /**
     * 支付交易核心服务,调用API前,调用前API前先行初始化
     */
    private IAlipayTradeService tradeService;

    @Autowired
    public AlipayCore(AlipayConfig alipayConfig, IAlipayTradeService tradeService) {
        this.alipayConfig = alipayConfig;
        this.tradeService = tradeService;
    }

    public ClientBuilder builder;

    /**
     * 第三步：参数构造,初始化 tradeService,builder
     *
     * @param builder
     */
    public AlipayCore(ClientBuilder builder) {
        if (org.apache.commons.lang.StringUtils.isEmpty(builder.getAppId())) {
            throw new NullPointerException("appid should not be NULL!");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(builder.getPrivateKey())) {
            throw new NullPointerException("private should not be NULL!");
        }
        this.builder = builder;
        AlipayTradeServiceImpl.ClientBuilder alipayTradebuilder = new AlipayTradeServiceImpl.ClientBuilder();
        alipayTradebuilder.setAlipayPublicKey(builder.getPublicKey());
        alipayTradebuilder.setAppid(builder.getAppId());
//        alipayTradebuilder.setCharset(alipayConfig.getCharset());
//        alipayTradebuilder.setFormat(alipayConfig.getFormat());
//        alipayTradebuilder.setGatewayUrl(alipayConfig.getServerUrl());
        alipayTradebuilder.setCharset(CHARSET);
        alipayTradebuilder.setFormat(FORMAT);
//        alipayTradebuilder.setGatewayUrl(ALIPAY_SERVER_URL);
        alipayTradebuilder.setGatewayUrl(ALIPAY_SAND_BOX_SERVER_URL);
        alipayTradebuilder.setPrivateKey(builder.getPrivateKey());
        alipayTradebuilder.setSignType(builder.getSignType());
        tradeService = alipayTradebuilder.build();

    }

    public static class ClientBuilder {

        /**
         * 第二步：参数构造,supper class instance.
         *
         * @return
         */
        public AlipayCore build() {

            if (StringUtils.isBlank(appId)) {
                throw new NullPointerException("please set appId first");
            }
            if (StringUtils.isBlank(privateKey)) {
                throw new NullPointerException("please set private_key first!");
            }
            if (StringUtils.isBlank(signType)) {
                throw new NullPointerException("please set sign_type first!");
            } else {
                if (signType.equalsIgnoreCase(AlipayConstants.SIGN_TYPE_RSA2) && StringUtils.isBlank(publicKey)) {
                    throw new NullPointerException("please set alipay_public_key first,when the sign_type is RSA2!");
                }
            }
            return new AlipayCore(this);
        }

        /**
         * 支付宝分配给开发者的应用ID
         */
        private String appId;
        /**
         * 公钥
         */
        private String publicKey;
        /**
         * 私钥
         */
        private String privateKey;
        /**
         * 商户生成签名字符串所使用的签名算法类型
         */
        private String signType;

        public String getAppId() {
            return appId;
        }

        public ClientBuilder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public ClientBuilder setPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public ClientBuilder setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public String getSignType() {
            return signType;
        }

        public ClientBuilder setSignType(String signType) {
            this.signType = signType;
            return this;
        }
    }

    /***
     * 支付宝手机网页支付,支付宝新接口,在手机上可直接调用支付宝APP完成支付宝(有安装支付宝APP情况)<br>
     * @see <a href="https://docs.open.alipay.com/203/105285">https://docs.open.alipay.com/203/105285</a>
     * @see <a href="https://docs.open.alipay.com/203/105286">https://docs.open.alipay.com/203/105286</a>
     * @param request the request 新版网页支付参数
     * @return string 支付宝产生用于网页支付的html.
     * @throws Exception the exception
     */
    public String payInMobileSite(AlipayMobileSiteRequest request) throws Exception {
        log.debug("支付宝手机网页支付 payInMobileSite request=\n" + request.toString());
        String form = "<font style='color: red'>请求支付宝超时,请稍后再试!</font>";
        try {
            if (null == builder) {
                throw new Exception("Please set AlipayCore.ClientBuider first.");
            }
            String bizContent = JsonUtil.beanToJsontring(request.getBizContent());
            AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_SAND_BOX_SERVER_URL,
                    builder.getAppId(), builder.getPrivateKey(), FORMAT, CHARSET, builder.getPublicKey());

            AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();
            alipayRequest.setReturnUrl(request.getReturnUrl());
            alipayRequest.setNotifyUrl(request.getNotifyUrl());
            alipayRequest.setBizContent(bizContent);
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            log.debug("payInMobileSite response={}", form);
        }

        return form;
    }

    /***
     * 支付宝电脑网站支付，该操作会跳转到支付宝的支付页面中完成支付动作，以 异步/同步 的形式告知支付结果
     * @param request 支付参数对象
     * @return 返回支付宝页面, 直接输出在页面中
     * @throws Exception 程序异常
     */
    public String payInWebSite(AlipayWebSiteRequest request) throws Exception {
        log.debug("支付宝电脑网站支付 payInWebSite request=\n" + request.toString());
        String form = "<font style='color: red'>请求支付宝超时,请稍后再试!</font>";

        try {
            if (null == builder) {
                throw new Exception("Please set AlipayCore.ClientBuider first.");
            }
            String bizContent = JsonUtil.beanToJsontring(request.getBizContent());
            AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_SAND_BOX_SERVER_URL,
                    builder.getAppId(), builder.getPrivateKey(), FORMAT, CHARSET, builder.getPublicKey());

            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setReturnUrl(request.getReturnUrl());
            alipayRequest.setNotifyUrl(request.getNotifyUrl());
            alipayRequest.setBizContent(bizContent);
            form = alipayClient.pageExecute(alipayRequest).getBody();
        } catch (Exception e) {
            log.error("支付宝电脑网站支付出现异常", e);
            throw e;
        } finally {
            log.debug("payInWebSite response=\n" + form);
        }
        return form;
    }


    /**
     * 当面付.扫码支付，指用户打开支付宝钱包中的“扫一扫”功能，扫描商户针对每个订单实时生成的订单二维码，并在手机端确认支付。
     * 收银员通过收银台或商户后台调用支付宝接口，生成二维码后，展示给用户，由用户扫描二维码完成订单支付。
     *
     * @param request the request 扫码支付所需参数
     * @return the alipay qrcode response 扫码支付结果
     * @throws Exception the exception
     * @see <a href="https://docs.open.alipay.com/194/105170">https://docs.open.alipay.com/194/105170</a>
     */
    public ResponseEntity preCreate(AlipayQrcodeRequest request) throws Exception {

        log.debug("当面付.扫码支付 preCreate request=\n" + request.toString());
        ResponseEntity response = new ResponseEntity();
        Map<String, Object> resultMap = new HashMap<>();

        try {
            if (null == tradeService) {
                throw new Exception("Please set AlipayCore.ClientBuider first and call build().");
            }

            // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
            ExtendParams extendParams = new ExtendParams();

            AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                    .setSubject(request.getSubject())
                    .setTotalAmount(request.getTotalAmount())
                    .setOutTradeNo(request.getOutTradeNo())
                    .setUndiscountableAmount(request.getUndiscountableAmount())
                    .setSellerId(request.getSellerId())
                    .setBody(request.getBody())
                    .setOperatorId(request.getOperatorId())
                    .setStoreId(request.getStoreId())
                    .setExtendParams(extendParams);

            AlipayF2FPrecreateResult result = (AlipayF2FPrecreateResult) tradeService.tradePrecreate(builder);
            resultMap.put("TRADE_STATUS", result.getTradeStatus());
            resultMap.put("CODE", result.getResponse().getCode());
            resultMap.put("MSG", result.getResponse().getMsg());
            resultMap.put("SUB_CODE", result.getResponse().getSubCode());
            resultMap.put("SUB_MSG", result.getResponse().getSubMsg());

            switch (result.getTradeStatus()) {
                case SUCCESS:
                    resultMap.put("RESULT_DESC", "支付宝预下单成功!");
                    resultMap.put("out_trade_no", result.getResponse().getOutTradeNo());
                    resultMap.put("qr_code", result.getResponse().getQrCode());
                    break;
                case FAILED:
                    resultMap.put("RESULT_DESC", "支付宝预下单失败!");
                    break;
                case UNKNOWN:
                    resultMap.put("RESULT_DESC", "系统异常，预下单状态未知!");
                    break;
                default:
                    resultMap.put("RESULT_DESC", "不支持的交易状态，交易返回异常!");
                    break;
            }
            response.setData(resultMap);
        } catch (Exception e) {
            log.error("支付宝预下单出现异常", e);
            response.setResultCode(BusinessCodeEnum.FAILURE.getCode());
        } finally {
            log.debug("precreate response=\n" + response.toString());
        }
        return response;
    }

    /***
     * 当面付.条码支付,是支付宝给到线下传统行业的一种收款方式。商户使用扫码枪等条码识别设备扫描用户支付宝钱包上的条码/二维码，完成收款。用户仅需出示付款码，所有操作由商户端完成。
     * @see <a href="https://docs.open.alipay.com/194/105170">https://docs.open.alipay.com/194/105170</a>
     * @param request the request 条码支付 所需参数
     * @return alipay trade response 条码支付所需参数
     * @throws Exception the exception
     */
    public ResponseEntity pay(AlipayTradeRequest request) throws Exception {
        log.debug("当面付.条码支付 pay request=\n" + request.toString());
        ResponseEntity response = new ResponseEntity();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            if (null == tradeService) {
                throw new Exception("Please set AlipayCore.ClientBuider first and call build().");
            }

            // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
            String providerId = "2088102176187953";
            ExtendParams extendParams = new ExtendParams();
            extendParams.setSysServiceProviderId(providerId);

            AlipayTradePayRequestBuilder builder = new AlipayTradePayRequestBuilder()
                    //            .setAppAuthToken(appAuthToken)
                    .setOutTradeNo(request.getOutTradeNo()).setSubject(request.getSubject()).setAuthCode(request.getAuthCode())
                    .setTotalAmount(request.getTotalAmount()).setStoreId(request.getStoreId())
                    .setUndiscountableAmount(request.getUndiscountableAmount()).setBody(request.getBody()).setOperatorId(request.getOperatorId())
                    .setExtendParams(extendParams).setSellerId(request.getSellerId())
                    .setGoodsDetailList(request.getGoodsDetailList());

            AlipayF2FPayResult result = (AlipayF2FPayResult) tradeService.tradePay(builder);
            resultMap.put("TRADE_STATUS", result.getTradeStatus());
            resultMap.put("CODE", result.getResponse().getCode());
            resultMap.put("MSG", result.getResponse().getMsg());
            resultMap.put("SUB_CODE", result.getResponse().getSubCode());
            resultMap.put("SUB_MSG", result.getResponse().getSubMsg());

            switch (result.getTradeStatus()) {
                case SUCCESS:
                    BeanUtilsBean copyBean = BeanUtilsBean.getInstance();
                    copyBean.copyProperties(response, result.getResponse());
                    resultMap.put("RESULT_DESC", "支付宝支付成功!");
                    break;
                case FAILED:
                    resultMap.put("RESULT_DESC", "支付宝支付失败!");
                    break;
                case UNKNOWN:
                    resultMap.put("RESULT_DESC", "系统异常，订单状态未知!");
                    break;
                default:
                    resultMap.put("RESULT_DESC", "不支持的交易状态，交易返回异常!");
                    break;
            }
            response.setData(resultMap);
        } catch (Exception e) {
            log.error("支付宝支付出现异常", e);
            response.setResultCode(BusinessCodeEnum.FAILURE.getCode());
        } finally {
            log.debug("pay response=\n" + response.toString());
        }
        return response;
    }


    /**
     * 查询支付订单.
     *
     * @param outTradeNo the out trade no
     * @return the alipay query response
     * @throws Exception the exception
     */
    public ResponseEntity query(String outTradeNo) throws Exception {

        log.debug("query outTradeNo=" + outTradeNo);
        ResponseEntity response = new ResponseEntity();
        Map<String, Object> resultMap = new HashMap<>();

        try {
            if (null == tradeService) {
                throw new Exception("Please set AlipayCore.ClientBuider first and call build().");
            }

            AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
                    .setOutTradeNo(outTradeNo);
            AlipayF2FQueryResult result = (AlipayF2FQueryResult) tradeService.queryTradeResult(builder);
            resultMap.put("TRADE_STATUS", result.getTradeStatus());
            resultMap.put("CODE", result.getResponse().getCode());
            resultMap.put("MSG", result.getResponse().getMsg());
            resultMap.put("SUB_CODE", result.getResponse().getSubCode());
            resultMap.put("SUB_MSG", result.getResponse().getSubMsg());

            switch (result.getTradeStatus()) {
                case SUCCESS:
                    resultMap.put("RESULT_DESC", "查询返回该订单信息成功！");
                    break;
                case FAILED:
                    resultMap.put("RESULT_DESC", "查询返回该订单支付失败或被关闭!");
                    break;
                case UNKNOWN:
                    resultMap.put("RESULT_DESC", "系统异常，订单支付状态未知!");
                    break;
                case WAIT_BUYER_PAY:
                    resultMap.put("RESULT_DESC", "查询返回该订单信息处于待支付状态");
                    break;
                default:
                    resultMap.put("RESULT_DESC", "不支持的交易状态，交易返回异常!");
                    break;
            }
            response.setData(resultMap);
        } catch (Exception e) {
            log.error("查询支付宝支付订单出现异常", e);
            response.setResultCode(BusinessCodeEnum.FAILURE.getCode());
        } finally {
            log.debug("query response=\n" + response.toString());
        }
        return response;
    }


    /**
     * 支付宝退款，商户因业务原因需要退款时，可通过成功交易的商户订单号或支付宝交易号进行退款 ，支持部分退款。
     * 退款接口会根据外部请求号out_request_no幂等返回，因此同一笔需要多次部分退款时，必须使用不同的out_request_no
     *
     * @param request the request 退款参数
     * @return 退款结果 alipay refund response
     * @throws Exception the exception
     * @see <a href="https://docs.open.alipay.com/api_1/alipay.trade.refund">https://docs.open.alipay.com/api_1/alipay.trade.refund</a>
     */
    public ResponseEntity refund(AlipayRefundRequest request) throws Exception {

        log.debug("refund request=\n" + request.toString());
        ResponseEntity response = new ResponseEntity();
        Map<String, Object> resultMap = new HashMap<>();

        try {
            if (null == tradeService) {
                throw new Exception("Please set AlipayCore.ClientBuider first and call build().");
            }

            AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder();

            if (StringUtils.isBlank(request.getOutTradeNo()) && StringUtils.isBlank(request.getTradeNo())) {
                throw new Exception("trade_no , out_trade_no 不能同时为空");
            }
            if (StringUtils.isBlank(request.getRefundAmount())) {
                throw new Exception("refundAmount 不能为空");
            }
            if (StringUtils.isNotBlank(request.getOutTradeNo())) {
                builder.setOutTradeNo(request.getOutTradeNo());
            }
            if (org.apache.commons.lang.StringUtils.isNotBlank(request.getTradeNo())) {
                builder.setTradeNo(request.getTradeNo());
            }
            if (org.apache.commons.lang.StringUtils.isNotBlank(request.getRefundReason())) {
                builder.setRefundReason(request.getRefundReason());
            }
            if (org.apache.commons.lang.StringUtils.isNotBlank(request.getStoreId())) {
                builder.setStoreId(request.getStoreId());
            }
            if (org.apache.commons.lang.StringUtils.isNotBlank(request.getOutRequestNo())) {
                builder.setOutRequestNo(request.getOutRequestNo());
            }

            //trade_no , out_trade_no 不能同时存在,否则支付宝会报错ACQ.TRADE_STATUS_ERROR.交易状态不合法
            if (org.apache.commons.lang.StringUtils.isNotBlank(builder.getOutTradeNo()) && org.apache.commons.lang.StringUtils.isNotBlank(builder.getTradeNo())) {
                builder.setTradeNo(null);
            }

            builder.setRefundAmount(request.getRefundAmount());
            AlipayF2FRefundResult result = (AlipayF2FRefundResult) tradeService.tradeRefund(builder);
            resultMap.put("TRADE_STATUS", result.getTradeStatus());
            resultMap.put("CODE", result.getResponse().getCode());
            resultMap.put("MSG", result.getResponse().getMsg());
            resultMap.put("SUB_CODE", result.getResponse().getSubCode());
            resultMap.put("SUB_MSG", result.getResponse().getSubMsg());

            switch (result.getTradeStatus()) {
                case SUCCESS:
//                    BeanUtilsBean copyBean = BeanUtilsBean.getInstance();
//                    copyBean.copyProperties(response, result.getResponse());
                    resultMap.put("RESULT_DESC", "支付宝退款成功!");
                    break;
                case FAILED:
                    resultMap.put("RESULT_DESC", "支付宝退款失败!");
                    break;
                case UNKNOWN:
                    resultMap.put("RESULT_DESC", "系统异常，订单退款状态未知!");
                    break;
                default:
                    resultMap.put("RESULT_DESC", "不支持的交易状态，交易返回异常!");
                    break;
            }
            response.setData(resultMap);
        } catch (Exception e) {
            log.error("支付宝退款出现异常", e);
            response.setResultCode(BusinessCodeEnum.FAILURE.getCode());
        } finally {
            log.debug("refund response={}", response.toString());
        }
        return response;
    }

    /**
     * 撤销订单
     *
     * @param outTradeNo the out trade no
     * @return the alipay cancel response
     * @throws Exception the exception
     */
    public ResponseEntity cancel(String outTradeNo) throws Exception {
        ResponseEntity response = new ResponseEntity();
        Map<String, Object> resultMap = new HashMap<>();

        try {
            AlipayTradeCancelRequestBuilder builder = new AlipayTradeCancelRequestBuilder();
            builder.setOutTradeNo(outTradeNo);

            AlipayF2FCancelResult result = (AlipayF2FCancelResult) tradeService.tradeCancel_II(builder);
            resultMap.put("TRADE_STATUS", result.getTradeStatus());
            resultMap.put("CODE", result.getResponse().getCode());
            resultMap.put("MSG", result.getResponse().getMsg());
            resultMap.put("SUB_CODE", result.getResponse().getSubCode());
            resultMap.put("SUB_MSG", result.getResponse().getSubMsg());
            switch (result.getTradeStatus()) {
                case SUCCESS:
//                    BeanUtilsBean copyBean = BeanUtilsBean.getInstance();
//                    copyBean.copyProperties(response, result.getResponse());
                    resultMap.put("RESULT_DESC", "支付宝撤销成功！");
                    break;
                case FAILED:
                    resultMap.put("RESULT_DESC", "支付宝撤销失败！");
                    break;
                case UNKNOWN:
                    resultMap.put("RESULT_DESC", "系统异常，订单撤销状态未知!");
                    break;
                default:
                    resultMap.put("RESULT_DESC", "不支持的交易状态，交易返回异常!");
                    break;
            }
            response.setData(resultMap);
        } catch (Exception e) {
            log.error("支付宝订单撤销出现异常", e);
            response.setResultCode(BusinessCodeEnum.FAILURE.getCode());
        } finally {
            log.debug("cancel response=\n" + response.toString());
        }

        return response;

    }

}
