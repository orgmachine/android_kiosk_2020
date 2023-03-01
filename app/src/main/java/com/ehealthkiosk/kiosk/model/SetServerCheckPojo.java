package com.ehealthkiosk.kiosk.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SetServerCheckPojo {
    @SerializedName("app_version")
    @Expose
    private Integer app_version;

    public Integer getAppVersion() {
        return app_version;
    }

    public void setAppVersion(Integer app_version) {
        this.app_version = app_version;
    }
}
