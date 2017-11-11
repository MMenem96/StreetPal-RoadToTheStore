
package com.sharekeg.streetpal.Settings;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteTrustedcontact implements Serializable
{

    @SerializedName("name")
    @Expose
    private String name;
    private final static long serialVersionUID = -6188132137779573265L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}