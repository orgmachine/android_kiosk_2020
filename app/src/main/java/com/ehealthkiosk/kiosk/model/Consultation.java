package com.ehealthkiosk.kiosk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Consultation {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("doctor")
    @Expose
    private Doctor doctor;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("appointment_id")
    @Expose
    private String appointmentId;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("start_quarter")
    @Expose
    private Integer startQuarter;
    @SerializedName("duration_quarters")
    @Expose
    private Integer durationQuarters;
    @SerializedName("patient_name")
    @Expose
    private String patientName;
    @SerializedName("str_date")
    @Expose
    private String strDate;

    public Consultation(String status) {
        this.status = status;
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStartQuarter() {
        return startQuarter;
    }

    public void setStartQuarter(Integer startQuarter) {
        this.startQuarter = startQuarter;
    }

    public Integer getDurationQuarters() {
        return durationQuarters;
    }

    public void setDurationQuarters(Integer durationQuarters) {
        this.durationQuarters = durationQuarters;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}