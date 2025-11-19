package com.eibrahim.winkel.core;

import java.io.Serializable;

public class DataRecyclerviewItemOrderItemData implements Serializable {

    private final String itemId;
    private final String itemPrice;
    private final String itemMuch;
    private final String itemTotalPrice;
    private final String itemSize;
    private final String itemCategory;

    public DataRecyclerviewItemOrderItemData(String itemId, String itemPrice, String itemMuch, String itemTotalPrice, String itemSize, String itemCategory) {
        this.itemId = itemId;
        this.itemPrice = itemPrice;
        this.itemMuch = itemMuch;
        this.itemTotalPrice = itemTotalPrice;
        this.itemSize = itemSize;
        this.itemCategory = itemCategory;

    }

    public String getItemId() {
        return itemId;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public String getItemMuch() {
        return itemMuch;
    }

    public String getItemTotalPrice() {
        return itemTotalPrice;
    }

    public String getItemSize() {
        return itemSize;
    }

    public String getItemCategory() {
        return itemCategory;
    }
}
