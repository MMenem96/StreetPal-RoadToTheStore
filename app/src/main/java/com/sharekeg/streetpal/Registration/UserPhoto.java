
package com.sharekeg.streetpal.Registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserPhoto {

    @SerializedName("base64")
    @Expose
    private String base64;

    /**
     * No args constructor for use in serialization
     * 
     */
    public UserPhoto() {
    }

    /**
     * 
     * @param base64
     */
    public UserPhoto(String base64) {
        super();
        this.base64 = base64;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

}
