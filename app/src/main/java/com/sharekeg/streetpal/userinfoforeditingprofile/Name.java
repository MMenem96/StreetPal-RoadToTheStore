package com.sharekeg.streetpal.userinfoforeditingprofile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by HP on 03/03/17.
 */

public class Name implements Serializable {
    @SerializedName("first")
    @Expose
    private String first;
    @SerializedName("middle")
    @Expose
    private String middle;
    @SerializedName("last")
    @Expose
    private String last;
    private final static long serialVersionUID = 936795112756721890L;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
