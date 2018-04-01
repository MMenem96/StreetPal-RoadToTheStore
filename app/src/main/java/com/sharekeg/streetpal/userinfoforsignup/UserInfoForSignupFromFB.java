package com.sharekeg.streetpal.userinfoforsignup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by MMenem on 3/20/2018.
 */

public class UserInfoForSignupFromFB {


    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("facebookAccessToken")
    @Expose
    private String fbAccessToken;

    /**
     * No args constructor for use in serialization
     */
    public UserInfoForSignupFromFB() {
    }

    /**
     * @param user
     * @param name
     * @param email
     * @param fbAccessToken
     */
    public UserInfoForSignupFromFB(String user, String name, String email, String fbAccessToken) {
        this.user = user;
        this.name = name;
        this.email = email;
        this.fbAccessToken = fbAccessToken;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFbAccessToken() {
        return fbAccessToken;
    }

    public void setFbAccessToken(String fbAccessToken) {
        this.fbAccessToken = fbAccessToken;
    }
}


