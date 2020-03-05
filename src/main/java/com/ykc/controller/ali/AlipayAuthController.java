package com.ykc.controller.ali;

import com.ykc.base.action.BaseAction;
import com.ykc.config.common.entity.Message;
import com.ykc.config.common.entity.RequestData;
import com.ykc.config.common.entity.ResponseEntity;
import com.ykc.constant.BusinessCodeEnum;
import com.ykc.entity.ali.request.AlipayAuthorizationEntity;
import com.ykc.service.ali.IAlipayAuthService;
import com.ykc.util.CodeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName AliPayAuthController
 * @Description 支付宝-用户授权
 * @Author hgq
 * @Date 2020/2/28 13:54
 * @Version 1.0
 */
@RestController
@RequestMapping("aliPay")
public class AlipayAuthController extends BaseAction {

    private IAlipayAuthService alipayAuthService;

    @Autowired
    public AlipayAuthController(IAlipayAuthService alipayAuthService) {
        this.alipayAuthService = alipayAuthService;
    }

    @RequestMapping("/aliPayAuth")
    private Message aliPayAuth(@RequestBody RequestData<AlipayAuthorizationEntity> aliPayAuthEntity){
        if(CodeHelper.isNull(aliPayAuthEntity.getBody()) || CodeHelper.isNull(aliPayAuthEntity.getBody().getAuthCode())){
            return convert(aliPayAuthEntity.getHeader(),new ResponseEntity(BusinessCodeEnum.PARAMS_ERROR.getCode(),BusinessCodeEnum.PARAMS_ERROR.getMessage()));
        }
        return convert(aliPayAuthEntity.getHeader(),alipayAuthService.alipayAuthorization(aliPayAuthEntity.getBody()));
    }

}
