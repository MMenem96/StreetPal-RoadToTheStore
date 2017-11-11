package com.sharekeg.streetpal.forgotpassword;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserEmailForForgetPassword implements Serializable {

    @SerializedName("email")
    @Expose
    private String email;
    private final static long serialVersionUID = 6397231872113132645L;

    /**
     * No args constructor for use in serialization
     */
    public UserEmailForForgetPassword() {
    }

    /**
     * @param email
     */
    public UserEmailForForgetPassword(String email) {
        super();
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}