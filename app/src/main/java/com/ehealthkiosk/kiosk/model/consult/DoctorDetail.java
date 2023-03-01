package com.ehealthkiosk.kiosk.model.consult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DoctorDetail {

    @SerializedName("doctor")
    @Expose
    private List<Doctor> doctor;

    public List<Doctor> getDoctor() {
        return doctor;
    }

    public void setDoctor(List<Doctor> doctors) {
        this.doctor = doctors;
    }
}
