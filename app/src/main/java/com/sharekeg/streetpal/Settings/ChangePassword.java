package com.sharekeg.streetpal.Settings;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangePassword implements Serializable
{

    @SerializedName("old-pass")
    @Expose
    private String oldPass;
    @SerializedName("new-pass")
    @Expose
    private String newPass;
    private final static long serialVersionUID = -4179075549449096607L;

    public ChangePassword(String oldUserPassword, String textnewpassword) {
        this.oldPass = oldUserPassword ;
        this.newPass =textnewpassword ;
    }

    public String getOldPass() {
        return oldPass;
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

}