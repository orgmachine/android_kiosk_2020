package com.ehealthkiosk.kiosk.model.consult;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DoctorStatus {

    @SerializedName("available")
    @Expose
    private boolean available;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
