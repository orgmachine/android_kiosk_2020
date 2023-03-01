package com.ehealthkiosk.kiosk.model.consult.requests;

import com.ehealthkiosk.kiosk.model.consult.Doctor;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DoctorListAPI {

    @SerializedName("filter_id")
    @Expose
    private int filter_id;

    public DoctorListAPI(int filter_id) {
        this.filter_id = filter_id;
    }
    public DoctorListAPI() {
    }

    public int getFilter_id() {
        return filter_id;
    }

    public void setFilter_id(int filter_id) {
        this.filter_id = filter_id;
    }
}
