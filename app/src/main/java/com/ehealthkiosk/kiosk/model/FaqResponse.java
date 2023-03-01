package com.ehealthkiosk.kiosk.model;

import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaqResponse {
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private FaqData data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public FaqData getData() {
        return data;
    }

    public void setData(FaqData data) {
        this.data = data;
    }
}
