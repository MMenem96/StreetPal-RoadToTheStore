
package com.sharekeg.streetpal.userinfoforsignup.signuperrors;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sharekeg.streetpal.userinfoforsignup.*;

public class Errors implements Serializable
{

    private final static long serialVersionUID = 1350928241547707453L;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("birth")
    @Expose
    private com.sharekeg.streetpal.userinfoforsignup.Birth birth;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public com.sharekeg.streetpal.userinfoforsignup.Birth getBirth() {
        return birth;
    }

    public void setBirth(com.sharekeg.streetpal.userinfoforsignup.Birth birth) {
        this.birth = birth;
    }

}
