
package com.sharekeg.streetpal.forgotpassword;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Errors implements Serializable
{

    @SerializedName("pass")
    @Expose
    private String pass;
    private final static long serialVersionUID = -4356944344418140393L;

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

}
