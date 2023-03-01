package com.ehealthkiosk.kiosk.model.consult.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SlotsAPI {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("doctor_id")
    @Expose
    private int doctorId;

    public SlotsAPI(String date, int doctorId) {
        this.date = date;
        this.doctorId = doctorId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
}
