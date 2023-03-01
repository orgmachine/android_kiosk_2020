package com.ehealthkiosk.kiosk.model.consult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Appointment {

    @SerializedName("doctor")
    @Expose
    private Doctor doctor;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("has_started")
    @Expose
    private boolean hasStarted;
    @SerializedName("appointment_number")
    @Expose
    private int appointmentNumber;
    @SerializedName("patient_id")
    @Expose
    private int patientId;
    @SerializedName("appointment_id")
    @Expose
    private String appointmentId;
    @SerializedName("appointment_str")
    @Expose
    private String appointmentStr;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("patient_name")
    @Expose
    private String patientName;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("str_datetime")
    @Expose
    private String strDatetime;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("date")
    @Expose
    private String date;

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public String getStrDatetime() {
        return strDatetime;
    }

    public void setStrDatetime(String strDatetime) {
        this.strDatetime = strDatetime;
    }

    public String getAppointmentStr() {
        return appointmentStr;
    }

    public void setAppointmentStr(String appointmentStr) {
        this.appointmentStr = appointmentStr;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(int appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getAppointmentId() {
        return appointmentId.substring(0, 7);
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
