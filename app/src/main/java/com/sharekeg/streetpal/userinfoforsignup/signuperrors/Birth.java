
package com.sharekeg.streetpal.userinfoforsignup.signuperrors;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Birth implements Serializable
{

    @SerializedName("d")
    @Expose
    private String d;
    @SerializedName("m")
    @Expose
    private String m;
    @SerializedName("y")
    @Expose
    private String y;
    private final static long serialVersionUID = -7501417768341444290L;

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

}
