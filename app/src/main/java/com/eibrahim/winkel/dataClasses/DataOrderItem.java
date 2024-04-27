package com.eibrahim.winkel.dataClasses;

import java.io.Serializable;
import java.util.List;

public class DataOrderItem implements Serializable {
    private final String custId;
    private final List<DataRecyclerviewItemOrderItemData> listItemsData;

    private final String totalPrice;

    public DataOrderItem(String custId, List<DataRecyclerviewItemOrderItemData> listItemsData, String totalPrice) {
        this.custId = custId;
        this.listItemsData = listItemsData;
        this.totalPrice = totalPrice;

    }


    public String getCustId() {
        return custId;
    }

    public List<DataRecyclerviewItemOrderItemData> getListItemsData() {
        return listItemsData;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

}
