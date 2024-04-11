package com.eibrahim.winkel.dataClasses;

import java.io.Serializable;

public class DataRecyclerviewItem implements Serializable {
    private final String imageId;
    private String itemId;
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

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

}
