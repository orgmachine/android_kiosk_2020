package com.ehealthkiosk.kiosk.model.commonresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Base<T> {
    @SerializedName("status")
    @Expose
    private Status status;
    @SerializedName("data")
    @Expose
    private T data;

    public Base(Status status, T data) {
        this.status = status;
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
