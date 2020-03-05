package com.ykc.service.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ykc.config.WechatPayConfig;
import com.ykc.config.common.entity.RequestData;
import com.ykc.config.common.entity.ResponseEntity;
import com.ykc.constant.BusinessCodeEnum;
import com.ykc.constant.WechatConstants;
import com.ykc.entity.wechat.WeChatPayAuthorizationEntity;
import com.ykc.util.CodeHelper;
import com.ykc.util.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.weixin4j.WeixinException;
import org.weixin4j.http.HttpsClient;
import org.weixin4j.http.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WeChatPayService
 * @Description 微信支付
 * @Author hgq
 * @Date 2020/2/28 12:37
 * @Version 1.0
 */
@Service
@Slf4j
public class WeChatPayService {

    /**
     * 微信支付配置
     */
    private WechatPayConfig weChatPayConfig;

    @Autowired
    public WeChatPayService(WechatPayConfig weChatPayConfig) {
        this.weChatPayConfig = weChatPayConfig;
    }

    /**
     * 微信授权
     *
     * @param requestParams 请求参数
     * @return 响应体
     * @throws WeixinException 微信异常
     */
    public ResponseEntity wechatAuthorization(RequestData<WeChatPayAuthorizationEntity> requestParams) throws WeixinException {

        try {
            StringBuilder param = new StringBuilder();
            param.append(weChatPayConfig.getAccessTokenUrl())
                    .append(WechatConstants.PAY_URL_PARAM_APP_ID)
                    .append(weChatPayConfig.getAppId())
                    .append(WechatConstants.PAY_URL_PARAM_SECRET)
                    .append(weChatPayConfig.getAppSecret())
                    .append(WechatConstants.PAY_URL_PARAM_SECRET_JS_CODE)
                    .append(requestParams.getBody().getJsCode())
                    .append(WechatConstants.PAY_URL_PARAM_SECRET_GRANT_TYPE)
                    .append(weChatPayConfig.getGrantType());
            //使用code换取oauth2的授权access_token
            String authUrl = param.toString();

            log.debug("[微信授权]-授权请求URL：{}", authUrl);
            HttpsClient http = new HttpsClient();
            Response response = http.get(authUrl);
            JSONObject jsonObject = response.asJSONObject();
            log.info("[微信授权]-授权响应信息：{}", jsonObject.toString());
            if (!jsonObject.isEmpty()) {
                if (CodeHelper.isNotNull(jsonObject.get(WechatConstants.ERROR_CODE_AUTH))) {
                    return new ResponseEntity(BusinessCodeEnum.AUTH_FAILURE.getCode(), BusinessCodeEnum.AUTH_FAILURE.getMessage());
                }
                String openId = jsonObject.get("openid").toString();
                String token = JwtHelper.createJWT(JwtHelper.JWT_ID, openId, JwtHelper.JWT_TTL);
                Map<String, Object> result = new HashMap<>(4);
                result.put(WechatConstants.KEY_LOGIN_OPEN_ID, openId);
                result.put("token", token);
                log.info("[微信授权]-返回授权响应信息：{}", JSON.toJSONString(result));
                return new ResponseEntity(result);
            } else {
                log.info("[微信授权]-认证失败！");
                return new ResponseEntity(BusinessCodeEnum.GRANT_FAILURE.getCode(), BusinessCodeEnum.GRANT_FAILURE.getMessage());
            }
        } catch (WeixinException e) {
            log.info("[微信授权]-认证异常：{}", e);
            return new ResponseEntity(BusinessCodeEnum.GRANT_FAILURE.getCode(), BusinessCodeEnum.GRANT_FAILURE.getMessage());
        }
    }

}
