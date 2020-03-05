package com.ykc.controller.wechat;

import com.ykc.base.action.BaseAction;
import com.ykc.config.common.entity.Message;
import com.ykc.config.common.entity.RequestData;
import com.ykc.config.common.entity.ResponseEntity;
import com.ykc.constant.BusinessCodeEnum;
import com.ykc.entity.wechat.WeChatPayAuthorizationEntity;
import com.ykc.service.wechat.WeChatPayService;
import com.ykc.util.CodeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.weixin4j.WeixinException;

/**
 * @ClassName WeChatPayController
 * @Description 微信支付
 * @Author hgq
 * @Date 2020/2/28 11:12
 * @Version 1.0
 */
@RestController
@RequestMapping("weChatPay")
public class WechatPayController extends BaseAction {

    private WeChatPayService weChatPayService;

    @Autowired
    public WechatPayController(WeChatPayService weChatPayService) {
        this.weChatPayService = weChatPayService;
    }

    /**
     * 微信 — 用户授权 + 获取用户openId
     *
     * @param weChatPayAuthEntity 请求参数
     * @return Message
     * @throws WeixinException
     */
    @PostMapping("/weChatPayAuth")
    public Message weChatPayAuth(@RequestBody RequestData<WeChatPayAuthorizationEntity> weChatPayAuthEntity) throws WeixinException {
        if (CodeHelper.isNull(weChatPayAuthEntity.getBody()) || CodeHelper.isNullOrEmpty(weChatPayAuthEntity.getBody().getJsCode())) {
            return convert(weChatPayAuthEntity.getHeader(), new ResponseEntity(BusinessCodeEnum.PARAMS_ERROR.getCode(), "授权失败，请重试！"));
        }
        return convert(weChatPayAuthEntity.getHeader(), weChatPayService.wechatAuthorization(weChatPayAuthEntity));
    }

}
