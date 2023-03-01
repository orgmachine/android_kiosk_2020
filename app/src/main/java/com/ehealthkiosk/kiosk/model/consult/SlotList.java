package com.ehealthkiosk.kiosk.model.consult;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SlotList {

    @SerializedName("slots")
    @Expose
    private List<Slot> slots;

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }
}
