package com.sharekeg.streetpal.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MMenem on 3/20/2018.
 */

public class LoginCredentialsWithFB {





    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("accessToken")
    @Expose
    private String accessToken;

    /**
     * No args constructor for use in serialization
     *
     */
    public LoginCredentialsWithFB() {
    }

    /**
     *
     * @param email
     * @param accessToken
     */
    public LoginCredentialsWithFB(String email, String accessToken) {
        super();
        this.email = email;
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String user) {
        this.email = user;
    }

    public void setFbAccessToken(String pass) {
        this.accessToken = accessToken;
    }



}
