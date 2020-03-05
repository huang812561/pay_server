package com.ykc.util;


import com.alipay.api.AlipayConstants;
import com.ykc.config.common.entity.ResponseEntity;
import com.ykc.constant.PayConstants;
import com.ykc.entity.ali.waprequest.*;
import com.ykc.service.ali.impl.AlipayCore;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * 支付宝接口测试
 * <h3>特别注意</h3>
 * 项目中提供的微信测试账户，仅用于接口功能测试，请勿用作其他用途。
 *
 * @author hanley@thlws.com
 * @date 2018/11/16
 */
@Slf4j
//@RunWith(value = SpringJUnit4ClassRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(locations = {"classpath:bootstrap.properties","classpath:application-dev.properties"})
public class AlipayPayClientTest {


    //RSA加密方式,秘钥长度为1024,支付宝公钥为可选参数
    private static final String appid = "2016042501333089";
    private static final String private_key = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAOGAI1Nf+T3F/BGb CxLQg04h1tqxtIz31T0GuiPHq25xvRacoyI4HMeJrFt72iSxnNiFjGqCLySTrKs2 zrGjbxA2dGSKkNVDWFjAx4yaDjG/nOK0sXyKwjbaf4gGjW4fq9BzP6KKG+LoZj/e wa5syDvK1UTstAc3fqCKXqOQN24vAgMBAAECgYBr5aIBfMSHWDo6OlNcF0Ucl3h0 f98CLHttjdB22k0etXFiZmRwNSUgHLHJvEMul4WS3KPJOq1P9REDg1/3A1paI345 r6qZdn8dovEX9DG6cVnjLxx7zoobCz/eGt/nWhgIQqPXSdTYhcnlncJPogSQhnUF 0lOUUIsC/RddZYXyMQJBAPlSqy5QXB/LudH8orRSjzRWHlhVswknzIMOJX9o0Tje nlwRM3vqnstvCZKaHc0lbmitHKqDfXNCA4u49zlKB5UCQQDniiVFKY5v+VQm6724 hkVvd8ctYwdx2Qj4HXdnoXfOKxOJQuSRUW2xvTSBFtFcFlC/KZNS4Zr9u01+s5Bh J12zAkAgTWimUH0PFYsWvL+r00KAimPWGAxhNEnX9P5AuqjY0Gb+ELB17pjyTImV 9+fCi4X5g+xB31VimHvzH1zQjFndAkAz0zMGaQfNvmP8ljWP8NXn3kqBuTG4ZNji GE5arsSPLAsiZQueDzjbWLtAzaes0f3e2+Jy4bZ8Zokw++YsKNCLAkBKB1vZplV2 NG9ryG2C7KGG2AgcfkUthcFPT9tXoIYPxPXwYbChjMddMDK3QK99e9CoSKLq+RY/ B0fCEdSbADgS";
    private static final String alipay_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";

    //RSA2 测试环境：秘钥长度为2048
//    private final static String appid_2 = "2019042064007077";
//    private final static String private_key_2 = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDHF0MxAZB0iHiX5/o+8ZAPzxsqFD3+T03j+hmAZAuNpXuODtSJ7/UqOLahsBUb82GnusxR5N/LCZDw81KFwmbGzAo4FwUMjmnJKKHsnHxTYYSnHjxjveteceN1CF+qBTR9T7ARQpaoDH4xZInm/TNNCj3Vp3TME63kvSEU5B2covpVhoz0NpbtNb3yDI+OC2VkXBOkjVEgatdoP5rZsiii40VnmY6iqajRuSo/ZbAJApakbWrFW/rFnvD7GJ857U8EYFpz9tsl4O9nMQ2tRdRe67e9bvAbKCSMKi6Fs7BHIGVItkZkzmfd9uWf8Sd7kDng+ejQ37koRyo9e45eEjKBAgMBAAECggEAc6NtYZ+Y+jbyZLlXPwwsMyKGkrBjOb1yboKsIUj69PEzjNXMWbpI2pdKbSvLdwqspDH5z29zFYme6HVMb2j1LW8CWIX2Zs6pXy/8rWRPSlcQJ8mGJiD//0GDbkzI1AZl8ItCpyPYQg/PlHzjJInZLFxQgjLbwlWD4aKA/pNpJ7ryJTxGuXDezHU57Wv8nJzb3p/R5/FmjBxJeYHaSOO8p5bYZO86bXjjBGqi85PyYttkQrMaeaNXHyKUw/IYob06ERW6fWRnsTa20O1pVzAu+uu7kyJyAyEyZsu+ZzRocatRguknc9/6YQ77K0tpQ9UzHkZXVMaSN62rIfBCiHhdaQKBgQD+sGFQ7oM0jOCJHvwhpnPV08FM/3O7cq9g6UoGwo957qvCmQMOQi+HHJHA82a9uutbIHD22QzfJHgNMZkGEb9MyIUUTcIryLvQU6EcSFMoqcdm5ah0NDz2TypGrdKP/b20T+tbOR6F7YO5IY+OTxwH0BxP8i/Wo216tBJSZkXBuwKBgQDIHZ394jizyDyFvLy6TGfgTBPI8k84pWMw8hk8l02vqdmZ1d33otvph8LtnqRfTRHs8Cve6li001LYmzk46FT9M4g8DdB5CmHnODnwlVGI5AZfPgm6V15iwwf3v6ZgoerJhGOasMFCOSBU7GoYXW/UzgxMoDzkL2NDesEaHz4K8wKBgQCcmQa9JGo4HYbRyONuLVJ73z+zgD5Auztqcwa5MVfWuRGTDVH7qmZexQo5gW9iqVOdLE1I+hwX9+x48E4OlUrygziQS19gXIRei408PpwjHIEmm38AND5nlDWCT85wCqxh+eHUrUI/RtL/OctyzOGHTEacQWV4q/PcxmudjpTFkQKBgFSre1qdaeJfeYQYbD3i8ByN890U8QIFsCdYd+cUGZGWqvQU8jrB1IxFnetOMTwFbu0yXU2PGANCWo49dwOmAbe0IxNNwQRFqVDckF9DvmDOIggQRqtqJgxherCPjUTbnWywMDiWSt7LLuvfr7ApcJS4ramKLtlZV4WIncWArI6TAoGAW9KFDlU8bDXtarZjQ7ECM8bETceSo+DvxNBtrkqqOm3kAYLZ/RK3btumxeLTxSQQVL694IzmMuCtFeA0EKPX5+8jDFt6/SaJWgkKURuhkaZle4bCHkFUPXAnYDdZZfOYbfTv8VZFRuNk2rT1yni25/LHVo8HTsW400EDbQ75Pn8=";
//    private final static String alipay_public_key_2 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3QBSPkfwf5gsgWaYjpxcFuoxq6qU4wrx4EE9RI06XVp52HDM26ti/H215tPXg4M7JWUhRs6jpI7fEzXRFKDrVHygxkM2cctIR7BHTr3CWODHybIPI3FtuoO7D6qGYZSoxp1hOUcNg61UGEdBJwkIAH/98teap0VWBkGU++gNUhUw8cfrSjz2HUswFkeabUmPfGIAJLx92FV9/C15Z5nRxFE1Y5CuK8nmlJdVJhlpd5LW+Rr1UwTTvzCVJug3dMefKNLDyJ0RE9t7OGzxE3PBeQtVCSIM2lPtduSp8BzwXBIeEVu4XebOvQvjUNNlOvUw3VmH5+yCDt5WuoulbCgq/QIDAQAB";

    //RSA2 沙箱测试环境：使用沙箱环境，请修改AlipayCore包中 支付宝网关
    private final static String appid_2 = "2016091800540160";
    private final static String private_key_2 = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCWtXTCMOqYCjnA1YALtyGUNOcuWi1+8dHwrQVQH7bNQZo4hzkRAsHqA0I1UkhlS5SkuuGix9Fv4H+JVEU4A/EBDY91vqOeHRwdVEJNFj5F9/gjzysaTBWEQTzRXlgzjD2y9LQzsPHUyw+U3nP0duyrcYURSTf75TTFG7J/SW3aEsTUAQ1Ql4MZ/cxVLU7Ohx+OGsAC8iCQms8n3CX+q/xkhlTs0ek6TGZdTf6rX5ZGpxHzYIkOwjF7cPwogsk6JEblUzUhbJooqxanT/IKfCeRVyVKM7joP8mHnm06tIpzJusarirxLuw+rcTka5ZpfYWdSfRfZrJkZizfTaJ+PR2rAgMBAAECggEAXgv0Ra6C5sgSzDwvQKBSSwtMbMeHdp1vFlLSavKvf5z9oWL5AJ2I5wCLk3y6gsPMiZO5dHxfPQKKc+drno4PGkodCSwjLGsb/U10ZmiICd+5MJd6XVm3X6XaTwkh32EXq5I61bsI3nKtUYgUMiz4efBISsY3MnERIiOrmvT2oUtzvxcov4gz1T5tAuPO0QK3A/bVf6eaSx1kr5eU2KLGgAyrwNM+87b7Q8zfC3vnrSI+ExDA8DzEyxCM00WknUok4zVOn0d8D2X8NYWujhnq88gxDsdCblxzO/tbUhJpGXmJj8t2OGNdzUs+iY5u/U9golDr5MZqQYT9maTgr/PlOQKBgQD14SUvhWWcv7l8XalaUvjEauo0BwMoQMCUmaeYLpbvphT4fmrSfPsXhwmGsNgESK/VEdOs3CodNaVyPM0zuqx2Fg+rV5/KfQfTAe1fsY3xAITitIyAJdbSSOf3JrJ+bW/g5rqvi35h2zg9ACBZhvv7na5fmM62ZFvQaDcO3ckxxQKBgQCc6X0gv6cIsKxSHKerA7frdiFyGPVvvbrLTAVEF4HHVpgycEY6KCBFKTTof93AeDRMlz5G2WcHT1uOAM0jwTB61nLbF0ZYlO8NUMfVS6NsOzuHfaIOY+8n7f27QcLkeyENfLZ+jyy8lisGBkcQxL7VbDI/xAGr9wgE850+hZ04rwKBgHtas9wucXb9XOI+3wq4m6N2+/6UEgpsyfEDUWhw3zpluRmd2pu1Jgd8sNPI/ubPTJ95rReI8PvsgQ9Z35z3JzSrBrtwX3Mn3PgIfH1Ix6xEFWbjK6XS1f5zoySEXoI9BybuunP9aK9RWtqHqLMhnGN0jRnawMISgVmCHzUvIZP9AoGAQ/waHdVL3sUFdHI75ymuR1OgfBn6RVbUFv8yDOWOcjq/qKLimpIGIVEQPmggQSMTrdedwP6y2AT3KxiHySUMBzzg+LIl5X1p8b/9RRoHjX5AdZzmWQvNc+R2meqNMYdzs69ENqN8A52guHSB1+8Wq8u8Hcqj2skmbVbL22nyy7sCgYAgzkVgymv6WhP3Nczy84c5mUepLM9JTc/IoADaqpYRR1SHUUQweLzjQCsYtYFSm5zeicglIiLRV0qfXslWOdfijtyH7Zl+nDTO2R1xY/Nya+OSFdWbmYFwmBCUjwab41OSXFeqMoqrptNCjWFvAqkY5MKzqa3eDCS91BpbdEcVbg==";
    private final static String alipay_public_key_2 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA97PgojBzyg3BzCIc0nFPjmfxUD6euW8OSV0/JT1aaXhcpwPc6K1g6qNeiq7K/VZrdCavLKwVA5wIaXFobtIE8slgpLyOTWe4z1MEKnH6sKjtPySUWfhAzoWuuu8bw565OksTAXRsjukCN2zo5E3JT9g0t6KlBv6JNn9X+mUb6kthpcl5O7Gcu0uEGJbb9T/eTxMhVMrbcXcV2J4eyekMNHPKTOpOWix9/fftHWv/lr0S795FFLYMaiii8KyOsw0HidTi/31CjI4NTLHjF05SDPTpDpok5E7DeyjUklbFywgxaw2PbP+9lEa/jR5+b05HcdSjdTs1yOo7Nk62GyAGOQIDAQAB";

    static AlipayCore alipayCore;

    /**
     * 初始化函数.
     */
    @Before
    public void init() {
        AlipayCore.ClientBuilder clientBuilder = new AlipayCore.ClientBuilder();
        //signType=rsa时，可不传 alipay_public_key
//        alipayCore = clientBuilder.setAppId(appid).setPrivateKey(private_key).setSignType(AlipayConstants.SIGN_TYPE_RSA).build();

        //signType=rsa2时，必须传 支付宝应用公钥 alipay_public_key
        alipayCore = clientBuilder.setPublicKey(alipay_public_key_2).setAppId(appid_2).setPrivateKey(private_key_2).setSignType(AlipayConstants.SIGN_TYPE_RSA2).build();
    }


    /***
     * 统一收单线下交易预创建（扫码支付）
     * 暂未开启异步通知,完成预订单创建后,请自行实现Thread调用query()查询支付结果
     */
    @Test
    public void testPreCreate() {

        try {
            AlipayQrcodeRequest request = new AlipayQrcodeRequest();
            request.setSubject("购买商品");
            request.setOutTradeNo(System.currentTimeMillis() + "");
            request.setBody("测试下单");
            request.setOperatorId("990001");
            request.setStoreId("0001025104489");
            request.setTotalAmount("10.5");
            ResponseEntity response = AlipayClient.preCreate(request, alipayCore);
            log.debug("统一收单线下交易预创建 response=" + JsonUtil.format(response));
            Map<String, Object> result = (Map<String, Object>) response.getData();
            assertTrue(PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(result.get("CODE")));
        } catch (Exception e) {
            log.error("统一收单线下交易预创建", e);
        }
    }

    /***
     * 统一收单线下交易查询
     */
    @Test
    public void testQuery() {
        try {
            ResponseEntity response = AlipayClient.query("1583310653942", alipayCore);
            log.debug("统一收单线下交易查询：response=" + JsonUtil.format(response));
            Map<String, Object> result = (Map<String, Object>) response.getData();
            assertTrue(!"".equals(result.get("CODE")));
        } catch (Exception e) {
            log.error("统一收单线下交易查询出现异常", e);
        }
    }

    /***
     * 统一收单交易撤销接口
     */
    @Test
    public void testCancel() {
        try {
            ResponseEntity response = AlipayClient.cancel("1583310653942", alipayCore);
            log.debug("response=" + JsonUtil.format(response));
            Map<String, Object> result = (Map<String, Object>) response.getData();
            assertTrue(PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(result.get("CODE")));
        } catch (Exception e) {
            log.error("统一收单交易撤销接口出现异常",e);
        }
    }

    @Test
    public void testRefund() {
        try {
            AlipayRefundRequest request = new AlipayRefundRequest();
            request.setOutTradeNo("1583306981230");//商户单号
//            request.setTradeNo("2018062821001004510561182960");//支付宝交易号
            request.setRefundAmount("10.01");
            request.setRefundReason("测试全部退款");
            ResponseEntity response = AlipayClient.refund(request, alipayCore);
            log.debug("response=" + JsonUtil.format(response));
            Map<String, Object> result = (Map<String, Object>) response.getData();
            assertTrue(PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(result.get("CODE")));
        } catch (Exception e) {
            log.error("测试全额退款", e);
        }
    }

    /***
     * 统一收单交易退款接口(部分退款)
     */
    @Test
    public void testPartRefund() {

        try {
            AlipayRefundRequest request = new AlipayRefundRequest();
            request.setOutTradeNo("1583306981230");
//            request.setTradeNo("2018062821001004510561182960");
            request.setRefundAmount("20");
            request.setRefundReason("测试部分退款");
            request.setOutRequestNo(System.currentTimeMillis() + "");   //部分退款，此值必填
            ResponseEntity response = AlipayClient.refund(request, alipayCore);
            log.debug("response=" + JsonUtil.format(response));
            Map<String, Object> result = (Map<String, Object>) response.getData();
            assertTrue(PayConstants.ALI_TRADE_STATUS.SUCCESS.equals(result.get("CODE")));
        } catch (Exception e) {
            log.error("测试部分退款", e);
        }
    }

    /***
     * 统一收单交易支付接口（条码支付）
     */
    @Test
    public void testPay() {
        try {
            AlipayTradeRequest request = new AlipayTradeRequest();
            //必须参数
            request.setTotalAmount("100.32");
            request.setStoreId("00001025104487");
            request.setOperatorId("hanley001");
            request.setAuthCode("281616839544161936");//用户支付宝展示的支付条码
            request.setOutTradeNo(System.currentTimeMillis() + "");
            request.setSubject("CI测试买单001");

//            如下为可选参数，全部参数请查看 AlipayTradeRequest
//            request.setBody("测试支付");
//            request.setDiscountableAmount("0");
//            request.setUndiscountableAmount("0");
//            request.setSellerId(partner_id_0);
//            List<GoodsDetail> list = new ArrayList<GoodsDetail>();
//            list.add(GoodsDetail.newInstance("g01","name1",10,1));
//            list.add(GoodsDetail.newInstance("g02","name2",12,3));
//            request.setGoodsDetailList(list);

            ResponseEntity response = AlipayClient.pay(request, alipayCore);
            log.info("统一收单交易支付结果：{}",response.toString());
            //response就是支付结果,具体请参考相关属性说明
        } catch (Exception e) {
            log.error("统一收单交易支付接口出现异常", e);
        }
    }

    /***
     * H5网页支付接口（手机网站支付）
     */
    @Test
    public void testPayInMobileSite() {

        try {
            AlipayMobileSiteRequest request = new AlipayMobileSiteRequest();
            request.setNotifyUrl("异步通知地址,支付宝通知支付结果");
            request.setReturnUrl("同步返回地址,完成支付后自动转向的地址");
            AlipayMobileSiteRequest.BizContent bizContent = new AlipayMobileSiteRequest.BizContent();
            bizContent.setTotalAmount("0.01");
            bizContent.setSubject("测试H5(手机网页)支付");
            //bizContent.setSeller_id(partner_id);
            bizContent.setProductCode("p0001");
            bizContent.setOutTradeNo(System.currentTimeMillis() + "");
            request.setBizContent(bizContent);

            String html = AlipayClient.payInMobileSite(request, alipayCore);
            log.debug("html=" + html);
            assertNotNull(html);
            //html结果直接显示在页面即可
        } catch (Exception e) {
            log.error("H5网页支付接口出现异常", e);
        }
    }


    /***
     * 电脑网页支付接口
     */
    @Test
    public void testPayInWebSite() {
        try {
            AlipayWebSiteRequest request = new AlipayWebSiteRequest();
            request.setNotifyUrl("异步通知地址,支付宝通知支付结果");
            request.setReturnUrl("同步返回地址,完成支付后自动转向的地址");
            AlipayWebSiteRequest.BizContent bizContent = new AlipayWebSiteRequest.BizContent();
            bizContent.setTotalAmount("0.01");
            bizContent.setSubject("测试电脑网站支付");
            bizContent.setBody("测试");
            bizContent.setProductCode("p0001");
            bizContent.setOutTradeNo(System.currentTimeMillis() + "");
            String html = AlipayClient.payInWebSite(request, alipayCore);
            assertNotNull(html);
            //html结果直接显示在页面即可
        } catch (Exception e) {
            log.error("电脑网页支付接口出现异常",e);
        }
    }
}
