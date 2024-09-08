package com.graduation.order;

import com.graduation.user.UserModel;

import java.util.List;

public class UserOrderModel {

    //订单编号
    private Integer id;

    //下单用户
    private Integer userId;

    //下单用户
    private UserModel user;

    //订单总价
    private String price;

    //支付方式
    private String payType;

    //收货人
    private String receiver;

    //收货人电话
    private String phone;

    //收货人地址
    private String address;

    //订单状态
    private String status;

    //创建时间
    private String createTime;

    //订单详情
    private List<OrderDetailDO> detail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<OrderDetailDO> getDetail() {
        return detail;
    }

    public void setDetail(List<OrderDetailDO> detail) {
        this.detail = detail;
    }
}
