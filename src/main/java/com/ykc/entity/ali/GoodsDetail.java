package com.ykc.entity.ali;


import com.alipay.api.internal.mapping.ApiField;
import com.google.gson.annotations.SerializedName;
import com.ykc.util.Utils;
import lombok.Data;


/**
 * @ClassName GoodsDetail
 * @Description 商品明细
 * @Author hgq
 * @Date 2020/3/1 13:03
 * @Version 1.0
 */
@Data
public class GoodsDetail {

    /**
     * 商品的编号
     */
    @SerializedName("goods_id")
    private String goodsId;

    /**
     * 支付宝定义的统一商品编号
     */
    @SerializedName("alipay_goods_id")
    private String alipayGoodsId;

    /**
     * 商品名称
     */
    @SerializedName("goods_name")
    private String goodsName;

    /**
     * 商品数量
     */
    private int quantity;

    /**
     * 商品价格，此处单位为元，精确到小数点后2位
     */
    private String price;

    /**
     * 商品类别
     */
    @SerializedName("goods_category")
    private String goodsCategory;

    /**
     * 商品详情 - 商品描述信息
     */
    private String body;

    // 创建一个商品信息，参数含义：商品的编号、商品名称、商品价格（单位为分）、商品数量
    public static GoodsDetail newInstance(String goodsId, String goodsName, long price, int quantity) {
        GoodsDetail goodsDetail = new GoodsDetail();
        goodsDetail.setGoodsId(goodsId);
        goodsDetail.setGoodsName(goodsName);
        goodsDetail.setPrice(Utils.toAmount(price));
        goodsDetail.setQuantity(quantity);
        return goodsDetail;
    }

}
