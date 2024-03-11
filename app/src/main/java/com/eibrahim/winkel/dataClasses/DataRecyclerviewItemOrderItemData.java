package com.eibrahim.winkel.dataClasses;

import java.io.Serializable;

public class DataRecyclerviewItemOrderItemData implements Serializable {

    private final String itemId;
    private final String itemPrice;
    private final String itemMuch;
    private final String itemTotalPrice;

    public DataRecyclerviewItemOrderItemData(String itemId, String itemPrice, String itemMuch, String itemTotalPrice) {
        this.itemId = itemId;
        this.itemPrice = itemPrice;
        this.itemMuch = itemMuch;
        this.itemTotalPrice = itemTotalPrice;

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

}
