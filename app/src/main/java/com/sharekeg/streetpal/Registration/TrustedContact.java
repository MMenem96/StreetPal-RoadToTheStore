
package com.sharekeg.streetpal.Registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrustedContact {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TrustedContact() {
    }

    /**
     * 
     * @param phone
     * @param email
     * @param name
     */
    public TrustedContact(String name, String phone, String email) {
        super();
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}
