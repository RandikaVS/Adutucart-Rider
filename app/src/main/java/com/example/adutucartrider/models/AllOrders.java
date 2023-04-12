package com.example.adutucartrider.models;

import java.util.List;

public class AllOrders {

    private List<PendingOrderList> pendingOrderLists;

    public AllOrders(){}

    public AllOrders(List<PendingOrderList> pendingOrderLists){
        this.pendingOrderLists = pendingOrderLists;
    }

    public List<PendingOrderList> getPendingOrderLists() {
        return pendingOrderLists;
    }

    public void setPendingOrderLists(List<PendingOrderList> pendingOrderLists) {
        this.pendingOrderLists = pendingOrderLists;
    }
}
