
package com.sharekeg.streetpal.userinfoforeditingprofile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UsersInfoForEditingProfile {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("birth")
    @Expose
    private Birth birth;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("work")
    @Expose
    private String work;

    /**
     * No args constructor for use in serialization
     * 
     */
    public UsersInfoForEditingProfile() {
    }

    /**
     * 
     * @param work
     * @param birth
     * @param phone
     * @param email
     * @param name
     * @param gender
     */
    public UsersInfoForEditingProfile(String name, String email, String phone, Birth birth, String gender, String work) {
        super();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birth = birth;
        this.gender = gender;
        this.work = work;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Birth getBirth() {
        return birth;
    }

    public void setBirth(Birth birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

}
