package com.example.adutucartrider.models;

import java.util.List;

public class PickupOrderList {

    private String userId;
    private String orderAddress;

    private String orderId;

    private String status;


    public PickupOrderList(){}

    public PickupOrderList(String userId,String address,String orderKey,String status){
        this.userId = userId;
        this.orderId = orderKey;
        this.orderAddress = address;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderKey() {
        return orderId;
    }

    public void setOrderKey(String orderId) {
        this.orderId = orderId;
    }

    public String getAddress() {
        return orderAddress;
    }

    public void setAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

}
