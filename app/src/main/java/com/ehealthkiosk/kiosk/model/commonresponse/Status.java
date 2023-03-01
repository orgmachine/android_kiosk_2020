package com.ehealthkiosk.kiosk.model.commonresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Status {

    @SerializedName("result")
    @Expose
    private int result;
    @SerializedName("message")
    @Expose
    private String message;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
