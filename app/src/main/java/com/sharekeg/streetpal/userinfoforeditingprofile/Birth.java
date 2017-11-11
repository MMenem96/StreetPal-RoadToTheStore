
package com.sharekeg.streetpal.userinfoforeditingprofile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Birth {

    @SerializedName("d")
    @Expose
    private long d;
    @SerializedName("m")
    @Expose
    private long m;
    @SerializedName("y")
    @Expose
    private long y;

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
    public Birth(long d, long m, long y) {
        super();
        this.d = d;
        this.m = m;
        this.y = y;
    }

    public long getD() {
        return d;
    }

    public void setD(long d) {
        this.d = d;
    }

    public long getM() {
        return m;
    }

    public void setM(long m) {
        this.m = m;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

}
