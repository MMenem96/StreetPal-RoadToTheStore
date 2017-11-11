
package com.sharekeg.streetpal.userinfoforlogin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Birth {

    @SerializedName("d")
    @Expose
    private Integer d;
    @SerializedName("m")
    @Expose
    private Integer m;
    @SerializedName("y")
    @Expose
    private Integer y;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Birth() {
    }

    /**
     * 
     * @param d
     * @param m
     * @param y
     */
    public Birth(Integer d, Integer m, Integer y) {
        super();
        this.d = d;
        this.m = m;
        this.y = y;
    }

    public Integer getD() {
        return d;
    }

    public void setD(Integer d) {
        this.d = d;
    }

    public Integer getM() {
        return m;
    }

    public void setM(Integer m) {
        this.m = m;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

}
