package com.eibrahim.winkel.dataClasses;

import java.io.Serializable;

public class DataRecyclerviewItemOrder implements Serializable {
    private final String orderId;
    private final String custId;
    private String farmerId;

    private final String totalPrice;

    public DataRecyclerviewItemOrder(String orderId, String custId, String totalPrice) {
        this.orderId = orderId;
        this.custId = custId;
        this.totalPrice = totalPrice;

    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustId() {
        return custId;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

}
