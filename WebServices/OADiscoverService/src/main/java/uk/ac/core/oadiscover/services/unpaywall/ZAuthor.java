
package com.example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZAuthor {

    @SerializedName("family")
    @Expose
    private String family;
    @SerializedName("given")
    @Expose
    private String given;

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getGiven() {
        return given;
    }

    public void setGiven(String given) {
        this.given = given;
    }

    @Override
    public String toString() {
        return "ZAuthor{" + "family=" + family + ", given=" + given + '}';
    }

    
}
