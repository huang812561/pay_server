package com.ykc.constant;

/**
 * @ClassName WechatConstants
 * @Description TODO
 * @Author hgq
 * @Date 2020/2/28 12:40
 * @Version 1.0
 */
public class WechatConstants {

    /**
     * 用户的openId
     */
    public static final String KEY_LOGIN_OPEN_ID = "openId";
    /**
     * 参数拼接APP_ID
     */
    public static final String PAY_URL_PARAM_APP_ID = "?appid=";

    /**
     * app密钥
     */
    public static final String PAY_URL_PARAM_SECRET = "&secret=";

    /**
     * 获取token的key
     */
    public static final String KEY_ACCESS_TOKEN = "access_token";

    /**
     * CODE
     */
    public static final String PAY_URL_PARAM_SECRET_JS_CODE = "&js_code=";

    /**
     * 授权类型
     */
    public static final String PAY_URL_PARAM_SECRET_GRANT_TYPE = "&grant_type=";

    /**
     * errcode
     */
    public static final String ERROR_CODE_AUTH = "errcode";

}
