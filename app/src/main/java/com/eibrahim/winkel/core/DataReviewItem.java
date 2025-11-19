package com.eibrahim.winkel.core;

import java.io.Serializable;

public class DataReviewItem implements Serializable {
    private final String name;
    private final String username;
    private final String review;
    private final String logo;
    private final String rate;

    public DataReviewItem(String logo, String name, String username, String review, String rate) {
        this.name = name;
        this.username = username;
        this.review = review;
        this.rate = rate;
        this.logo = logo;
    }

    public String getUsername() {
        return username;
    }

    public String getReview() {
        return review;
    }

    public String getRate() {
        return rate;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

}
