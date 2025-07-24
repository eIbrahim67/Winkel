package com.eibrahim.winkel.dataClasses;

import java.io.Serializable;

public class DataRecyclerviewMyItem implements Serializable {
    private final String imageId;
    private String much;
    private String itemId;
    private double totalPriceItem;
    private final String type;
    private final String name;
    private final String category;
    private final String price;
    private final String itemSize;
    private Boolean itemLoved = false;

    public DataRecyclerviewMyItem(String category, String imageId, String name, String price, String type, String itemSize) {
        this.imageId = imageId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.type = type;
        this.itemSize = itemSize;
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

    public void setTotalPriceItem(double totalPriceItem) {
        this.totalPriceItem = totalPriceItem;
    }

    public double getTotalPriceItem() {
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

    public String getItemSize() {
        return itemSize;
    }

    public void setItemLoved(Boolean itemLoved) {
        this.itemLoved = itemLoved;
    }

    public Boolean getItemLoved() {
        return this.itemLoved;
    }
}
