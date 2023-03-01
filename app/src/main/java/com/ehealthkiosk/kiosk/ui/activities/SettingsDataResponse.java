package com.ehealthkiosk.kiosk.ui.activities;

import com.ehealthkiosk.kiosk.model.commonresponse.Status;

public class SettingsDataResponse {
    private Status status;
    private DeviceData data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public DeviceData getData() {
        return data;
    }

    public void setData(DeviceData deviceData) {
        this.data = deviceData;
    }
}
