package com.ehealthkiosk.kiosk.model;

import com.ehealthkiosk.kiosk.model.commonresponse.Status;

public class SendEmailResponse {
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
