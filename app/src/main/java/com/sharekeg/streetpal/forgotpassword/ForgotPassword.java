
package com.sharekeg.streetpal.forgotpassword;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ForgotPassword implements Serializable
{

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("pass")
    @Expose
    private String pass;
    private final static long serialVersionUID = -7779610292613953892L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ForgotPassword() {
    }

    /**
     * 
     * @param code
     * @param user
     * @param pass
     */
    public ForgotPassword(String user, String code, String pass) {
        super();
        this.user = user;
        this.code = code;
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

}
