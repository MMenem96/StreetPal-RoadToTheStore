
package com.sharekeg.streetpal.Login;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginCredentialsWithFB implements Serializable {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("accessToken")
    @Expose
    private String accessToken;
    private final static long serialVersionUID = -9137262528416003056L;

    /**
     * No args constructor for use in serialization
     */
    public LoginCredentialsWithFB() {
    }

    /**
     * @param accessToken
     * @param email
     */
    public LoginCredentialsWithFB(String email, String accessToken) {
        super();
        this.email = email;
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}