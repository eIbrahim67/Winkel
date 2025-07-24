package com.eibrahim.winkel.dataClasses;

import java.io.Serializable;

public class DataProductItem implements Serializable {

    private String category;
    private String imageId;
    private String name;
    private String price;
    private String type;
    private String itemId;
    private String userId;  // 👈 Add this

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    // Required no-arg constructor for Firestore
    public DataProductItem() {
    }

    public DataProductItem(String category, String imageId, String name, String price, String type) {
        this.category = category;
        this.imageId = imageId;
        this.name = name;
        this.price = price;
        this.type = type;
    }

    // Getters and setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
