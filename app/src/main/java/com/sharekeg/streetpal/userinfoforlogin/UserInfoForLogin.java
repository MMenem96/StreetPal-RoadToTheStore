
package com.sharekeg.streetpal.userinfoforlogin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfoForLogin {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("email-verified")
    @Expose
    private Boolean emailVerified;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("birth")
    @Expose
    private Birth birth;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("work")
    @Expose
    private String work;

    /**
     * No args constructor for use in serialization
     * 
     */
    public UserInfoForLogin() {
    }

    /**
     * 
     * @param work
     * @param phone
     * @param birth
     * @param email
     * @param name
     * @param gender
     * @param active
     * @param user
     * @param emailVerified
     */
    public UserInfoForLogin(String user, Boolean emailVerified, Boolean active, Birth birth, String email, String gender, String name, String phone, String work) {
        super();
        this.user = user;
        this.emailVerified = emailVerified;
        this.active = active;
        this.birth = birth;
        this.email = email;
        this.gender = gender;
        this.name = name;
        this.phone = phone;
        this.work = work;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Birth getBirth() {
        return birth;
    }

    public void setBirth(Birth birth) {
        this.birth = birth;
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

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

}
