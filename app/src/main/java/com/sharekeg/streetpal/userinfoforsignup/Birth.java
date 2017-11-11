
package com.sharekeg.streetpal.userinfoforsignup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Birth {

    @SerializedName("d")
    @Expose
    private int d;
    @SerializedName("m")
    @Expose
    private int m;
    @SerializedName("y")
    @Expose
    private int y;

    /**
     * No args constructor for use in serialization
     */
    public Birth() {
    }

    /**
     * @param d
     * @param m
     * @param y
     */
    public Birth(int d, int m, int y) {
        super();
        this.d = d;
        this.m = m;
        this.y = y;
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
