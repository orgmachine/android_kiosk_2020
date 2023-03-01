package com.ehealthkiosk.kiosk.model.consult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class AppointmentDetail {

    @SerializedName("doctor")
    @Expose
    private Doctor doctor;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("appointment_number")
    @Expose
    private int appointmentNumber;
    @SerializedName("appointment_id")
    @Expose
    private String appointmentId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("dialog_id")
    @Expose
    private String dialogId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("patient")
    @Expose
    private Patient patient;
    @SerializedName("previous_appointments")
    @Expose
    private List<Appointment> prevAppointments;

    public List<Appointment> getPrevAppointments() {
        return prevAppointments;
    }

    public void setPrevAppointments(List<Appointment> prevAppointments) {
        this.prevAppointments = prevAppointments;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
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


    public String getAppointmentId() {
        return appointmentId;
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


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
