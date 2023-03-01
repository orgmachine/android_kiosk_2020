package com.ehealthkiosk.kiosk.model.consult.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAppointmentDetailsAPI {

    @SerializedName("id")
    @Expose
    private int id;

    public GetAppointmentDetailsAPI(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
