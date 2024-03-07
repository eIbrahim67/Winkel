package com.eibrahim.winkel.dataClasses;

import java.io.Serializable;

public class DataRecyclerviewItemBasket implements Serializable {
    private final String id;
    private final String image;

    private final String title;

    private final String totalPrice;
    private final String details;

    public DataRecyclerviewItemBasket(String id, String image, String title , String totalPrice, String details) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.totalPrice = totalPrice;
        this.details = details;

    }


    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getDetails() {
        return details;
    }
}
