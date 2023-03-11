package com.example.adutucartrider.models;

import java.util.List;

public class PendingOrderList {

    private String userId;
    private String address;

    private String orderKey;
    private String subTotal;
    private String paymentType;
    private String status;
    private List<PendingOrders> pendingOrders;

    public PendingOrderList(){}

    public PendingOrderList(String userId,List<PendingOrders> pendingOrders,String address,String orderKey,String subTotal,String paymentType,String status){
        this.userId = userId;
        this.orderKey = orderKey;
        this.pendingOrders = pendingOrders;
        this.address = address;
        this.subTotal = subTotal;
        this.paymentType = paymentType;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getOrderKey() {
        return orderKey;
    }

    public void setOrderKey(String orderKey) {
        this.orderKey = orderKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setPendingOrders(List<PendingOrders> pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public List<PendingOrders> getPendingOrders() {
        return pendingOrders;
    }
}
