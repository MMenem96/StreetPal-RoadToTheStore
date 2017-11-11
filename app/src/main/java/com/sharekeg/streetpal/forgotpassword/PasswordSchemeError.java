
package com.sharekeg.streetpal.forgotpassword;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PasswordSchemeError implements Serializable
{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("errors")
    @Expose
    private Errors errors;
    private final static long serialVersionUID = -6320588778711844741L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }

}
