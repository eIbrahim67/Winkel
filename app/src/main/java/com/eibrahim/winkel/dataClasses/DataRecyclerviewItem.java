package com.eibrahim.winkel.dataClasses;

import java.io.Serializable;

public class DataRecyclerviewItem implements Serializable {
    private final String imageId;
    private String much;
    private String itemId;

    private String totalPriceItem;
    private final String type;
    private final String name;
    private final String category;
    private final String price;

    public DataRecyclerviewItem(String category, String imageId, String name, String price, String type) {
        this.imageId = imageId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.type = type;
    }

    public String getImageId() {
        return imageId;
    }

    public String getMuch() {
        return much;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getPrice() {
        return price;
    }

    public void setMuch(String much) {
        this.much = much;
    }

    public void setTotalPriceItem(String totalPriceItem) {
        this.totalPriceItem = totalPriceItem;
    }

    public String getTotalPriceItem() {
        return totalPriceItem;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return type;
    }
}
