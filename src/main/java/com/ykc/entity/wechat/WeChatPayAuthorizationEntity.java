package com.ykc.entity.wechat;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName WeChatPayAuthorizationEntity
 * @Description 微信支付-用户授权
 * @Author hgq
 * @Date 2020/2/28 11:12
 * @Version 1.0
 */
@Data
public class WeChatPayAuthorizationEntity {

    /**
     * 临时登录凭证 code jsCode
     */
    private String jsCode;

    /**
     * 认证类型 0：小程序 1：公众号
     */
    private String authType;
}
