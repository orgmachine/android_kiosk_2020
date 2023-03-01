package com.ehealthkiosk.kiosk.model.consult.requests;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DocumentsAPI {

    @SerializedName("appointment_id")
    @Expose
    private Integer appointmentId;

    public DocumentsAPI(int aid) {
        this.appointmentId = aid;
    }
}