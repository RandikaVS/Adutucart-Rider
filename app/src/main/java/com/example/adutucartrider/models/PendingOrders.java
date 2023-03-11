package com.example.adutucartrider.models;

public class PendingOrders extends PendingOrderList{

    private String productId;
    private String productImage;
    private String productName;
    private String qty;
    private String unitPrice;
    private String total;

    public PendingOrders(){}

    public PendingOrders(String productImage,String productName,String qty,String total , String unitPrice,String productId){

        this.productName = productName;
        this.productImage = productImage;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.total = total;
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTotal() {
        return total;
    }

    public String getQty() {
        return qty;
    }

    public String getProductName() {
        return productName;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getProductImage() {
        return productImage;
    }
}
