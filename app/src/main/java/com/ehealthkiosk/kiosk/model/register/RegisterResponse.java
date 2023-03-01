package com.ehealthkiosk.kiosk.model.register;

import com.ehealthkiosk.kiosk.model.commonresponse.Status;

public class RegisterResponse {

    private Status status;
    private RegisterData data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public RegisterData getData() {
        return data;
    }

    public void setData(RegisterData data) {
        this.data = data;
    }

}
