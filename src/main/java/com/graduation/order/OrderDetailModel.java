package com.graduation.order;

import com.graduation.goods.GoodsModel;

public class OrderDetailModel {

    //id
    private Integer id;

    //订单编号
    private Integer orderId;

    //订单编号
    private UserOrderModel order;

    //商品id
    private Integer goodsId;

    //商品id
    private GoodsModel goods;

    //商品名称
    private String goodsName;

    //商品图片
    private String imgUrl;

    //购买个数
    private Integer num;

    //单价
    private String price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public UserOrderModel getOrder() {
        return order;
    }

    public void setOrder(UserOrderModel order) {
        this.order = order;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public GoodsModel getGoods() {
        return goods;
    }

    public void setGoods(GoodsModel goods) {
        this.goods = goods;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

}
