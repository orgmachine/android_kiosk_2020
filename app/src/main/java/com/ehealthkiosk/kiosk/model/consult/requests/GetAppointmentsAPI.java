package com.ehealthkiosk.kiosk.model.consult.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAppointmentsAPI {

    @SerializedName("list")
    @Expose
    private boolean list;

    public GetAppointmentsAPI(boolean list) {
        this.list = list;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }
}
