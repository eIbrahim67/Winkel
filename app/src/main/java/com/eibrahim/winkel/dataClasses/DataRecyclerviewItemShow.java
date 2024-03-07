package com.eibrahim.winkel.dataClasses;

import java.io.Serializable;

public class DataRecyclerviewItemShow implements Serializable {
    private final String image;
    private final String productId;
    private final String title;
    private final String price;
    private String details;


    public DataRecyclerviewItemShow(String productId, String image, String title, String price, String details) {
        this.image = image;
        this.title = title;
        this.details = details;;
        this.price = price;;
        this.productId = productId;;
    }

    public String getImage() {
        return image;
    }


    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getProductId() {
        return productId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
