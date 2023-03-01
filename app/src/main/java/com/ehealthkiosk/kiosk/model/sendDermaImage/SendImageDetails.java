package com.ehealthkiosk.kiosk.model.sendDermaImage;

import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendImageDetails {

    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private sendImageResultData data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public sendImageResultData getData() {
        return data;
    }

    public void setData(sendImageResultData data) {
        this.data = data;
    }
}
