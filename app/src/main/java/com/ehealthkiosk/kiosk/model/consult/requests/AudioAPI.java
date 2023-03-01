package com.ehealthkiosk.kiosk.model.consult.requests;

import java.io.File;

public class AudioAPI {

    private Integer appointment_id;
    private File audio;

    public AudioAPI(Integer appointment_id, File audio) {
        this.appointment_id = appointment_id;
        this.audio = audio;
    }

    public Integer getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(Integer appointment_id) {
        this.appointment_id = appointment_id;
    }

    public File getAudio() {
        return audio;
    }

    public void setAudio(File audio) {
        this.audio = audio;
    }
}
