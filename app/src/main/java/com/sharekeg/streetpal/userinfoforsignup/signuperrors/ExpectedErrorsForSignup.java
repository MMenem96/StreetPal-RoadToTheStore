
package com.sharekeg.streetpal.userinfoforsignup.signuperrors;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExpectedErrorsForSignup implements Serializable
{

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("errors")
    @Expose
    private Errors errors;
    @SerializedName("message")
    @Expose
    private String message;
    private final static long serialVersionUID = -2815477835788642626L;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
