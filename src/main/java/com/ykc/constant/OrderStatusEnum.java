package com.ykc.constant;

/**
 * @ClassName OrderStatusEnum
 * @Description 订单状态
 * @Author hgq
 * @Date 2020/2/29 22:04
 * @Version 1.0
 */
public enum OrderStatusEnum {

    WAIT_PAY("10", "待付款"),			// 代付款
    PAID("20", "已付款"),				// 已付款
    CANCELED("30", "已取消"),			// 已取消
    CLOSED("40", "交易关闭");			// 超时未支付, 交易关闭
    ;

    OrderStatusEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    private String status;
    private String desc;

}
