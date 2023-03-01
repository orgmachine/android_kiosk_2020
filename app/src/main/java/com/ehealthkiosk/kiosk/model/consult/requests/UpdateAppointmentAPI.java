package com.ehealthkiosk.kiosk.model.consult.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateAppointmentAPI {
    @SerializedName("appointment_id")
    @Expose
    private int patientId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("start_time")
    @Expose
    private String startTime;

    public UpdateAppointmentAPI(int patientId, String date, String startTime) {
        this.patientId = patientId;
        this.date = date;
        this.startTime = startTime;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
