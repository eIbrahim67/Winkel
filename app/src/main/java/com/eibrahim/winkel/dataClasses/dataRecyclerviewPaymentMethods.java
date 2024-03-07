package com.eibrahim.winkel.dataClasses;

public class dataRecyclerviewPaymentMethods {

    private final String type;
    private final String number;
    private final String date;



    public dataRecyclerviewPaymentMethods(String type, String number, String date) {
        this.type = type;
        this.number = number;
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public String getDate() {
        return date;
    }
}
