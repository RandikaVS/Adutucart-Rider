package com.example.adutucartrider.models;

import java.util.List;

public class PickupOrderList {

    private String userId;
    private String orderAddress;

    private String paymentType;

    private String orderId;

    private String status;

    private String waitingTime;
    private String token;

    private String total;

    private String riderId;

    private String evidence;

    public PickupOrderList(){}

    public PickupOrderList(String riderId,String userId,String orderAddress,String orderId,
                           String status,String waitingTime,String token,String evidence,String paymentType,String total){
        this.userId = userId;
        this.orderId = orderId;
        this.orderAddress = orderAddress;
        this.status = status;
        this.waitingTime = waitingTime;
        this.token = token;
        this.evidence = evidence;
        this.riderId = riderId;
        this.paymentType = paymentType;
        this.total = total;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getRiderId() {
        return riderId;
    }

    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWaitingTime() {
        return waitingTime;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public void setWaitingTime(String waitingTime) {
        this.waitingTime = waitingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAddress() {
        return orderAddress;
    }

    public void setAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

}
