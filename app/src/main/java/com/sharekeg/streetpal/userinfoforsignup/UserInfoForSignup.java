
package com.sharekeg.streetpal.userinfoforsignup;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfoForSignup {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("pass")
    @Expose
    private String pass;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("birth")
    @Expose
    private Birth birth;
    @SerializedName("email")
    @Expose
    private String email;
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
    public UserInfoForSignup() {
    }

    /**
     * 
     * @param work
     * @param phone
     * @param birth
     * @param email
     * @param name
     * @param gender
     * @param user
     * @param pass
     */
    public UserInfoForSignup(String user, String pass, String name, String gender, Birth birth, String email, String phone, String work) {
        super();
        this.user = user;
        this.pass = pass;
        this.name = name;
        this.gender = gender;
        this.birth = birth;
        this.email = email;
        this.phone = phone;
        this.work = work;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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
