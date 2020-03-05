package com.ykc.service.ali;

import com.ykc.config.common.entity.ResponseEntity;
import com.ykc.entity.ali.request.AlipayAuthorizationEntity;

/**
 * @ClassName IAlipayService
 * @Description 支付宝支付
 * @Author hgq
 * @Date 2020/2/28 16:58
 * @Version 1.0
 */
public interface IAlipayAuthService {

    ResponseEntity alipayAuthorization(AlipayAuthorizationEntity authorizationEntity);
}
