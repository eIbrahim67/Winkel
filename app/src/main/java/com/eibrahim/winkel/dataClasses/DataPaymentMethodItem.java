package com.eibrahim.winkel.dataClasses;

import java.io.Serializable;

public class DataPaymentMethodItem implements Serializable {

    private final String type;
    private final String number;
    private final String cvv;
    private final String holder_name;



    public DataPaymentMethodItem(String holder_name, String type, String number, String cvv) {
        this.type = type;
        this.number = number;
        this.cvv = cvv;
        this.holder_name = holder_name;
    }

    public String getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public String getCvv() {
        return cvv;
    }
    public String getHolder_name() {
        return holder_name;
    }
}
