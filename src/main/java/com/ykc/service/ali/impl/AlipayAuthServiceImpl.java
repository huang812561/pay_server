package com.ykc.service.ali.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.ykc.config.AlipayConfig;
import com.ykc.config.common.entity.ResponseEntity;
import com.ykc.constant.BusinessCodeEnum;
import com.ykc.constant.PayConstants;
import com.ykc.entity.ali.request.AlipayAuthorizationEntity;
import com.ykc.entity.ali.response.AlipayUserEntity;
import com.ykc.service.ali.IAlipayAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName AliPayService
 * @Description 支付宝支付
 * @Author hgq
 * @Date 2020/2/28 15:11
 * @Version 1.0
 */
@Service
@Slf4j
public class AlipayAuthServiceImpl extends AbsAlipayService implements IAlipayAuthService {

    private AlipayConfig alipayConfig;

    @Autowired
    public AlipayAuthServiceImpl(AlipayConfig alipayConfig) {
        this.alipayConfig = alipayConfig;
    }

    /**
     * 支付宝 — 用户授权 + 获取用户信息
     * @param authorizationEntity
     * @return
     */
    public ResponseEntity alipayAuthorization(AlipayAuthorizationEntity authorizationEntity) {

        /*auth_code 用户授权码：服务端根据用户的授权码获取access_token*/
        String authCode = authorizationEntity.getAuthCode();
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getServerUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                alipayConfig.getFormat(),
                alipayConfig.getCharset(),
                alipayConfig.getPublicKey(),
                alipayConfig.getSignType());
        AlipaySystemOauthTokenRequest oauthTokenRequest = new AlipaySystemOauthTokenRequest();
        oauthTokenRequest.setGrantType(PayConstants.GRANT_TYPE);
        oauthTokenRequest.setCode(authCode); //用户授权码
        AlipaySystemOauthTokenResponse oauthTokenResponse = new AlipaySystemOauthTokenResponse();
        try {
            oauthTokenResponse = alipayClient.execute(oauthTokenRequest);
        } catch (AlipayApiException e) {
            log.error("【支付宝】根据auth_code:{},获取用户TOKEN失败",authCode, e);
            return new ResponseEntity(BusinessCodeEnum.GRANT_FAILURE.getCode(),BusinessCodeEnum.GRANT_FAILURE.getMessage());
        }
        if(oauthTokenResponse.isSuccess()){ //token调用成功后，根据access_token获取用户信息
            AlipayUserInfoShareRequest userInfoShareRequest = new AlipayUserInfoShareRequest();
            try {
                AlipayUserInfoShareResponse userInfoShareResponse = alipayClient.execute(userInfoShareRequest,oauthTokenResponse.getAccessToken());
                if(userInfoShareResponse.isSuccess()){  //获取用户信息成功后封装参数返回前端
                    AlipayUserEntity alipayUserEntity = new AlipayUserEntity();
                    alipayUserEntity.setAlipayId(userInfoShareResponse.getUserId());
                    alipayUserEntity.setNickName(userInfoShareResponse.getNickName());
                    alipayUserEntity.setPhone(userInfoShareResponse.getPhone());
                    return new ResponseEntity(alipayUserEntity);
                }else{
                    log.error("【支付宝】根据ACCESS_TOKEN获取用户会员信息失败！");
                    return new ResponseEntity(BusinessCodeEnum.GRANT_FAILURE.getCode(),BusinessCodeEnum.GRANT_FAILURE.getMessage());
                }
            } catch (AlipayApiException e) {
                log.error("【支付宝】根据ACCESS_TOKEN获取用户会员信息失败",e);
                return new ResponseEntity(BusinessCodeEnum.GRANT_FAILURE.getCode(),BusinessCodeEnum.GRANT_FAILURE.getMessage());
            }

        }else{
            log.error("【支付宝】根据auth_code：{},获取用户ACCESS_TOKEN失败",authorizationEntity.getAuthCode());
            return new ResponseEntity(BusinessCodeEnum.AUTH_FAILURE.getCode(),BusinessCodeEnum.AUTH_FAILURE.getMessage());
        }
    }

}
