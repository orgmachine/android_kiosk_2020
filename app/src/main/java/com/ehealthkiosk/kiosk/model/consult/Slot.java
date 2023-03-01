package com.ehealthkiosk.kiosk.model.consult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Slot {

    @SerializedName("group")
    @Expose
    private String group;
    @SerializedName("free")
    @Expose
    private List<Boolean> free;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<Boolean> getFree() {
        return free;
    }

    public void setFree(List<Boolean> free) {
        this.free = free;
    }
}
