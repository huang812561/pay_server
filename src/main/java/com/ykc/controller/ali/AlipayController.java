package com.ykc.controller.ali;

import com.ykc.base.action.BaseAction;
import com.ykc.config.common.entity.Message;
import com.ykc.config.common.entity.RequestData;
import com.ykc.config.common.entity.ResponseEntity;
import com.ykc.entity.ali.waprequest.AlipayQrcodeRequest;
import com.ykc.util.AlipayClient;
import com.ykc.util.AlipayCore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName AlipayController
 * @Description 支付宝-支付场景
 * @Author hgq
 * @Date 2020/2/28 16:56
 * @Version 1.0
 */
@RestController
@RequestMapping("aliPay")
public class AlipayController extends BaseAction {

    /**
     * 支付宝-统一下单线下扫码支付
     * @param qrcodeRequestRequestData
     * @return
     */
    @RequestMapping("/webWay")
    public Message webPay(@RequestBody RequestData<AlipayQrcodeRequest> qrcodeRequestRequestData){
        return convert(qrcodeRequestRequestData.getHeader(),new ResponseEntity());
    }

}
